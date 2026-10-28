package vn.ubm.usedbookmarketplace.repository;

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class ManagerShopApprovalRepository {
	private final NamedParameterJdbcTemplate jdbc;

	public ManagerShopApprovalRepository(NamedParameterJdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	public List<Map<String, Object>> findPendingShops(String actorEmail, boolean admin) {
		String scope = admin ? ""
				: "AND EXISTS (SELECT 1 FROM dbo.Users mu JOIN dbo.ShopManagerAssignments sma ON sma.ManagerUserId=mu.UserId AND sma.EndedAt IS NULL WHERE LOWER(LTRIM(RTRIM(mu.Email)))=LOWER(LTRIM(RTRIM(:email))) AND sma.ShopId=s.ShopId)";
		String sql = """
				SELECT s.ShopId AS shopId, s.ShopName AS shopName, s.ShopSlug AS shopSlug,
				       s.OwnerType AS ownerType, s.Description AS description,
				       s.ContactPhone AS contactPhone, s.ContactEmail AS contactEmail,
				       s.Province AS province, s.District AS district, s.AddressLine AS addressLine,
				       s.CreatedAt AS createdAt, u.UserId AS ownerUserId,
				       u.FullName AS ownerName, u.Email AS ownerEmail, u.CCCD AS cccd
				FROM dbo.Shops s
				JOIN dbo.Users u ON u.UserId=s.OwnerUserId
				WHERE s.ApprovalStatus='PENDING' AND s.IsActive=1
				""" + scope + " ORDER BY s.CreatedAt ASC, s.ShopId ASC";
		return jdbc.queryForList(sql, new MapSqlParameterSource("email", actorEmail));
	}

	@Transactional
	public void reviewShop(String actorEmail, boolean admin, long shopId, String decision, String note) {
		if (shopId <= 0)
			throw new IllegalArgumentException("Shop không hợp lệ.");
		if (!"APPROVED".equals(decision) && !"REJECTED".equals(decision)) {
			throw new IllegalArgumentException("Quyết định duyệt không hợp lệ.");
		}
		if (note == null || note.isBlank())
			throw new IllegalArgumentException("Vui lòng nhập ghi chú xét duyệt.");
		if (note.trim().length() > 1000)
			throw new IllegalArgumentException("Ghi chú không được vượt quá 1000 ký tự.");

		MapSqlParameterSource params = new MapSqlParameterSource().addValue("shopId", shopId).addValue("email",
				actorEmail);
		Map<String, Object> shop;
		try {
			shop = jdbc.queryForMap(
					"SELECT ShopId AS shopId, OwnerUserId AS ownerUserId, ApprovalStatus AS approvalStatus FROM dbo.Shops WITH (UPDLOCK, ROWLOCK) WHERE ShopId=:shopId",
					params);
		} catch (org.springframework.dao.EmptyResultDataAccessException ex) {
			throw new IllegalArgumentException("Không tìm thấy shop.");
		}
		if (!"PENDING".equalsIgnoreCase(String.valueOf(shop.get("approvalStatus")))) {
			throw new IllegalArgumentException("Shop này đã được xử lý trước đó.");
		}
		if (!admin) {
			Integer assigned = jdbc.queryForObject("""
					SELECT COUNT(1) FROM dbo.Users u
					JOIN dbo.ShopManagerAssignments sma ON sma.ManagerUserId=u.UserId AND sma.EndedAt IS NULL
					WHERE LOWER(LTRIM(RTRIM(u.Email)))=LOWER(LTRIM(RTRIM(:email))) AND sma.ShopId=:shopId
					""", params, Integer.class);
			if (assigned == null || assigned == 0)
				throw new IllegalArgumentException("Shop này không thuộc phạm vi được phân công cho bạn.");
		}

		int updated = jdbc.update(
				"UPDATE dbo.Shops SET ApprovalStatus=:decision, UpdatedAt=SYSUTCDATETIME() WHERE ShopId=:shopId AND ApprovalStatus='PENDING'",
				new MapSqlParameterSource().addValue("decision", decision).addValue("shopId", shopId));
		if (updated != 1)
			throw new IllegalArgumentException("Shop vừa được xử lý bởi người khác. Vui lòng tải lại trang.");

		Long actorId = jdbc.queryForObject(
				"SELECT UserId FROM dbo.Users WHERE LOWER(LTRIM(RTRIM(Email)))=LOWER(LTRIM(RTRIM(:email)))", params,
				Long.class);
		if ("APPROVED".equals(decision)) {
			jdbc.update(
					"""
							INSERT INTO dbo.UserRoles (UserId, RoleId, AssignedAt, AssignedByUserId)
							SELECT :ownerUserId, r.RoleId, SYSUTCDATETIME(), :actorId
							FROM dbo.Roles r
							WHERE r.RoleCode='SELLER'
							  AND NOT EXISTS (SELECT 1 FROM dbo.UserRoles ur WHERE ur.UserId=:ownerUserId AND ur.RoleId=r.RoleId)
							""",
					new MapSqlParameterSource().addValue("ownerUserId", ((Number) shop.get("ownerUserId")).longValue())
							.addValue("actorId", actorId));
		}
		jdbc.update("""
				INSERT INTO dbo.ShopApprovalHistories (ShopId, ActorUserId, PreviousStatus, NewStatus, Note, CreatedAt)
				VALUES (:shopId, :actorId, 'PENDING', :decision, :note, SYSUTCDATETIME())
				""", new MapSqlParameterSource().addValue("shopId", shopId).addValue("actorId", actorId)
				.addValue("decision", decision).addValue("note", note.trim()));
	}
}
