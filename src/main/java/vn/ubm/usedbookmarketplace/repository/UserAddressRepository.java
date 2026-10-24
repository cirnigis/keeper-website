package vn.ubm.usedbookmarketplace.repository;

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import vn.ubm.usedbookmarketplace.dto.UserAddress;

@Repository
public class UserAddressRepository {

	private final NamedParameterJdbcTemplate jdbcTemplate;

	public UserAddressRepository(NamedParameterJdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public List<UserAddress> findAllByUserId(Long userId) {
		String sql = """
				SELECT AddressId, RecipientName, Phone, Province,
				       District, Ward, AddressLine, IsDefault
				FROM dbo.UserAddresses
				WHERE UserId = :userId AND IsActive = 1
				ORDER BY IsDefault DESC, AddressId DESC
				""";

		return jdbcTemplate.query(sql, Map.of("userId", userId), (rs, rowNum) -> {
			UserAddress address = new UserAddress();
			address.setAddressId(rs.getLong("AddressId"));
			address.setRecipientName(rs.getString("RecipientName"));
			address.setPhone(rs.getString("Phone"));
			address.setProvince(rs.getString("Province"));
			address.setDistrict(rs.getString("District"));
			address.setWard(rs.getString("Ward"));
			address.setAddressLine(rs.getString("AddressLine"));
			address.setDefault(rs.getBoolean("IsDefault"));
			return address;
		});
	}

	public boolean belongsToUser(Long addressId, Long userId) {
		String sql = """
				SELECT COUNT(*)
				FROM dbo.UserAddresses
				WHERE AddressId = :addressId
				  AND UserId = :userId
				  AND IsActive = 1
				""";

		Integer count = jdbcTemplate.queryForObject(sql, Map.of("addressId", addressId, "userId", userId),
				Integer.class);

		return count != null && count > 0;
	}

	@Transactional
	public void insert(Long userId, UserAddress address) {
		String sql = """
				INSERT INTO dbo.UserAddresses
				    (UserId, RecipientName, Phone, Province, District,
				     Ward, AddressLine, IsDefault, IsActive)
				VALUES
				    (:userId, :recipientName, :phone, :province, :district,
				     :ward, :addressLine, 0, 1)
				""";

		MapSqlParameterSource params = new MapSqlParameterSource().addValue("userId", userId)
				.addValue("recipientName", address.getRecipientName().trim())
				.addValue("phone", address.getPhone().trim()).addValue("province", address.getProvince().trim())
				.addValue("district", address.getDistrict().trim()).addValue("ward", address.getWard().trim())
				.addValue("addressLine", address.getAddressLine().trim());

		jdbcTemplate.update(sql, params);
	}

	@Transactional
	public void setDefault(Long addressId, Long userId) {
		// First remove the default flag from this user's active addresses.
		String clearSql = """
				UPDATE dbo.UserAddresses
				SET IsDefault = 0
				WHERE UserId = :userId AND IsActive = 1
				""";

		jdbcTemplate.update(clearSql, Map.of("userId", userId));

		// Then set the selected address as default, if it belongs to this user.
		String setSql = """
				UPDATE dbo.UserAddresses
				SET IsDefault = 1
				WHERE AddressId = :addressId
				  AND UserId = :userId
				  AND IsActive = 1
				""";

		int updated = jdbcTemplate.update(setSql, Map.of("addressId", addressId, "userId", userId));

		if (updated == 0) {
			throw new IllegalArgumentException("Address was not found.");
		}
	}

	public void deactivate(Long addressId, Long userId) {
		String sql = """
				UPDATE dbo.UserAddresses
				SET IsActive = 0, IsDefault = 0
				WHERE AddressId = :addressId
				  AND UserId = :userId
				  AND IsActive = 1
				""";

		jdbcTemplate.update(sql, Map.of("addressId", addressId, "userId", userId));
	}

	public boolean hasOtherActiveAddress(Long userId, Long excludedAddressId) {
		String sql = """
				SELECT COUNT(*)
				FROM dbo.UserAddresses
				WHERE UserId = :userId
				  AND IsActive = 1
				  AND AddressId <> :addressId
				""";

		Integer count = jdbcTemplate.queryForObject(sql, Map.of("userId", userId, "addressId", excludedAddressId),
				Integer.class);

		return count != null && count > 0;
	}

	public Long findDefaultAddressId(Long userId) {
		String sql = """
				SELECT TOP 1 AddressId
				FROM dbo.UserAddresses
				WHERE UserId = :userId AND IsActive = 1 AND IsDefault = 1
				ORDER BY AddressId DESC
				""";

		List<Long> ids = jdbcTemplate.query(sql, Map.of("userId", userId), (rs, rowNum) -> rs.getLong("AddressId"));

		return ids.isEmpty() ? null : ids.get(0);
	}

	@Transactional
	public void deleteAddress(Long addressId, Long userId) {
		deactivate(addressId, userId);

		// If the deleted address was the default, promote another active address.
		String promoteSql = """
				UPDATE dbo.UserAddresses
				SET IsDefault = 1
				WHERE AddressId = (
				    SELECT TOP 1 AddressId
				    FROM dbo.UserAddresses
				    WHERE UserId = :userId AND IsActive = 1
				    ORDER BY AddressId DESC
				)
				AND NOT EXISTS (
				    SELECT 1 FROM dbo.UserAddresses
				    WHERE UserId = :userId AND IsActive = 1 AND IsDefault = 1
				)
				""";

		jdbcTemplate.update(promoteSql, Map.of("userId", userId));
	}
}