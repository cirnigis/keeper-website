package vn.ubm.usedbookmarketplace.repository;

import java.util.List;
import java.util.Map;
import java.util.Locale;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class AdminManagerManagementRepository {
	private final NamedParameterJdbcTemplate jdbc;

	public AdminManagerManagementRepository(NamedParameterJdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	public List<Map<String, Object>> findUsers() {
		return jdbc.queryForList(
				"""
						SELECT u.UserId AS userId, u.FullName AS fullName, u.Email AS email,
						       u.AccountStatus AS accountStatus,
						       CAST(CASE WHEN EXISTS (
						         SELECT 1 FROM dbo.UserRoles ur JOIN dbo.Roles r ON r.RoleId=ur.RoleId
						         WHERE ur.UserId=u.UserId AND r.RoleCode='MANAGER'
						       ) THEN 1 ELSE 0 END AS bit) AS isManager
						FROM dbo.Users u
						ORDER BY CASE WHEN EXISTS (SELECT 1 FROM dbo.UserRoles ur JOIN dbo.Roles r ON r.RoleId=ur.RoleId WHERE ur.UserId=u.UserId AND r.RoleCode='MANAGER') THEN 0 ELSE 1 END,
						         u.FullName
						""",
				Map.of());
	}

	@Transactional
	public void changeManagerRole(String adminEmail, long targetUserId, boolean grant) {
		Long actor = jdbc.queryForObject(
				"SELECT UserId FROM dbo.Users WHERE LOWER(LTRIM(RTRIM(Email)))=LOWER(LTRIM(RTRIM(:email)))",
				Map.of("email", adminEmail), Long.class);
		if (actor == null)
			throw new IllegalArgumentException("Không xác định được tài khoản Admin.");
		if (actor.longValue() == targetUserId)
			throw new IllegalArgumentException("Không thể tự thay đổi quyền Manager của chính mình.");
		MapSqlParameterSource p = new MapSqlParameterSource().addValue("target", targetUserId).addValue("actor", actor);
		List<Map<String, Object>> users = jdbc.queryForList(
				"SELECT UserId, AccountStatus FROM dbo.Users WITH (UPDLOCK, ROWLOCK) WHERE UserId=:target", p);
		if (users.isEmpty())
			throw new IllegalArgumentException("Không tìm thấy tài khoản.");
		String status = String.valueOf(users.get(0).get("AccountStatus"));
		if (!"ACTIVE".equalsIgnoreCase(status))
			throw new IllegalArgumentException("Chỉ tài khoản ACTIVE mới được cấp hoặc thu hồi quyền Manager.");
		Integer roleId = jdbc.queryForObject("SELECT RoleId FROM dbo.Roles WHERE RoleCode='MANAGER'", Map.of(),
				Integer.class);
		if (roleId == null)
			throw new IllegalStateException("Không tìm thấy role MANAGER trong bảng Roles.");
		p.addValue("roleId", roleId);
		Integer exists = jdbc.queryForObject(
				"SELECT COUNT(1) FROM dbo.UserRoles WHERE UserId=:target AND RoleId=:roleId", p, Integer.class);
		if (grant) {
			if (exists == null || exists == 0) {
				jdbc.update(
						"INSERT INTO dbo.UserRoles(UserId,RoleId,AssignedAt,AssignedByUserId) VALUES(:target,:roleId,SYSDATETIME(),:actor)",
						p);
			}
		} else if (exists != null && exists > 0) {
			jdbc.update("DELETE FROM dbo.UserRoles WHERE UserId=:target AND RoleId=:roleId", p);
			jdbc.update("DELETE FROM dbo.ShopManagerAssignments WHERE ManagerUserId=:target AND EndedAt IS NULL", p);
		}
	}
}
