
package vn.ubm.usedbookmarketplace.controller;

import java.security.Principal;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import vn.ubm.usedbookmarketplace.service.OrderService;

@Controller
@RequestMapping("/buyer/orders")
public class OrderController {

	private final OrderService orderService;

	public OrderController(OrderService orderService) {
		this.orderService = orderService;
	}

	@GetMapping
	public String viewOrders(Principal principal, Model model) {
		if (principal == null) {
			return "redirect:/auth/login";
		}

		model.addAttribute("orders", orderService.getOrdersForCurrentBuyer(principal.getName()));

		return "buyer/orders";
	}

	@GetMapping("/{orderId}")
	public String viewOrderDetails(@PathVariable Long orderId, Principal principal, Model model) {

		if (principal == null) {
			return "redirect:/auth/login";
		}

		if (orderId <= 0) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}

		var order = orderService.getOrderDetailsForBuyer(orderId, principal.getName());

		if (order == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}

		model.addAttribute("order", order);
		model.addAttribute("items", orderService.getOrderItemsForBuyer(orderId, principal.getName()));

		return "buyer/order-detail";
	}

}
