
package vn.ubm.usedbookmarketplace.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface OrderRepositoryCustom {

	List<Map<String, Object>> findCheckoutItemsForUpdate(Long cartId);

	Long createOrder(Long buyerUserId, Long shopId, String paymentMethod, BigDecimal subtotal, BigDecimal shippingFee,
			Long addressId, String buyerNote);

	Long createOrderItem(Long orderId, Long listingId, String bookTitle, String conditionCode, BigDecimal unitPrice,
			int quantity);

	void reserveInventory(Long orderItemId, Long listingId, int quantity, String paymentMethod);

	void removeCartItem(Long cartId, Long cartItemId);

	void createStatusHistory(Long orderId, Long userId, String newStatus, String note);
}
