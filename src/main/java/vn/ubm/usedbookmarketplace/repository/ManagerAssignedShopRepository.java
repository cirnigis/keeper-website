
package vn.ubm.usedbookmarketplace.repository;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class ManagerAssignedShopRepository {

	private final NamedParameterJdbcTemplate jdbc;

	public ManagerAssignedShopRepository(NamedParameterJdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	public List<Map<String, Object>> findAssignedShops(int managerUserId) {

		String sql = """
				SELECT
				    s.ShopId AS shopId,
				    s.ShopName AS shopName,
				    s.ApprovalStatus AS approvalStatus,
				    s.IsActive AS isActive,
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
