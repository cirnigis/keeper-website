package vn.ubm.usedbookmarketplace.repository;

import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class AdminShopManagementRepository {
	private final NamedParameterJdbcTemplate jdbc;

	public AdminShopManagementRepository(NamedParameterJdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	public List<Map<String, Object>> findAll() {
		return jdbc.queryForList("""
				SELECT s.ShopId AS shopId, s.ShopName AS shopName, s.ApprovalStatus AS approvalStatus,
				       s.IsActive AS isActive, s.UpdatedAt AS updatedAt, u.FullName AS ownerName,
				       u.Email AS ownerEmail
				FROM dbo.Shops s JOIN dbo.Users u ON u.UserId=s.OwnerUserId
				ORDER BY CASE WHEN s.IsActive=0 THEN 0 ELSE 1 END, s.ShopName
				""", Map.of());
	}

	@Transactional
	public void changeStatus(String adminEmail, long shopId, String action, String reason) {
		if (!List.of("SUSPEND", "REACTIVATE", "CLOSE").contains(action))
			throw new IllegalArgumentException("Thao tác không hợp lệ.");
		if (reason == null || reason.trim().length() < 8 || reason.trim().length() > 1000)
			throw new IllegalArgumentException("Lý do phải từ 8 đến 1.000 ký tự.");
		Long actor = jdbc.queryForObject(
				"SELECT UserId FROM dbo.Users WHERE LOWER(LTRIM(RTRIM(Email)))=LOWER(LTRIM(RTRIM(:email)))",
				Map.of("email", adminEmail), Long.class);
		var p = new MapSqlParameterSource().addValue("id", shopId).addValue("actor", actor).addValue("reason",
				reason.trim());
		List<Map<String, Object>> current = jdbc.queryForList(
				"SELECT ApprovalStatus, IsActive FROM dbo.Shops WITH (UPDLOCK, ROWLOCK) WHERE ShopId=:id", p);
		if (current.isEmpty())
			throw new IllegalArgumentException("Không tìm thấy cửa hàng.");
		String old = String.valueOf(current.get(0).get("ApprovalStatus"));
		boolean active = Boolean.TRUE.equals(current.get(0).get("IsActive"));
		if ("APPROVED".equalsIgnoreCase(old) && !active)
			old = "SUSPENDED";
		if ("CLOSED".equalsIgnoreCase(old))
			throw new IllegalArgumentException("Cửa hàng đã đóng vĩnh viễn, không thể thay đổi.");
		String next;
		boolean isActive;
		switch (action) {
		case "SUSPEND" -> {
			if (!"APPROVED".equalsIgnoreCase(old) && !"SUSPENDED".equalsIgnoreCase(old))
				throw new IllegalArgumentException("Chỉ có thể tạm khóa cửa hàng đã được duyệt.");
			next = "SUSPENDED";
			isActive = false;
		}
		case "REACTIVATE" -> {
			if (!"SUSPENDED".equalsIgnoreCase(old))
				throw new IllegalArgumentException("Chỉ có thể mở lại cửa hàng đang tạm khóa.");
			next = "APPROVED";
			isActive = true;
		}
		default -> {
			if (!List.of("APPROVED", "SUSPENDED", "REJECTED").contains(old.toUpperCase()))
				throw new IllegalArgumentException("Trạng thái hiện tại không cho phép đóng cửa hàng.");
			next = "CLOSED";
			isActive = false;
		}
		}
		p.addValue("next", next).addValue("active", isActive).addValue("old", old);
		jdbc.update(
				"UPDATE dbo.Shops SET ApprovalStatus=:next, IsActive=:active, UpdatedAt=SYSDATETIME() WHERE ShopId=:id",
				p);
		jdbc.update(
				"INSERT INTO dbo.ShopApprovalHistories (ShopId, ActorUserId, PreviousStatus, NewStatus, Note, CreatedAt) VALUES (:id,:actor,:old,:next,:reason,SYSDATETIME())",
				p);
	}
}
