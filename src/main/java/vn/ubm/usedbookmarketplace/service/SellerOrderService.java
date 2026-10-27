
package vn.ubm.usedbookmarketplace.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.ubm.usedbookmarketplace.repository.SellerOrderRepository;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@Service
public class SellerOrderService {

	private static final Set<String> ALLOWED_FILTER_STATUSES = Set.of("PENDING_PAYMENT", "PENDING_SELLER", "CONFIRMED",
			"PREPARING", "READY_TO_SHIP", "SHIPPING", "DELIVERED", "CANCEL_REQUESTED", "CANCELED", "RETURN_REQUESTED",
			"RETURNING", "RETURNED", "COMPLETED");

	private final SellerOrderRepository sellerOrderRepository;

	public SellerOrderService(SellerOrderRepository sellerOrderRepository) {
		this.sellerOrderRepository = sellerOrderRepository;
	}

	@Transactional(readOnly = true)
	public List<Map<String, Object>> getOrders(String email, long shopId, String status) {
		requireEmail(email);
		requireShopId(shopId);
		requireApprovedShopOwner(email, shopId);

		String normalizedStatus = normalizeFilterStatus(status);

		return sellerOrderRepository.findOrders(email.trim(), shopId, normalizedStatus);
	}

	@Transactional(readOnly = true)
	public Map<String, Object> getOrder(String email, long shopId, long orderId) {
		requireEmail(email);
		requireShopId(shopId);
		requireOrderId(orderId);
		requireApprovedShopOwner(email, shopId);

		return sellerOrderRepository.findOrder(email.trim(), shopId, orderId);
	}

	@Transactional(readOnly = true)
	public List<Map<String, Object>> getOrderItems(String email, long shopId, long orderId) {
		requireEmail(email);
		requireShopId(shopId);
		requireOrderId(orderId);
		requireApprovedShopOwner(email, shopId);

		return sellerOrderRepository.findOrderItems(email.trim(), shopId, orderId);
	}

	@Transactional(readOnly = true)
	public List<Map<String, Object>> getOrderStatusHistories(String email, long shopId, long orderId) {
		requireEmail(email);
		requireShopId(shopId);
		requireOrderId(orderId);
		requireApprovedShopOwner(email, shopId);

		return sellerOrderRepository.findOrderStatusHistories(email.trim(), shopId, orderId);
	}

	@Transactional
	public void updateOrderStatus(String email, long actorUserId, long shopId, long orderId, String action,
			String note) {
		requireEmail(email);
		requireShopId(shopId);
		requireOrderId(orderId);

		if (actorUserId <= 0) {
			throw new IllegalArgumentException("Không xác định được tài khoản thực hiện thao tác.");
		}

		requireApprovedShopOwner(email, shopId);

		String currentStatus = sellerOrderRepository.findOrderStatusForUpdate(shopId, orderId);

		String newStatus = resolveNextStatus(currentStatus, action);
		String normalizedNote = normalizeNote(note);

		int affected = sellerOrderRepository.updateOrderStatus(shopId, orderId, currentStatus, newStatus);

		if (affected != 1) {
			throw new IllegalArgumentException(
					"Trạng thái đơn hàng đã thay đổi hoặc không thể cập nhật. Vui lòng tải lại trang.");
		}

		sellerOrderRepository.insertOrderStatusHistory(orderId, actorUserId, currentStatus, newStatus, normalizedNote);
	}

	private String resolveNextStatus(String currentStatus, String action) {
		if (currentStatus == null || action == null || action.isBlank()) {
			throw new IllegalArgumentException("Không xác định được trạng thái hoặc thao tác của đơn hàng.");
		}

		String normalizedAction = action.trim().toUpperCase(Locale.ROOT);

		return switch (normalizedAction) {
		case "CONFIRM" -> {
			requireCurrentStatus(currentStatus, "PENDING_SELLER");
			yield "CONFIRMED";
		}
		case "PREPARE" -> {
			requireCurrentStatus(currentStatus, "CONFIRMED");
			yield "PREPARING";
		}
		case "READY_TO_SHIP" -> {
			requireCurrentStatus(currentStatus, "PREPARING");
			yield "READY_TO_SHIP";
		}
		default -> throw new IllegalArgumentException("Thao tác xử lý đơn hàng không hợp lệ.");
		};
	}

	private void requireCurrentStatus(String currentStatus, String expectedStatus) {
		if (!expectedStatus.equalsIgnoreCase(currentStatus)) {
			throw new IllegalArgumentException(
					"Không thể thực hiện thao tác này khi đơn hàng đang ở trạng thái " + currentStatus + ".");
		}
	}

	private String normalizeFilterStatus(String status) {
		if (status == null || status.isBlank() || "ALL".equalsIgnoreCase(status.trim())) {
			return null;
		}

		String normalized = status.trim().toUpperCase(Locale.ROOT);

		if (!ALLOWED_FILTER_STATUSES.contains(normalized)) {
			throw new IllegalArgumentException("Trạng thái lọc đơn hàng không hợp lệ.");
		}

		return normalized;
	}

	private String normalizeNote(String note) {
		if (note == null || note.isBlank()) {
			return null;
		}

		String normalized = note.trim();

		if (normalized.length() > 1000) {
			throw new IllegalArgumentException("Ghi chú không được vượt quá 1000 ký tự.");
		}

		return normalized;
	}

	private void requireApprovedShopOwner(String email, long shopId) {
		if (!sellerOrderRepository.isApprovedActiveShopOwner(email.trim(), shopId)) {
			throw new IllegalArgumentException("Không có quyền quản lý shop này hoặc shop chưa được duyệt.");
		}
	}

	private void requireEmail(String email) {
		if (email == null || email.isBlank()) {
			throw new IllegalArgumentException("Không xác định được tài khoản đang đăng nhập.");
		}
	}

	private void requireShopId(long shopId) {
		if (shopId <= 0) {
			throw new IllegalArgumentException("Shop không hợp lệ.");
		}
	}

	private void requireOrderId(long orderId) {
		if (orderId <= 0) {
			throw new IllegalArgumentException("Đơn hàng không hợp lệ.");
		}
	}
}
