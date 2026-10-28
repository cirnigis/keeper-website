package vn.ubm.usedbookmarketplace.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Collections;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import vn.ubm.usedbookmarketplace.entity.Payment;
import vn.ubm.usedbookmarketplace.repository.PaymentRepository;

@Service
public class PaymentService {

	private final PaymentRepository paymentRepository;
	private final NamedParameterJdbcTemplate jdbc;

	public PaymentService(PaymentRepository paymentRepository, NamedParameterJdbcTemplate jdbc) {
		this.paymentRepository = paymentRepository;
		this.jdbc = jdbc;
	}

	@Transactional
	public Payment createPayment(Long orderId, String paymentMethod, BigDecimal amount) {
		if (orderId == null || orderId <= 0) {
			throw new IllegalArgumentException("Order ID không hợp lệ.");
		}

		String method = normalizePaymentMethod(paymentMethod);

		if (amount == null || amount.signum() < 0) {
			throw new IllegalArgumentException("Số tiền thanh toán không hợp lệ.");
		}

		// Never trust a caller-supplied amount/method without comparing it to the
		// persisted order.
		// Checkout is the normal caller, but this service-level guard also protects
		// future callers.
		MapSqlParameterSource orderParams = new MapSqlParameterSource().addValue("orderId", orderId);
		List<Map<String, Object>> orderRows = jdbc.queryForList("""
				SELECT o.TotalAmount, o.PaymentMethod, o.OrderStatus
				FROM dbo.Orders o WITH (UPDLOCK, HOLDLOCK)
				WHERE o.OrderId = :orderId
				""", orderParams);
		if (orderRows.isEmpty()) {
			throw new IllegalArgumentException("Không tìm thấy đơn hàng để tạo thanh toán.");
		}
		Map<String, Object> order = orderRows.get(0);
		BigDecimal expectedAmount = (BigDecimal) order.get("TotalAmount");
		String expectedMethod = String.valueOf(order.get("PaymentMethod")).trim().toUpperCase();
		String orderStatus = String.valueOf(order.get("OrderStatus")).trim().toUpperCase();
		if (!expectedMethod.equals(method)) {
			throw new IllegalArgumentException("Phương thức thanh toán không khớp với đơn hàng.");
		}
		if (expectedAmount == null || expectedAmount.compareTo(amount) != 0) {
			throw new IllegalArgumentException("Số tiền thanh toán không khớp tổng tiền đơn hàng.");
		}
		if ("CANCELLED".equals(orderStatus) || "CANCELED".equals(orderStatus) || "COMPLETED".equals(orderStatus)
				|| "DELIVERED".equals(orderStatus)) {
			throw new IllegalArgumentException("Trạng thái đơn hàng không cho phép tạo thanh toán mới.");
		}
		Integer existingPayments = jdbc.queryForObject(
				"SELECT COUNT(1) FROM dbo.Payments WITH (UPDLOCK, HOLDLOCK) WHERE OrderId = :orderId",
				new MapSqlParameterSource().addValue("orderId", orderId), Integer.class);
		if (existingPayments != null && existingPayments > 0) {
			throw new IllegalArgumentException("Đơn hàng đã có bản ghi thanh toán.");
		}

		Payment payment = new Payment();
		payment.setOrderId(orderId);
		payment.setPaymentMethod(method);
		payment.setAmount(amount);
		payment.setPaymentStatus("PENDING");

		return paymentRepository.save(payment);
	}

