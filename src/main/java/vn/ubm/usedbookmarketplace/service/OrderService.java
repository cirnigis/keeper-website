
package vn.ubm.usedbookmarketplace.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import vn.ubm.usedbookmarketplace.repository.OrderItemRepository;
import vn.ubm.usedbookmarketplace.repository.OrderRepository;
import vn.ubm.usedbookmarketplace.repository.projection.OrderDetailView;
import vn.ubm.usedbookmarketplace.repository.projection.OrderItemView;
import vn.ubm.usedbookmarketplace.repository.projection.OrderListView;

@Service
@Transactional(readOnly = true)
public class OrderService {

	private final OrderRepository orderRepository;
	private final OrderItemRepository orderItemRepository;

	public OrderService(OrderRepository orderRepository, OrderItemRepository orderItemRepository) {
		this.orderRepository = orderRepository;
		this.orderItemRepository = orderItemRepository;
	}

	public List<OrderListView> getOrdersForCurrentBuyer(String email) {
		return orderRepository.findOrdersByBuyerEmail(email);
	}

	public OrderDetailView getOrderDetailsForBuyer(Long orderId, String email) {
		return orderRepository.findOrderDetailsByIdAndBuyerEmail(orderId, email).orElse(null);
	}

	public List<OrderItemView> getOrderItemsForBuyer(Long orderId, String email) {
		return orderItemRepository.findItemsByOrderIdAndBuyerEmail(orderId, email);
	}
}
