package vn.ubm.usedbookmarketplace.repository;

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class AdminManagerDashboardRepository {
	private final NamedParameterJdbcTemplate jdbc;

	public AdminManagerDashboardRepository(NamedParameterJdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	public Map<String, Object> getAdminSummary() {
		String sql = """
				SELECT
				  (SELECT COUNT_BIG(*) FROM dbo.Users) AS totalUsers,
				  (SELECT COUNT_BIG(*) FROM dbo.Users u JOIN dbo.UserRoles ur ON ur.UserId=u.UserId JOIN dbo.Roles r ON r.RoleId=ur.RoleId WHERE r.RoleCode='MANAGER') AS totalManagers,
				  (SELECT COUNT_BIG(*) FROM dbo.Users u JOIN dbo.UserRoles ur ON ur.UserId=u.UserId JOIN dbo.Roles r ON r.RoleId=ur.RoleId WHERE r.RoleCode='SELLER') AS totalSellers,
				  (SELECT COUNT_BIG(*) FROM dbo.Shops WHERE ApprovalStatus='PENDING') AS pendingShops,
				  (SELECT COUNT_BIG(*) FROM dbo.Shops) AS totalShops,
				  (SELECT COUNT_BIG(*) FROM dbo.ShopListings WHERE ListingStatus='ACTIVE') AS activeListings,
				  (SELECT COUNT_BIG(*) FROM dbo.Orders) AS totalOrders
				""";
		return jdbc.queryForMap(sql, Map.of());
	}

	public List<Map<String, Object>> findManagers() {
		String sql = """
				SELECT u.UserId AS userId, u.FullName AS fullName, u.Email AS email,
				       u.AccountStatus AS accountStatus,
				       COUNT(DISTINCT CASE WHEN sma.EndedAt IS NULL THEN sma.ShopId END) AS assignedShops
				FROM dbo.Users u
				JOIN dbo.UserRoles ur ON ur.UserId=u.UserId
				JOIN dbo.Roles r ON r.RoleId=ur.RoleId AND r.RoleCode='MANAGER'
				LEFT JOIN dbo.ShopManagerAssignments sma ON sma.ManagerUserId=u.UserId
				GROUP BY u.UserId, u.FullName, u.Email, u.AccountStatus
				ORDER BY u.FullName
				""";
		return jdbc.queryForList(sql, Map.of());
	}

	public Map<String, Object> getManagerSummary(String email) {
		String sql = """
				SELECT
				  COUNT(DISTINCT CASE WHEN sma.EndedAt IS NULL THEN s.ShopId END) AS assignedShops,
				  COUNT(DISTINCT CASE WHEN sma.EndedAt IS NULL AND s.ApprovalStatus='PENDING' THEN s.ShopId END) AS pendingShops,
				  COUNT(DISTINCT CASE WHEN sma.EndedAt IS NULL AND s.ApprovalStatus='APPROVED' AND s.IsActive=1 THEN s.ShopId END) AS approvedShops,
				  COUNT(DISTINCT CASE WHEN sma.EndedAt IS NULL THEN sl.ListingId END) AS assignedListings,
				  COUNT(DISTINCT CASE WHEN sma.EndedAt IS NULL THEN o.OrderId END) AS assignedOrders
				FROM dbo.Users u
				JOIN dbo.ShopManagerAssignments sma ON sma.ManagerUserId=u.UserId AND sma.EndedAt IS NULL
				JOIN dbo.Shops s ON s.ShopId=sma.ShopId
				LEFT JOIN dbo.ShopListings sl ON sl.ShopId=s.ShopId
				LEFT JOIN dbo.Orders o ON o.ShopId=s.ShopId
				WHERE LOWER(LTRIM(RTRIM(u.Email)))=LOWER(LTRIM(RTRIM(:email)))
				""";
		return jdbc.queryForMap(sql, Map.of("email", email));
	}

	public List<Map<String, Object>> findAssignedShops(String email) {
		String sql = """
				SELECT TOP (10) s.ShopId AS shopId, s.ShopName AS shopName,
				       s.ApprovalStatus AS approvalStatus, s.IsActive AS isActive,
				       u.FullName AS ownerName, s.CreatedAt AS createdAt
				FROM dbo.Users manager
				JOIN dbo.ShopManagerAssignments sma ON sma.ManagerUserId=manager.UserId AND sma.EndedAt IS NULL
				JOIN dbo.Shops s ON s.ShopId=sma.ShopId
				JOIN dbo.Users u ON u.UserId=s.OwnerUserId
				WHERE LOWER(LTRIM(RTRIM(manager.Email)))=LOWER(LTRIM(RTRIM(:email)))
				ORDER BY s.CreatedAt DESC, s.ShopId DESC
				""";
		return jdbc.queryForList(sql, Map.of("email", email));
	}
}
