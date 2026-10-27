
package vn.ubm.usedbookmarketplace.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class OrderRepositoryImpl implements OrderRepositoryCustom {

	private final NamedParameterJdbcTemplate jdbc;

	public OrderRepositoryImpl(NamedParameterJdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	@Override
	@Transactional
	public List<Map<String, Object>> findCheckoutItemsForUpdate(Long cartId) {
		String sql = """
				SELECT
				    ci.CartItemId AS cartItemId,
				    sl.ListingId AS listingId,
				    sl.ShopId AS shopId,
				    ci.Quantity AS quantity,
				    sl.StockQuantity AS stockQuantity,
				    sl.UnitPrice AS unitPrice,
				    COALESCE(
				        NULLIF(LTRIM(RTRIM(sl.ListingTitle)), ''),
				        CONCAT(N'Sách #', sl.BookId)
				    ) AS bookTitle,
				    sl.ConditionCode AS conditionCode
				FROM dbo.CartItems ci WITH (UPDLOCK, ROWLOCK)
				INNER JOIN dbo.ShopListings sl WITH (UPDLOCK, ROWLOCK)
				    ON sl.ListingId = ci.ListingId
				WHERE ci.CartId = :cartId
				  AND sl.ListingStatus = 'ACTIVE'
				ORDER BY ci.CartItemId
				""";

		return jdbc.queryForList(sql, new MapSqlParameterSource("cartId", cartId));
	}

	@Override
	@Transactional
	public Long createOrder(Long buyerUserId, Long shopId, String paymentMethod, BigDecimal subtotal,
			BigDecimal shippingFee, Long addressId, String buyerNote) {

		if (buyerUserId == null || shopId == null || addressId == null) {
			throw new IllegalArgumentException("Thông tin tạo đơn hàng không hợp lệ.");
		}

		if (subtotal == null || shippingFee == null || subtotal.signum() < 0 || shippingFee.signum() < 0) {
			throw new IllegalArgumentException("Số tiền đơn hàng không hợp lệ.");
		}

		if (paymentMethod == null) {
			throw new IllegalArgumentException("Vui lòng chọn phương thức thanh toán.");
		}

		String normalizedMethod = paymentMethod.trim().toUpperCase();

		if (!"COD".equals(normalizedMethod) && !"MOMO".equals(normalizedMethod)) {
			throw new IllegalArgumentException("Chỉ hỗ trợ thanh toán COD hoặc MOMO.");
		}

		String orderStatus = "COD".equals(normalizedMethod) ? "PENDING_SELLER" : "PENDING_PAYMENT";

		String orderCode = UUID.randomUUID().toString().replace("-", "");
		BigDecimal discount = BigDecimal.ZERO;
		BigDecimal commission = BigDecimal.ZERO;
		BigDecimal total = subtotal.add(shippingFee);

		String sql = """
				INSERT INTO dbo.Orders (
				    OrderCode,
				    BuyerUserId,
				    ShopId,
				    OrderStatus,
				    PaymentMethod,
				    Subtotal,
				    DiscountAmount,
				    ShippingFee,
				    PlatformCommission,
				    TotalAmount,
				    RecipientName,
				    RecipientPhone,
				    Province,
				    District,
				    Ward,
				    AddressLine,
				    BuyerNote
				)
				OUTPUT INSERTED.OrderId
				SELECT
				    :orderCode,
				    :buyerUserId,
				    :shopId,
				    :orderStatus,
				    :paymentMethod,
				    :subtotal,
				    :discount,
				    :shippingFee,
				    :commission,
				    :total,
				    ua.RecipientName,
				    ua.Phone,
				    ua.Province,
				    ua.District,
				    ua.Ward,
				    ua.AddressLine,
				    :buyerNote
				FROM dbo.UserAddresses ua
				WHERE ua.AddressId = :addressId
				  AND ua.UserId = :buyerUserId
				  AND ua.IsActive = 1
				""";

		MapSqlParameterSource params = new MapSqlParameterSource().addValue("orderCode", orderCode)
				.addValue("buyerUserId", buyerUserId).addValue("shopId", shopId).addValue("orderStatus", orderStatus)
				.addValue("paymentMethod", normalizedMethod).addValue("subtotal", subtotal)
				.addValue("discount", discount).addValue("shippingFee", shippingFee).addValue("commission", commission)
				.addValue("total", total).addValue("addressId", addressId).addValue("buyerNote", buyerNote);

		List<Long> ids = jdbc.query(sql, params, (rs, rowNum) -> rs.getLong(1));

		if (ids.isEmpty()) {
			throw new IllegalArgumentException("Không tìm thấy địa chỉ giao hàng hợp lệ.");
		}

		return ids.get(0);
	}

	@Override
	@Transactional
	public Long createOrderItem(Long orderId, Long listingId, String bookTitle, String conditionCode,
			BigDecimal unitPrice, int quantity) {

		if (orderId == null || listingId == null || unitPrice == null || unitPrice.signum() < 0 || quantity <= 0) {
			throw new IllegalArgumentException("Thông tin sản phẩm trong đơn hàng không hợp lệ.");
		}

		String sql = """
				INSERT INTO dbo.OrderItems (
				    OrderId,
				    ListingId,
				    BookTitleSnapshot,
				    ConditionSnapshot,
				    UnitPrice,
				    Quantity,
				    LineTotal
				)
				OUTPUT INSERTED.OrderItemId
				VALUES (
				    :orderId,
				    :listingId,
				    :bookTitle,
				    :conditionCode,
				    :unitPrice,
				    :quantity,
				    :lineTotal
				)
				""";

		BigDecimal lineTotal = unitPrice.multiply(BigDecimal.valueOf(quantity));

		MapSqlParameterSource params = new MapSqlParameterSource().addValue("orderId", orderId)
				.addValue("listingId", listingId).addValue("bookTitle", bookTitle)
				.addValue("conditionCode", conditionCode).addValue("unitPrice", unitPrice)
				.addValue("quantity", quantity).addValue("lineTotal", lineTotal);

		return jdbc.queryForObject(sql, params, Long.class);
	}

	@Override
	@Transactional
	public void reserveInventory(Long orderItemId, Long listingId, int quantity, String paymentMethod) {

		if (orderItemId == null || listingId == null || quantity <= 0) {
			throw new IllegalArgumentException("Thông tin giữ hàng không hợp lệ.");
		}

		String updateSql = """
				UPDATE dbo.ShopListings
				SET
				    StockQuantity = StockQuantity - :quantity,
				    ListingStatus = CASE
				        WHEN StockQuantity - :quantity = 0 THEN 'SOLD_OUT'
				        ELSE ListingStatus
				    END,
				    UpdatedAt = SYSUTCDATETIME()
				WHERE ListingId = :listingId
				  AND ListingStatus = 'ACTIVE'
				  AND StockQuantity >= :quantity
				""";

		MapSqlParameterSource params = new MapSqlParameterSource().addValue("quantity", quantity).addValue("listingId",
				listingId);

		int updated = jdbc.update(updateSql, params);

		if (updated != 1) {
			throw new IllegalArgumentException("Sản phẩm đã hết hàng hoặc không đủ số lượng.");
		}

		String insertSql = """
				INSERT INTO dbo.InventoryReservations (
				    OrderItemId,
				    ListingId,
				    Quantity,
				    ReservationStatus,
				    ExpiresAt
				)
				VALUES (
				    :orderItemId,
				    :listingId,
				    :quantity,
				    'RESERVED',
				    DATEADD(MINUTE, 30, SYSUTCDATETIME())
				)
				""";

		jdbc.update(insertSql, new MapSqlParameterSource().addValue("orderItemId", orderItemId)
				.addValue("listingId", listingId).addValue("quantity", quantity));
	}

	@Override
	@Transactional
	public void removeCartItem(Long cartId, Long cartItemId) {
		String sql = """
				DELETE FROM dbo.CartItems
				WHERE CartId = :cartId
				  AND CartItemId = :cartItemId
				""";

		int deleted = jdbc.update(sql,
				new MapSqlParameterSource().addValue("cartId", cartId).addValue("cartItemId", cartItemId));

		if (deleted != 1) {
			throw new IllegalStateException("Không thể xóa sản phẩm khỏi giỏ hàng.");
		}
	}

	@Override
	@Transactional
	public void createStatusHistory(Long orderId, Long userId, String newStatus, String note) {

		if (orderId == null || userId == null || newStatus == null) {
			throw new IllegalArgumentException("Thông tin lịch sử đơn hàng không hợp lệ.");
		}

		String sql = """
				INSERT INTO dbo.OrderStatusHistories (
				    OrderId,
				    ActorUserId,
				    PreviousStatus,
				    NewStatus,
				    Note
				)
				VALUES (
				    :orderId,
				    :userId,
				    NULL,
				    :newStatus,
				    :note
				)
				""";

		jdbc.update(sql, new MapSqlParameterSource().addValue("orderId", orderId).addValue("userId", userId)
				.addValue("newStatus", newStatus).addValue("note", note));
	}
}
