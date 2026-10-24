
package vn.ubm.usedbookmarketplace.repository;

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import vn.ubm.usedbookmarketplace.dto.CartItemView;

@Repository
public class CartRepository {

	private final NamedParameterJdbcTemplate jdbc;

	public CartRepository(NamedParameterJdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	public Long findCartIdByUserId(Long userId) {
		List<Long> ids = jdbc.query("""
				SELECT CartId
				FROM dbo.Carts
				WHERE UserId = :userId
				""", Map.of("userId", userId), (rs, rowNum) -> rs.getLong("CartId"));

		return ids.isEmpty() ? null : ids.get(0);
	}

	public Long createCart(Long userId) {
		jdbc.update("""
				IF NOT EXISTS (
				    SELECT 1 FROM dbo.Carts WHERE UserId = :userId
				)
				BEGIN
				    INSERT INTO dbo.Carts (UserId)
				    VALUES (:userId)
				END
				""", Map.of("userId", userId));

		return findCartIdByUserId(userId);
	}

	public Map<String, Object> findAvailableListing(Long listingId) {
		List<Map<String, Object>> rows = jdbc.queryForList("""
				SELECT
				    sl.ListingId AS listingId,
				    sl.StockQuantity AS stockQuantity
				FROM dbo.ShopListings sl
				INNER JOIN dbo.Books b ON b.BookId = sl.BookId
				INNER JOIN dbo.Categories c ON c.CategoryId = b.CategoryId
				INNER JOIN dbo.Shops s ON s.ShopId = sl.ShopId
				WHERE sl.ListingId = :listingId
				  AND sl.ListingStatus = 'ACTIVE'
				  AND sl.StockQuantity > 0
				  AND b.IsActive = 1
				  AND c.IsActive = 1
				  AND s.IsActive = 1
				  AND s.ApprovalStatus = 'APPROVED'
				""", Map.of("listingId", listingId));

		return rows.isEmpty() ? null : rows.get(0);
	}

	public Integer findCartItemQuantity(Long cartId, Long listingId) {
		List<Integer> quantities = jdbc.query("""
				SELECT Quantity
				FROM dbo.CartItems
				WHERE CartId = :cartId
				  AND ListingId = :listingId
				""", new MapSqlParameterSource().addValue("cartId", cartId).addValue("listingId", listingId),
				(rs, rowNum) -> rs.getInt("Quantity"));

		return quantities.isEmpty() ? null : quantities.get(0);
	}

	public void insertItem(Long cartId, Long listingId, int quantity) {
		jdbc.update("""
				INSERT INTO dbo.CartItems (CartId, ListingId, Quantity)
				VALUES (:cartId, :listingId, :quantity)
				""", new MapSqlParameterSource().addValue("cartId", cartId).addValue("listingId", listingId)
				.addValue("quantity", quantity));
	}

	public void updateItemQuantity(Long cartId, Long listingId, int quantity) {

		jdbc.update("""
				UPDATE dbo.CartItems
				SET Quantity = :quantity
				WHERE CartId = :cartId
				  AND ListingId = :listingId
				""", new MapSqlParameterSource().addValue("cartId", cartId).addValue("listingId", listingId)
				.addValue("quantity", quantity));
	}

	public List<CartItemView> findCartItems(Long cartId) {
		return jdbc.query("""
				SELECT
				    ci.CartItemId AS cartItemId,
				    sl.ListingId AS listingId,
				    COALESCE(NULLIF(sl.ListingTitle, ''), b.Title) AS title,
				    COALESCE(
				        (
				            SELECT TOP 1 li.ImageUrl
				            FROM dbo.ListingImages li
				            WHERE li.ListingId = sl.ListingId
				            ORDER BY
				                li.IsPrimary DESC,
				                li.SortOrder ASC,
				                li.ListingImageId ASC
				        ),
				        b.CoverUrl
				    ) AS coverUrl,
				    sl.ConditionCode AS conditionCode,
				    s.ShopName AS shopName,
				    sl.UnitPrice AS unitPrice,
				    ci.Quantity AS quantity,
				    sl.StockQuantity AS stockQuantity
				FROM dbo.CartItems ci
				INNER JOIN dbo.ShopListings sl
				    ON sl.ListingId = ci.ListingId
				INNER JOIN dbo.Books b
				    ON b.BookId = sl.BookId
				INNER JOIN dbo.Categories c
				    ON c.CategoryId = b.CategoryId
				INNER JOIN dbo.Shops s
				    ON s.ShopId = sl.ShopId
				WHERE ci.CartId = :cartId
				  AND sl.ListingStatus = 'ACTIVE'
				  AND sl.StockQuantity > 0
				  AND b.IsActive = 1
				  AND c.IsActive = 1
				  AND s.IsActive = 1
				  AND s.ApprovalStatus = 'APPROVED'
				ORDER BY ci.AddedAt DESC
				""", Map.of("cartId", cartId), (rs, rowNum) -> {
			CartItemView item = new CartItemView();
			item.setCartItemId(rs.getLong("cartItemId"));
			item.setListingId(rs.getLong("listingId"));
			item.setTitle(rs.getString("title"));
			item.setCoverUrl(rs.getString("coverUrl"));
			item.setConditionCode(rs.getString("conditionCode"));
			item.setShopName(rs.getString("shopName"));
			item.setUnitPrice(rs.getBigDecimal("unitPrice"));
			item.setQuantity(rs.getInt("quantity"));
			item.setStockQuantity(rs.getInt("stockQuantity"));
			return item;
		});
	}

	public int updateQuantityByItemId(Long cartId, Long cartItemId, int quantity) {

		return jdbc.update("""
				UPDATE dbo.CartItems
				SET Quantity = :quantity
				WHERE CartId = :cartId
				  AND CartItemId = :cartItemId
				""", new MapSqlParameterSource().addValue("cartId", cartId).addValue("cartItemId", cartItemId)
				.addValue("quantity", quantity));
	}

	public int deleteItem(Long cartId, Long cartItemId) {
		return jdbc.update("""
				DELETE FROM dbo.CartItems
				WHERE CartId = :cartId
				  AND CartItemId = :cartItemId
				""", new MapSqlParameterSource().addValue("cartId", cartId).addValue("cartItemId", cartItemId));
	}

	public void updateCartTimestamp(Long cartId) {
		jdbc.update("""
				UPDATE dbo.Carts
				SET UpdatedAt = SYSUTCDATETIME()
				WHERE CartId = :cartId
				""", Map.of("cartId", cartId));
	}

	public void removeUnavailableItems(Long cartId) {
		jdbc.update("""
				DELETE ci
				FROM dbo.CartItems ci
				INNER JOIN dbo.ShopListings sl
				    ON sl.ListingId = ci.ListingId
				INNER JOIN dbo.Books b
				    ON b.BookId = sl.BookId
				INNER JOIN dbo.Categories c
				    ON c.CategoryId = b.CategoryId
				INNER JOIN dbo.Shops s
				    ON s.ShopId = sl.ShopId
				WHERE ci.CartId = :cartId
				  AND (
				        sl.ListingStatus <> 'ACTIVE'
				        OR sl.StockQuantity <= 0
				        OR b.IsActive = 0
				        OR c.IsActive = 0
				        OR s.IsActive = 0
				        OR s.ApprovalStatus <> 'APPROVED'
				  )
				""", Map.of("cartId", cartId));
	}
}
