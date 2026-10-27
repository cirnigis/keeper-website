
package vn.ubm.usedbookmarketplace.repository;

import java.util.List;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import vn.ubm.usedbookmarketplace.entity.UserAddress;

@Repository
public class UserAddressRepository {

	private final NamedParameterJdbcTemplate jdbcTemplate;

	public UserAddressRepository(NamedParameterJdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public List<UserAddress> findAllByUserId(Long userId) {
		String sql = """
				SELECT AddressId, UserId, RecipientName, Phone,
				       Province, District, Ward, AddressLine,
				       IsDefault, IsActive, CreatedAt
				FROM dbo.UserAddresses
				WHERE UserId = :userId
				  AND IsActive = 1
				ORDER BY IsDefault DESC, CreatedAt DESC, AddressId DESC
				""";

		MapSqlParameterSource params = new MapSqlParameterSource().addValue("userId", userId);

		return jdbcTemplate.query(sql, params, (rs, rowNum) -> {
			UserAddress address = new UserAddress();

			address.setAddressId(rs.getLong("AddressId"));
			address.setUserId(rs.getLong("UserId"));
			address.setRecipientName(rs.getString("RecipientName"));
			address.setPhone(rs.getString("Phone"));
			address.setProvince(rs.getString("Province"));
			address.setDistrict(rs.getString("District"));
			address.setWard(rs.getString("Ward"));
			address.setAddressLine(rs.getString("AddressLine"));
			address.setIsDefault(rs.getBoolean("IsDefault"));
			address.setIsActive(rs.getBoolean("IsActive"));

			return address;
		});
	}

	public boolean belongsToUser(Long addressId, Long userId) {
		String sql = """
				SELECT COUNT(1)
				FROM dbo.UserAddresses
				WHERE AddressId = :addressId
				  AND UserId = :userId
				  AND IsActive = 1
				""";

		MapSqlParameterSource params = new MapSqlParameterSource().addValue("addressId", addressId).addValue("userId",
				userId);

		Integer count = jdbcTemplate.queryForObject(sql, params, Integer.class);

		return count != null && count > 0;
	}

	@Transactional
	public Long insert(Long userId, UserAddress address) {
		String sql = """
				INSERT INTO dbo.UserAddresses (
				    UserId, RecipientName, Phone, Province,
				    District, Ward, AddressLine,
				    IsDefault, IsActive, CreatedAt
				)
				OUTPUT INSERTED.AddressId
				VALUES (
				    :userId, :recipientName, :phone, :province,
				    :district, :ward, :addressLine,
				    0, 1, SYSUTCDATETIME()
				)
				""";

		MapSqlParameterSource params = new MapSqlParameterSource().addValue("userId", userId)
				.addValue("recipientName", requireText(address.getRecipientName(), "Vui lòng nhập tên người nhận."))
				.addValue("phone", requireText(address.getPhone(), "Vui lòng nhập số điện thoại."))
				.addValue("province", requireText(address.getProvince(), "Vui lòng nhập tỉnh/thành phố."))
				.addValue("district", requireText(address.getDistrict(), "Vui lòng nhập quận/huyện."))
				.addValue("ward", requireText(address.getWard(), "Vui lòng nhập phường/xã."))
				.addValue("addressLine", requireText(address.getAddressLine(), "Vui lòng nhập địa chỉ cụ thể."));

		return jdbcTemplate.queryForObject(sql, params, Long.class);
	}

	public Long findDefaultAddressId(Long userId) {
		String sql = """
				SELECT TOP (1) AddressId
				FROM dbo.UserAddresses
				WHERE UserId = :userId
				  AND IsActive = 1
				  AND IsDefault = 1
				ORDER BY CreatedAt DESC, AddressId DESC
				""";

		MapSqlParameterSource params = new MapSqlParameterSource().addValue("userId", userId);

		List<Long> ids = jdbcTemplate.query(sql, params, (rs, rowNum) -> rs.getLong("AddressId"));

		return ids.isEmpty() ? null : ids.get(0);
	}

	@Transactional
	public void setDefault(Long addressId, Long userId) {
		if (!belongsToUser(addressId, userId)) {
			throw new IllegalArgumentException("Địa chỉ không tồn tại hoặc không thuộc người dùng.");
		}

		MapSqlParameterSource params = new MapSqlParameterSource().addValue("userId", userId).addValue("addressId",
				addressId);

		String clearSql = """
				UPDATE dbo.UserAddresses
				SET IsDefault = 0
				WHERE UserId = :userId
				  AND IsActive = 1
				""";

		jdbcTemplate.update(clearSql, params);

		String setSql = """
				UPDATE dbo.UserAddresses
				SET IsDefault = 1
				WHERE AddressId = :addressId
				  AND UserId = :userId
				  AND IsActive = 1
				""";

		int updated = jdbcTemplate.update(setSql, params);

		if (updated == 0) {
			throw new IllegalArgumentException("Không thể đặt địa chỉ mặc định.");
		}
	}

	@Transactional
	public void deleteAddress(Long addressId, Long userId) {
		String findSql = """
				SELECT IsDefault
				FROM dbo.UserAddresses
				WHERE AddressId = :addressId
				  AND UserId = :userId
				  AND IsActive = 1
				""";

		MapSqlParameterSource params = new MapSqlParameterSource().addValue("addressId", addressId).addValue("userId",
				userId);

		List<Boolean> defaults = jdbcTemplate.query(findSql, params, (rs, rowNum) -> rs.getBoolean("IsDefault"));

		if (defaults.isEmpty()) {
			throw new IllegalArgumentException("Địa chỉ không tồn tại hoặc không thuộc người dùng.");
		}

		boolean wasDefault = defaults.get(0);

		String deactivateSql = """
				UPDATE dbo.UserAddresses
				SET IsActive = 0, IsDefault = 0
				WHERE AddressId = :addressId
				  AND UserId = :userId
				  AND IsActive = 1
				""";

		jdbcTemplate.update(deactivateSql, params);

		if (!wasDefault) {
			return;
		}

		String promoteSql = """
				UPDATE dbo.UserAddresses
				SET IsDefault = 1
				WHERE AddressId = (
				    SELECT TOP (1) AddressId
				    FROM dbo.UserAddresses
				    WHERE UserId = :userId
				      AND IsActive = 1
				    ORDER BY CreatedAt DESC, AddressId DESC
				)
				  AND UserId = :userId
				  AND IsActive = 1
				""";

		jdbcTemplate.update(promoteSql, params);
	}

	private String requireText(String value, String message) {
		if (value == null || value.isBlank()) {
			throw new IllegalArgumentException(message);
		}

		return value.trim();
	}
}
