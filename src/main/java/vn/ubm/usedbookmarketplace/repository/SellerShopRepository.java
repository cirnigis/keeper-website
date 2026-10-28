
package vn.ubm.usedbookmarketplace.repository;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Repository
public class SellerShopRepository {

	private final NamedParameterJdbcTemplate jdbc;

	public SellerShopRepository(NamedParameterJdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	public List<Map<String, Object>> findByOwnerEmail(String email) {
		String sql = """
				SELECT
				    s.ShopId AS shopId,
				    s.ShopName AS shopName,
				    s.ShopSlug AS shopSlug,
				    s.OwnerType AS ownerType,
				    s.Description AS description,
				    s.LogoUrl AS logoUrl,
				    s.ContactPhone AS contactPhone,
				    s.ContactEmail AS contactEmail,
				    s.Province AS province,
				    s.District AS district,
				    s.AddressLine AS addressLine,
				    s.ApprovalStatus AS approvalStatus,
				    s.IsActive AS isActive,
				    s.CreatedAt AS createdAt,
				    s.UpdatedAt AS updatedAt
				FROM dbo.Shops s
				INNER JOIN dbo.Users u ON u.UserId = s.OwnerUserId
				WHERE LOWER(LTRIM(RTRIM(u.Email))) = LOWER(LTRIM(RTRIM(:email)))
				ORDER BY s.CreatedAt DESC, s.ShopId DESC
				""";

		return jdbc.queryForList(sql, new MapSqlParameterSource("email", email));
	}

	public List<Map<String, Object>> findApprovedActiveShopsByOwnerEmail(String email) {
		String sql = """
				SELECT
				    s.ShopId AS shopId,
				    s.ShopName AS shopName,
				    s.ShopSlug AS shopSlug,
				    s.OwnerType AS ownerType,
				    s.Description AS description,
				    s.LogoUrl AS logoUrl,
				    s.ContactPhone AS contactPhone,
				    s.ContactEmail AS contactEmail,
				    s.Province AS province,
				    s.District AS district,
				    s.AddressLine AS addressLine,
				    s.ApprovalStatus AS approvalStatus,
				    s.IsActive AS isActive,
				    s.CreatedAt AS createdAt,
				    s.UpdatedAt AS updatedAt
				FROM dbo.Shops s
				INNER JOIN dbo.Users u ON u.UserId = s.OwnerUserId
				WHERE LOWER(LTRIM(RTRIM(u.Email))) = LOWER(LTRIM(RTRIM(:email)))
				  AND s.ApprovalStatus = 'APPROVED'
				  AND s.IsActive = 1
				ORDER BY s.CreatedAt DESC, s.ShopId DESC
				""";

		return jdbc.queryForList(sql, new MapSqlParameterSource("email", email));
	}

	public Long findUserIdByEmail(String email) {
		String sql = """
				SELECT UserId
				FROM dbo.Users
				WHERE LOWER(LTRIM(RTRIM(Email))) = LOWER(LTRIM(RTRIM(:email)))
				""";

		try {
			return jdbc.queryForObject(sql, new MapSqlParameterSource("email", email), Long.class);
		} catch (EmptyResultDataAccessException ex) {
			throw new IllegalArgumentException("Không tìm thấy tài khoản người dùng.");
		}
	}

	public boolean existsShopSlug(String slug) {
		String sql = """
				SELECT COUNT(1)
				FROM dbo.Shops
				WHERE ShopSlug = :slug
				""";

		Integer count = jdbc.queryForObject(sql, new MapSqlParameterSource("slug", slug), Integer.class);

		return count != null && count > 0;
	}

	public boolean existsShopSlugForOtherShop(String slug, long shopId) {
		String sql = """
				SELECT COUNT(1)
				FROM dbo.Shops
				WHERE ShopSlug = :slug
				  AND ShopId <> :shopId
				""";

		Integer count = jdbc.queryForObject(sql,
				new MapSqlParameterSource().addValue("slug", slug).addValue("shopId", shopId), Integer.class);

		return count != null && count > 0;
	}

	public boolean isApprovedActiveShopOwner(String email, long shopId) {
		String sql = """
				SELECT COUNT(1)
				FROM dbo.Shops s
				INNER JOIN dbo.Users u ON u.UserId = s.OwnerUserId
				WHERE s.ShopId = :shopId
				  AND LOWER(LTRIM(RTRIM(u.Email))) = LOWER(LTRIM(RTRIM(:email)))
				  AND s.ApprovalStatus = 'APPROVED'
				  AND s.IsActive = 1
				""";

		Integer count = jdbc.queryForObject(sql,
				new MapSqlParameterSource().addValue("shopId", shopId).addValue("email", email), Integer.class);

		return count != null && count > 0;
	}

	public void createShop(long ownerUserId, String shopName, String shopSlug, String ownerType, String description,
			String contactPhone, String contactEmail, String province, String district, String addressLine) {
		String sql = """
				INSERT INTO dbo.Shops (
				    OwnerUserId,
				    ShopName,
				    ShopSlug,
				    OwnerType,
				    Description,
				    ContactPhone,
				    ContactEmail,
				    Province,
				    District,
				    AddressLine,
				    ApprovalStatus,
				    IsActive,
				    CreatedAt,
				    UpdatedAt
				)
				VALUES (
				    :ownerUserId,
				    :shopName,
				    :shopSlug,
				    :ownerType,
				    :description,
				    :contactPhone,
				    :contactEmail,
				    :province,
				    :district,
				    :addressLine,
				    'PENDING',
				    1,
				    SYSUTCDATETIME(),
				    SYSUTCDATETIME()
				)
				""";

		MapSqlParameterSource params = new MapSqlParameterSource().addValue("ownerUserId", ownerUserId)
				.addValue("shopName", shopName).addValue("shopSlug", shopSlug).addValue("ownerType", ownerType)
				.addValue("description", description).addValue("contactPhone", contactPhone)
				.addValue("contactEmail", contactEmail).addValue("province", province).addValue("district", district)
				.addValue("addressLine", addressLine);

		jdbc.update(sql, params);
	}

	public void updateShop(long shopId, long ownerUserId, String shopName, String ownerType, String description,
			String contactPhone, String contactEmail, String province, String district, String addressLine) {
		String sql = """
				UPDATE dbo.Shops
				SET ShopName = :shopName,
				    OwnerType = :ownerType,
				    Description = :description,
				    ContactPhone = :contactPhone,
				    ContactEmail = :contactEmail,
				    Province = :province,
				    District = :district,
				    AddressLine = :addressLine,
				    UpdatedAt = SYSUTCDATETIME()
				WHERE ShopId = :shopId
				  AND OwnerUserId = :ownerUserId
				  AND IsActive = 1
				  AND ApprovalStatus IN ('PENDING', 'APPROVED')
				""";

		MapSqlParameterSource params = new MapSqlParameterSource().addValue("shopId", shopId)
				.addValue("ownerUserId", ownerUserId).addValue("shopName", shopName).addValue("ownerType", ownerType)
				.addValue("description", description).addValue("contactPhone", contactPhone)
				.addValue("contactEmail", contactEmail).addValue("province", province).addValue("district", district)
				.addValue("addressLine", addressLine);

		int affected = jdbc.update(sql, params);

		if (affected != 1) {
			throw new IllegalArgumentException(
					"Không thể cập nhật shop. Shop không tồn tại, không thuộc tài khoản của bạn hoặc không ở trạng thái cho phép cập nhật.");
		}
	}

	public String findCccdByUserId(long userId) {
		String sql = """
				SELECT CCCD
				FROM dbo.Users
				WHERE UserId = :userId
				""";

		try {
			return jdbc.queryForObject(sql, new MapSqlParameterSource("userId", userId), String.class);
		} catch (EmptyResultDataAccessException ex) {
			throw new IllegalArgumentException("Không tìm thấy tài khoản người dùng.");
		}
	}

	public void updateUserCccd(long userId, String cccd) {
		String sql = """
				UPDATE dbo.Users
				SET CCCD = :cccd,
				    UpdatedAt = SYSUTCDATETIME()
				WHERE UserId = :userId
				""";

		int affected = jdbc.update(sql, new MapSqlParameterSource().addValue("userId", userId).addValue("cccd", cccd));

		if (affected != 1) {
			throw new IllegalArgumentException("Không thể cập nhật CCCD cho tài khoản người dùng.");
		}
	}
}
