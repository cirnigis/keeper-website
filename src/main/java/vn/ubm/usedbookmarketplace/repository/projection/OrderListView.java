
package vn.ubm.usedbookmarketplace.repository.projection;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface OrderListView {
	Long getOrderId();

	String getOrderCode();

	LocalDateTime getCreatedAt();

	String getStatus();

	String getPaymentStatus();

	BigDecimal getTotalAmount();

	String getShopName();
}
