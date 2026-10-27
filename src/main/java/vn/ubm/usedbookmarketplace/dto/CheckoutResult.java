
package vn.ubm.usedbookmarketplace.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CheckoutResult {

	private final List<Long> orderIds = new ArrayList<>();
	private BigDecimal totalAmount = BigDecimal.ZERO;

	public List<Long> getOrderIds() {
		return orderIds;
	}

	public void addOrderId(Long orderId) {
		orderIds.add(orderId);
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}
}
