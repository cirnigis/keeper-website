
package vn.ubm.usedbookmarketplace.repository.projection;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface OrderDetailView {
	Long getOrderId();

	String getOrderCode();

	LocalDateTime getCreatedAt();

	String getStatus();

	String getPaymentMethod();

	String getPaymentStatus();

	BigDecimal getSubtotal();

	BigDecimal getDiscountAmount();

	BigDecimal getShippingFee();

	BigDecimal getTotalAmount();

	String getShopName();

	String getRecipientName();

	String getRecipientPhone();

	String getProvince();

	String getDistrict();

	String getWard();

	String getAddressLine();

	String getBuyerNote();
}
