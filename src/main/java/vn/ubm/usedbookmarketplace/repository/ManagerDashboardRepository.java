
package vn.ubm.usedbookmarketplace.repository;

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ManagerDashboardRepository {

	private final NamedParameterJdbcTemplate jdbc;

	public ManagerDashboardRepository(NamedParameterJdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	public List<Map<String, Object>> findAssignedShops(long managerUserId) {

		String sql = """
				SELECT
				    s.ShopId AS shopId,
				    s.ShopName AS shopName,
				    s.ApprovalStatus AS approvalStatus,
				    s.IsActive AS isActive,
				    s.Province AS province,
				    s.District AS district,
				    s.CreatedAt AS createdAt
				FROM dbo.ShopManagerAssignments a
				INNER JOIN dbo.Shops s
				    ON s.ShopId = a.ShopId
				WHERE a.ManagerUserId = :managerUserId
				  AND a.EndedAt IS NULL
				ORDER BY s.CreatedAt DESC
				""";

		return jdbc.queryForList(sql, new MapSqlParameterSource("managerUserId", managerUserId));
	}
}
