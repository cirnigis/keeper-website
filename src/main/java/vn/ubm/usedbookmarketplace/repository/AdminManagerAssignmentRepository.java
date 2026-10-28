package vn.ubm.usedbookmarketplace.repository;

import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class AdminManagerAssignmentRepository {
	private final NamedParameterJdbcTemplate jdbc;

	public AdminManagerAssignmentRepository(NamedParameterJdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	public List<Map<String, Object>> findActiveManagers() {
		return jdbc.queryForList("""
				SELECT u.UserId AS userId, u.FullName AS fullName, u.Email AS email
				FROM dbo.Users u JOIN dbo.UserRoles ur ON ur.UserId=u.UserId
				JOIN dbo.Roles r ON r.RoleId=ur.RoleId
				WHERE r.RoleCode='MANAGER' AND u.AccountStatus='ACTIVE'
				ORDER BY u.FullName
				""", Map.of());
	}

	public List<Map<String, Object>> findEligibleShops() {
		return jdbc.queryForList(
				"SELECT ShopId AS shopId, ShopName AS shopName FROM dbo.Shops WHERE ApprovalStatus='APPROVED' AND IsActive=1 ORDER BY ShopName",
				Map.of());
	}

	public List<Map<String, Object>> findAssignments() {
		return jdbc.queryForList(
				"""
						SELECT sma.ShopId AS shopId, s.ShopName AS shopName, sma.ManagerUserId AS managerUserId,
						       u.FullName AS managerName, u.Email AS managerEmail, sma.AssignedAt AS assignedAt, sma.Note AS note
						FROM dbo.ShopManagerAssignments sma JOIN dbo.Shops s ON s.ShopId=sma.ShopId
						JOIN dbo.Users u ON u.UserId=sma.ManagerUserId
						WHERE sma.EndedAt IS NULL ORDER BY s.ShopName, u.FullName
						""",
				Map.of());
	}

	@Transactional
	public void assign(String adminEmail, long managerId, long shopId, String note) {
		var p = new MapSqlParameterSource().addValue("manager", managerId).addValue("shop", shopId)
				.addValue("note", note == null || note.isBlank() ? null : note.trim())
				.addValue("adminEmail", adminEmail);
		List<Map<String, Object>> managers = jdbc.queryForList("""
				SELECT u.UserId FROM dbo.Users u JOIN dbo.UserRoles ur ON ur.UserId=u.UserId
				JOIN dbo.Roles r ON r.RoleId=ur.RoleId
				WHERE u.UserId=:manager AND u.AccountStatus='ACTIVE' AND r.RoleCode='MANAGER'
				""", p);
		if (managers.isEmpty())
			throw new IllegalArgumentException("Tài khoản được chọn không có quyền Manager hoặc không ACTIVE.");
		List<Map<String, Object>> shops = jdbc.queryForList(
				"SELECT ShopId FROM dbo.Shops WITH (UPDLOCK, ROWLOCK) WHERE ShopId=:shop AND ApprovalStatus='APPROVED' AND IsActive=1",
				p);
		if (shops.isEmpty())
			throw new IllegalArgumentException("Chỉ được phân công cho cửa hàng đã duyệt và đang hoạt động.");
		Integer exists = jdbc.queryForObject(
				"SELECT COUNT(1) FROM dbo.ShopManagerAssignments WHERE ShopId=:shop AND ManagerUserId=:manager AND EndedAt IS NULL",
				p, Integer.class);
		if (exists != null && exists > 0)
			throw new IllegalStateException("Manager này đã được phân công cho cửa hàng.");
		Long actor = jdbc.queryForObject(
				"SELECT UserId FROM dbo.Users WHERE LOWER(LTRIM(RTRIM(Email)))=LOWER(LTRIM(RTRIM(:adminEmail)))", p,
				Long.class);
		if (actor == null)
			throw new IllegalStateException("Không xác định được tài khoản Admin.");
		p.addValue("actor", actor);
		jdbc.update(
				"INSERT INTO dbo.ShopManagerAssignments (ShopId, ManagerUserId, AssignedByUserId, AssignedAt, EndedAt, Note) VALUES (:shop,:manager,:actor,SYSDATETIME(),NULL,:note)",
				p);
	}

	@Transactional
	public void endAssignment(long managerId, long shopId) {
		int changed = jdbc.update(
				"UPDATE dbo.ShopManagerAssignments SET EndedAt=SYSDATETIME(), Note=CASE WHEN NULLIF(LTRIM(RTRIM(Note)),'') IS NULL THEN N'Kết thúc phân công bởi Admin' ELSE Note + N' | Kết thúc phân công bởi Admin' END WHERE ManagerUserId=:manager AND ShopId=:shop AND EndedAt IS NULL",
				new MapSqlParameterSource().addValue("manager", managerId).addValue("shop", shopId));
		if (changed == 0)
			throw new IllegalArgumentException("Không tìm thấy phân công đang hoạt động.");
	}
}
