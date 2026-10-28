package vn.ubm.usedbookmarketplace.repository;

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class SellerDashboardRepository {

	private final NamedParameterJdbcTemplate jdbcTemplate;

	public SellerDashboardRepository(NamedParameterJdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public Map<String, Object> getShopSummary(String email) {
		String sql = """
				SELECT
				    COUNT_BIG(*) AS totalShops,
				    COALESCE(SUM(CASE WHEN s.ApprovalStatus = 'APPROVED' AND s.IsActive = 1 THEN 1 ELSE 0 END), 0) AS approvedShops,
				    COALESCE(SUM(CASE WHEN s.ApprovalStatus = 'PENDING' THEN 1 ELSE 0 END), 0) AS pendingShops,
				    COALESCE(SUM(CASE WHEN s.ApprovalStatus = 'REJECTED' THEN 1 ELSE 0 END), 0) AS rejectedShops
				FROM dbo.Shops s
				INNER JOIN dbo.Users u ON u.UserId = s.OwnerUserId
				WHERE LOWER(LTRIM(RTRIM(u.Email))) = LOWER(LTRIM(RTRIM(:email)))
				""";

		return jdbcTemplate.queryForMap(sql, Map.of("email", email));
	}

	public Map<String, Object> getListingSummary(String email) {
		String sql = """
				SELECT
				    COUNT_BIG(*) AS totalListings,
				    COALESCE(SUM(CASE WHEN sl.ListingStatus = 'ACTIVE' THEN 1 ELSE 0 END), 0) AS activeListings,
				    COALESCE(SUM(CASE WHEN sl.ListingStatus = 'SOLD_OUT' THEN 1 ELSE 0 END), 0) AS soldOutListings,
				    COALESCE(SUM(CASE WHEN sl.ListingStatus = 'DRAFT' THEN 1 ELSE 0 END), 0) AS draftListings,
				    COALESCE(SUM(CASE WHEN sl.ListingStatus = 'HIDDEN' THEN 1 ELSE 0 END), 0) AS hiddenListings,
				    COALESCE(SUM(CASE WHEN sl.ListingStatus = 'REMOVED' THEN 1 ELSE 0 END), 0) AS removedListings,
				    COALESCE(SUM(sl.StockQuantity), 0) AS totalStock,
				    COALESCE(SUM(sl.SoldQuantity), 0) AS totalSold
				FROM dbo.ShopListings sl
				INNER JOIN dbo.Shops s ON s.ShopId = sl.ShopId
				INNER JOIN dbo.Users u ON u.UserId = s.OwnerUserId
				WHERE LOWER(LTRIM(RTRIM(u.Email))) = LOWER(LTRIM(RTRIM(:email)))
				""";

		return jdbcTemplate.queryForMap(sql, Map.of("email", email));
	}

	public Map<String, Object> getOrderSummary(String email) {
		String sql = """
				SELECT
				    COUNT_BIG(*) AS totalOrders,
				    COALESCE(SUM(CASE WHEN o.OrderStatus = 'PENDING_PAYMENT' THEN 1 ELSE 0 END), 0) AS pendingPaymentOrders,
				    COALESCE(SUM(CASE WHEN o.OrderStatus = 'PENDING_SELLER' THEN 1 ELSE 0 END), 0) AS pendingSellerOrders,
				    COALESCE(SUM(CASE WHEN o.OrderStatus = 'CONFIRMED' THEN 1 ELSE 0 END), 0) AS confirmedOrders,
				    COALESCE(SUM(CASE WHEN o.OrderStatus = 'PREPARING' THEN 1 ELSE 0 END), 0) AS preparingOrders,
				    COALESCE(SUM(CASE WHEN o.OrderStatus = 'READY_TO_SHIP' THEN 1 ELSE 0 END), 0) AS readyToShipOrders,
				    COALESCE(SUM(CASE WHEN o.OrderStatus = 'SHIPPING' THEN 1 ELSE 0 END), 0) AS shippingOrders,
				    COALESCE(SUM(CASE WHEN o.OrderStatus = 'DELIVERED' THEN 1 ELSE 0 END), 0) AS deliveredOrders,
				    COALESCE(SUM(CASE WHEN o.OrderStatus = 'COMPLETED' THEN 1 ELSE 0 END), 0) AS completedOrders,
				    COALESCE(SUM(CASE WHEN o.OrderStatus IN ('PENDING_SELLER', 'CONFIRMED', 'PREPARING') THEN 1 ELSE 0 END), 0) AS ordersNeedingAction
				FROM dbo.Orders o
				INNER JOIN dbo.Shops s ON s.ShopId = o.ShopId
				INNER JOIN dbo.Users u ON u.UserId = s.OwnerUserId
				WHERE LOWER(LTRIM(RTRIM(u.Email))) = LOWER(LTRIM(RTRIM(:email)))
				""";

		return jdbcTemplate.queryForMap(sql, Map.of("email", email));
	}

	public List<Map<String, Object>> findRecentOrders(String email, int limit) {
		String sql = """
				SELECT TOP (:limit)
				    o.OrderId AS orderId,
				    o.OrderCode AS orderCode,
				    s.ShopName AS shopName,
				    o.OrderStatus AS orderStatus,
				    o.PaymentMethod AS paymentMethod,
				    o.TotalAmount AS totalAmount,
				    o.CreatedAt AS createdAt
				FROM dbo.Orders o
				INNER JOIN dbo.Shops s ON s.ShopId = o.ShopId
				INNER JOIN dbo.Users u ON u.UserId = s.OwnerUserId
				WHERE LOWER(LTRIM(RTRIM(u.Email))) = LOWER(LTRIM(RTRIM(:email)))
				ORDER BY o.CreatedAt DESC, o.OrderId DESC
				""";

		MapSqlParameterSource params = new MapSqlParameterSource().addValue("email", email).addValue("limit", limit);

		return jdbcTemplate.queryForList(sql, params);
	}

	public List<Map<String, Object>> findMyShops(String email) {
		String sql = """
				SELECT
				    s.ShopId AS shopId,
				    s.ShopName AS shopName,
				    s.ShopSlug AS shopSlug,
				    s.ApprovalStatus AS approvalStatus,
				    s.IsActive AS isActive,
				    COALESCE(listingCounts.totalListings, 0) AS totalListings,
				    COALESCE(orderCounts.totalOrders, 0) AS totalOrders
				FROM dbo.Shops s
				INNER JOIN dbo.Users u ON u.UserId = s.OwnerUserId
				OUTER APPLY (
				    SELECT COUNT_BIG(*) AS totalListings
				    FROM dbo.ShopListings sl
				    WHERE sl.ShopId = s.ShopId
				) listingCounts
				OUTER APPLY (
				    SELECT COUNT_BIG(*) AS totalOrders
				    FROM dbo.Orders o
				    WHERE o.ShopId = s.ShopId
				) orderCounts
				WHERE LOWER(LTRIM(RTRIM(u.Email))) = LOWER(LTRIM(RTRIM(:email)))
				ORDER BY s.CreatedAt DESC, s.ShopId DESC
				""";

		return jdbcTemplate.queryForList(sql, Map.of("email", email));
	}
}