
package vn.ubm.usedbookmarketplace.repository.projection;

import java.math.BigDecimal;

public interface OrderItemView {
	Long getOrderItemId();

	String getBookTitle();

	String getCondition();

	BigDecimal getUnitPrice();

	Integer getQuantity();

	BigDecimal getLineTotal();
}