	@Transactional
	public void processMomoMockResult(String email, List<Long> orderIds, String result) {

		if (email == null || email.isBlank()) {
			throw new IllegalArgumentException("Vui lòng đăng nhập để tiếp tục.");
		}

		if (orderIds == null || orderIds.isEmpty()) {
			throw new IllegalArgumentException("Không tìm thấy đơn hàng cần thanh toán.");
		}

		String normalizedResult = result == null ? "" : result.trim().toUpperCase();

		if (!normalizedResult.equals("SUCCESS") && !normalizedResult.equals("FAILED")) {
			throw new IllegalArgumentException("Kết quả thanh toán không hợp lệ.");
		}

		if (orderIds.stream().anyMatch(id -> id == null || id <= 0)) {
			throw new IllegalArgumentException("Mã đơn hàng không hợp lệ.");
		}

		if (orderIds.stream().distinct().count() != orderIds.size()) {
			throw new IllegalArgumentException("Danh sách đơn hàng bị trùng.");
		}

		// Lock order rows in a stable order. The expiry worker locks Orders first too,
		// so a payment callback cannot race cancellation/stock release.
		List<Long> orderedIds = new ArrayList<>(orderIds);
		Collections.sort(orderedIds);
		for (Long orderId : orderedIds) {
			MapSqlParameterSource params = new MapSqlParameterSource().addValue("orderId", orderId).addValue("email",
					email.trim());

			List<Map<String, Object>> orderRows = jdbc.queryForList("""
					SELECT o.PaymentMethod, o.OrderStatus
					FROM dbo.Orders o WITH (UPDLOCK, HOLDLOCK)
					INNER JOIN dbo.Users u ON u.UserId = o.BuyerUserId
					WHERE o.OrderId = :orderId
					  AND u.Email = :email
					""", params);

			if (orderRows.isEmpty()) {
				throw new IllegalArgumentException("Đơn hàng không tồn tại hoặc không thuộc tài khoản của bạn.");
			}
			String method = String.valueOf(orderRows.get(0).get("PaymentMethod"));
			String orderStatus = String.valueOf(orderRows.get(0).get("OrderStatus"));
			if (!"MOMO".equalsIgnoreCase(method)) {
				throw new IllegalArgumentException("Chỉ đơn hàng thanh toán MoMo mới được xử lý tại trang này.");
			}
			if (!"PENDING_PAYMENT".equalsIgnoreCase(orderStatus)) {
				throw new IllegalArgumentException("Đơn hàng không còn ở trạng thái chờ thanh toán.");
			}
			List<String> statuses = jdbc.query("""
					SELECT TOP (1) p.PaymentStatus
					FROM dbo.Payments p WITH (UPDLOCK, HOLDLOCK)
					WHERE p.OrderId = :orderId
					ORDER BY p.CreatedAt DESC, p.PaymentId DESC
					""", params, (rs, rowNum) -> rs.getString("PaymentStatus"));

			if (statuses.isEmpty()) {
				throw new IllegalArgumentException("Không tìm thấy thông tin thanh toán.");
			}

			String currentStatus = statuses.get(0);

			if ("PAID".equalsIgnoreCase(currentStatus)) {
				throw new IllegalArgumentException("Đơn hàng này đã được thanh toán.");
			}

			if (!"PENDING".equalsIgnoreCase(currentStatus) && !"FAILED".equalsIgnoreCase(currentStatus)) {
				throw new IllegalArgumentException("Trạng thái thanh toán hiện tại không cho phép xử lý lại.");
			}

			Integer validReservations = jdbc.queryForObject("""
					SELECT COUNT(1)
					FROM dbo.InventoryReservations r WITH (UPDLOCK, HOLDLOCK)
					INNER JOIN dbo.OrderItems oi ON oi.OrderItemId = r.OrderItemId
					WHERE oi.OrderId = :orderId
					  AND r.ReservationStatus = 'RESERVED'
					  AND r.ExpiresAt > SYSUTCDATETIME()
					""", params, Integer.class);
			Integer totalReservations = jdbc.queryForObject("""
					SELECT COUNT(1)
					FROM dbo.InventoryReservations r WITH (UPDLOCK, HOLDLOCK)
					INNER JOIN dbo.OrderItems oi ON oi.OrderItemId = r.OrderItemId
					WHERE oi.OrderId = :orderId
					""", params, Integer.class);
			if (totalReservations == null || totalReservations == 0 || validReservations == null
					|| !validReservations.equals(totalReservations)) {
				throw new IllegalArgumentException("Giữ chỗ tồn kho đã hết hạn hoặc không còn hợp lệ.");
			}

			if ("SUCCESS".equals(normalizedResult)) {
				int updated = jdbc.update("""
						UPDATE dbo.Payments
						SET PaymentStatus = 'PAID',
						    PaidAt = SYSUTCDATETIME(),
						    UpdatedAt = SYSUTCDATETIME()
						WHERE PaymentId = (
						    SELECT TOP (1) p.PaymentId
						    FROM dbo.Payments p
						    WHERE p.OrderId = :orderId
						    ORDER BY p.CreatedAt DESC, p.PaymentId DESC
						)
						  AND PaymentStatus IN ('PENDING', 'FAILED')
						""", params);

				if (updated != 1) {
					throw new IllegalArgumentException("Không thể cập nhật trạng thái thanh toán.");
				}

				int orderUpdated = jdbc.update("""
						UPDATE dbo.Orders
						SET OrderStatus = 'PENDING_SELLER',
						    UpdatedAt = SYSUTCDATETIME()
						WHERE OrderId = :orderId
						  AND PaymentMethod = 'MOMO'
						  AND OrderStatus = 'PENDING_PAYMENT'
						""", params);

				if (orderUpdated != 1) {
					throw new IllegalArgumentException("Không thể cập nhật trạng thái đơn hàng.");
				}

				jdbc.update("""
						UPDATE r
						SET ReservationStatus = 'COMMITTED',
						    CommittedAt = SYSUTCDATETIME()
						FROM dbo.InventoryReservations r
						INNER JOIN dbo.OrderItems oi
						    ON oi.OrderItemId = r.OrderItemId
						WHERE oi.OrderId = :orderId
						  AND r.ReservationStatus = 'RESERVED'
						""", params);
			} else {
				int updated = jdbc.update("""
						UPDATE dbo.Payments
						SET PaymentStatus = 'FAILED',
						    UpdatedAt = SYSUTCDATETIME()
						WHERE PaymentId = (
						    SELECT TOP (1) p.PaymentId
						    FROM dbo.Payments p
						    WHERE p.OrderId = :orderId
						    ORDER BY p.CreatedAt DESC, p.PaymentId DESC
						)
						  AND PaymentStatus IN ('PENDING', 'FAILED')
						""", params);

				if (updated != 1) {
					throw new IllegalArgumentException("Không thể cập nhật trạng thái thanh toán.");
				}
			}
		}
	}

	@Transactional(readOnly = true)
	public List<Payment> getPaymentsByOrderId(Long orderId) {
		if (orderId == null || orderId <= 0) {
			throw new IllegalArgumentException("Order ID không hợp lệ.");
		}

		return paymentRepository.findByOrderIdOrderByCreatedAtDesc(orderId);
	}

	private String normalizePaymentMethod(String paymentMethod) {
		if (paymentMethod == null) {
			throw new IllegalArgumentException("Vui lòng chọn phương thức thanh toán.");
		}

		String method = paymentMethod.trim().toUpperCase();

		if (!method.equals("COD") && !method.equals("MOMO")) {
			throw new IllegalArgumentException("Phương thức thanh toán không hợp lệ.");
		}

		return method;
	}
}