
package vn.ubm.usedbookmarketplace.repository;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class SellerOrderRepository {

	private final NamedParameterJdbcTemplate jdbc;

	public SellerOrderRepository(NamedParameterJdbcTemplate jdbc) {
		this.jdbc = jdbc;
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

	public List<Map<String, Object>> findOrders(String email, long shopId, String status) {
		String sql = """
				SELECT
				    o.OrderId AS orderId,
				    o.OrderCode AS orderCode,
				    o.ShopId AS shopId,
				    s.ShopName AS shopName,
				    o.OrderStatus AS orderStatus,
				    o.PaymentMethod AS paymentMethod,
				    o.Subtotal AS subtotal,
				    o.DiscountAmount AS discountAmount,
				    o.ShippingFee AS shippingFee,
				    o.PlatformCommission AS platformCommission,
				    o.TotalAmount AS totalAmount,
				    o.RecipientName AS recipientName,
				    o.RecipientPhone AS recipientPhone,
				    o.Province AS province,
				    o.District AS district,
				    o.Ward AS ward,
				    o.AddressLine AS addressLine,
				    o.BuyerNote AS buyerNote,
				    o.SellerConfirmedAt AS sellerConfirmedAt,
				    o.DeliveredAt AS deliveredAt,
				    o.CompletedAt AS completedAt,
				    o.CanceledAt AS canceledAt,
				    o.CreatedAt AS createdAt,
				    o.UpdatedAt AS updatedAt,
				    u.Email AS buyerEmail
				FROM dbo.Orders o
				INNER JOIN dbo.Shops s ON s.ShopId = o.ShopId
				INNER JOIN dbo.Users owner ON owner.UserId = s.OwnerUserId
				INNER JOIN dbo.Users u ON u.UserId = o.BuyerUserId
				WHERE s.ShopId = :shopId
				  AND LOWER(LTRIM(RTRIM(owner.Email))) = LOWER(LTRIM(RTRIM(:email)))
				  AND s.ApprovalStatus = 'APPROVED'
				  AND s.IsActive = 1
				  AND (:status IS NULL OR o.OrderStatus = :status)
				ORDER BY o.CreatedAt DESC, o.OrderId DESC
				""";

		MapSqlParameterSource params = new MapSqlParameterSource().addValue("shopId", shopId).addValue("email", email)
				.addValue("status", status);

		return jdbc.queryForList(sql, params);
	}

	public Map<String, Object> findOrder(String email, long shopId, long orderId) {
		String sql = """
				SELECT
				    o.OrderId AS orderId,
				    o.OrderCode AS orderCode,
				    o.ShopId AS shopId,
				    s.ShopName AS shopName,
				    o.OrderStatus AS orderStatus,
				    o.PaymentMethod AS paymentMethod,
				    o.Subtotal AS subtotal,
				    o.DiscountAmount AS discountAmount,
				    o.ShippingFee AS shippingFee,
				    o.PlatformCommission AS platformCommission,
				    o.TotalAmount AS totalAmount,
				    o.RecipientName AS recipientName,
				    o.RecipientPhone AS recipientPhone,
				    o.Province AS province,
				    o.District AS district,
				    o.Ward AS ward,
				    o.AddressLine AS addressLine,
				    o.BuyerNote AS buyerNote,
				    o.SellerConfirmedAt AS sellerConfirmedAt,
				    o.DeliveredAt AS deliveredAt,
				    o.CompletedAt AS completedAt,
				    o.CanceledAt AS canceledAt,
				    o.CreatedAt AS createdAt,
				    o.UpdatedAt AS updatedAt,
				    u.Email AS buyerEmail
				FROM dbo.Orders o
				INNER JOIN dbo.Shops s ON s.ShopId = o.ShopId
				INNER JOIN dbo.Users owner ON owner.UserId = s.OwnerUserId
				INNER JOIN dbo.Users u ON u.UserId = o.BuyerUserId
				WHERE o.OrderId = :orderId
				  AND o.ShopId = :shopId
				  AND LOWER(LTRIM(RTRIM(owner.Email))) = LOWER(LTRIM(RTRIM(:email)))
				  AND s.ApprovalStatus = 'APPROVED'
				  AND s.IsActive = 1
				""";

		MapSqlParameterSource params = new MapSqlParameterSource().addValue("orderId", orderId)
				.addValue("shopId", shopId).addValue("email", email);

		List<Map<String, Object>> results = jdbc.queryForList(sql, params);

		if (results.isEmpty()) {
			throw new IllegalArgumentException("Không tìm thấy đơn hàng hoặc bạn không có quyền quản lý đơn hàng này.");
		}

		return results.get(0);
	}

	public List<Map<String, Object>> findOrderItems(String email, long shopId, long orderId) {
		String sql = """
				SELECT
				    oi.OrderItemId AS orderItemId,
				    oi.OrderId AS orderId,
				    oi.ListingId AS listingId,
				    oi.BookTitleSnapshot AS bookTitle,
				    oi.ConditionSnapshot AS conditionCode,
				    oi.UnitPrice AS unitPrice,
				    oi.Quantity AS quantity,
				    oi.LineTotal AS lineTotal,
				    oi.CreatedAt AS createdAt
				FROM dbo.OrderItems oi
				INNER JOIN dbo.Orders o ON o.OrderId = oi.OrderId
				INNER JOIN dbo.Shops s ON s.ShopId = o.ShopId
				INNER JOIN dbo.Users owner ON owner.UserId = s.OwnerUserId
				WHERE o.OrderId = :orderId
				  AND o.ShopId = :shopId
				  AND LOWER(LTRIM(RTRIM(owner.Email))) = LOWER(LTRIM(RTRIM(:email)))
				  AND s.ApprovalStatus = 'APPROVED'
				  AND s.IsActive = 1
				ORDER BY oi.OrderItemId
				""";

		MapSqlParameterSource params = new MapSqlParameterSource().addValue("orderId", orderId)
				.addValue("shopId", shopId).addValue("email", email);

		return jdbc.queryForList(sql, params);
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
			throw new IllegalArgumentException("Không tìm thấy tài khoản đang đăng nhập.");
		}
	}

	public String findOrderStatusForUpdate(long shopId, long orderId) {
		String sql = """
				SELECT OrderStatus
				FROM dbo.Orders WITH (UPDLOCK, ROWLOCK)
				WHERE OrderId = :orderId
				  AND ShopId = :shopId
				""";

		try {
			return jdbc.queryForObject(sql,
					new MapSqlParameterSource().addValue("orderId", orderId).addValue("shopId", shopId), String.class);
		} catch (EmptyResultDataAccessException ex) {
			throw new IllegalArgumentException("Không tìm thấy đơn hàng.");
		}
	}

	public int updateOrderStatus(long shopId, long orderId, String expectedStatus, String newStatus) {
		String sql = """
				UPDATE dbo.Orders
				SET OrderStatus = :newStatus,
				    SellerConfirmedAt = CASE
				        WHEN :newStatus = 'CONFIRMED'
				        THEN SYSUTCDATETIME()
				        ELSE SellerConfirmedAt
				    END,
				    UpdatedAt = SYSUTCDATETIME()
				WHERE OrderId = :orderId
				  AND ShopId = :shopId
				  AND OrderStatus = :expectedStatus
				""";

		MapSqlParameterSource params = new MapSqlParameterSource().addValue("shopId", shopId)
				.addValue("orderId", orderId).addValue("expectedStatus", expectedStatus)
				.addValue("newStatus", newStatus);

		return jdbc.update(sql, params);
	}

	public void insertOrderStatusHistory(long orderId, Long actorUserId, String previousStatus, String newStatus,
			String note) {
		String sql = """
				INSERT INTO dbo.OrderStatusHistories (
				    OrderId,
				    ActorUserId,
				    PreviousStatus,
				    NewStatus,
				    Note,
				    CreatedAt
				)
				VALUES (
				    :orderId,
				    :actorUserId,
				    :previousStatus,
				    :newStatus,
				    :note,
				    SYSUTCDATETIME()
				)
				""";

		MapSqlParameterSource params = new MapSqlParameterSource().addValue("orderId", orderId)
				.addValue("actorUserId", actorUserId).addValue("previousStatus", previousStatus)
				.addValue("newStatus", newStatus).addValue("note", note);

		jdbc.update(sql, params);
	}

	public List<Map<String, Object>> findOrderStatusHistories(String email, long shopId, long orderId) {
		String sql = """
				SELECT
				    h.OrderStatusHistoryId AS historyId,
				    h.PreviousStatus AS previousStatus,
				    h.NewStatus AS newStatus,
				    h.Note AS note,
				    h.CreatedAt AS createdAt,
				    u.Email AS actorEmail
				FROM dbo.OrderStatusHistories h
				INNER JOIN dbo.Orders o ON o.OrderId = h.OrderId
				INNER JOIN dbo.Shops s ON s.ShopId = o.ShopId
				INNER JOIN dbo.Users owner ON owner.UserId = s.OwnerUserId
				LEFT JOIN dbo.Users u ON u.UserId = h.ActorUserId
				WHERE h.OrderId = :orderId
				  AND o.ShopId = :shopId
				  AND LOWER(LTRIM(RTRIM(owner.Email))) = LOWER(LTRIM(RTRIM(:email)))
				  AND s.ApprovalStatus = 'APPROVED'
				  AND s.IsActive = 1
				ORDER BY h.CreatedAt DESC, h.OrderStatusHistoryId DESC
				""";

		return jdbc.queryForList(sql, new MapSqlParameterSource().addValue("orderId", orderId)
				.addValue("shopId", shopId).addValue("email", email));
	}
}
