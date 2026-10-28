
package vn.ubm.usedbookmarketplace.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.ubm.usedbookmarketplace.repository.SellerOrderRepository;
import vn.ubm.usedbookmarketplace.service.SellerOrderService;
import vn.ubm.usedbookmarketplace.service.SellerShopService;

@Controller
@RequestMapping("/seller/orders")
public class SellerOrderController {

	private final SellerOrderService sellerOrderService;
	private final SellerShopService sellerShopService;
	private final SellerOrderRepository sellerOrderRepository;
	private final vn.ubm.usedbookmarketplace.service.ShipmentWorkflowService shipmentWorkflowService;

	public SellerOrderController(SellerOrderService sellerOrderService, SellerShopService sellerShopService,
			SellerOrderRepository sellerOrderRepository,
			vn.ubm.usedbookmarketplace.service.ShipmentWorkflowService shipmentWorkflowService) {
		this.sellerOrderService = sellerOrderService;
		this.sellerShopService = sellerShopService;
		this.sellerOrderRepository = sellerOrderRepository;
		this.shipmentWorkflowService = shipmentWorkflowService;
	}

	@GetMapping
	public String orders(Authentication authentication, @RequestParam(required = false) Long shopId,
			@RequestParam(required = false) String status, Model model, RedirectAttributes redirectAttributes) {
		String email = getEmail(authentication);

		model.addAttribute("shops", sellerShopService.getMyApprovedActiveShops(email));
		model.addAttribute("selectedShopId", shopId);
		model.addAttribute("selectedStatus", status);

		if (shopId == null) {
			model.addAttribute("orders", java.util.List.of());
			return "seller/orders";
		}

		try {
			model.addAttribute("orders", sellerOrderService.getOrders(email, shopId, status));
		} catch (IllegalArgumentException ex) {
			redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());

			return "redirect:/seller/orders";
		}

		return "seller/orders";
	}

	@GetMapping("/detail")
	public String orderDetail(Authentication authentication, @RequestParam long shopId, @RequestParam long orderId,
			Model model, RedirectAttributes redirectAttributes) {
		String email = getEmail(authentication);

		try {
			model.addAttribute("order", sellerOrderService.getOrder(email, shopId, orderId));
			model.addAttribute("items", sellerOrderService.getOrderItems(email, shopId, orderId));
			model.addAttribute("histories", sellerOrderService.getOrderStatusHistories(email, shopId, orderId));
			model.addAttribute("shipments", shipmentWorkflowService.shipments(email, shopId, orderId));
			model.addAttribute("shippingProviders", shipmentWorkflowService.providers());

			return "seller/order-detail";
		} catch (IllegalArgumentException ex) {
			redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());

			return "redirect:/seller/orders?shopId=" + shopId;
		}
	}

	@PostMapping("/status")
	public String updateOrderStatus(Authentication authentication, @RequestParam long shopId,
			@RequestParam long orderId, @RequestParam String action, @RequestParam(required = false) String note,
			RedirectAttributes redirectAttributes) {
		String email = getEmail(authentication);

		try {
			Long actorUserId = sellerOrderRepository.findUserIdByEmail(email);

			sellerOrderService.updateOrderStatus(email, actorUserId, shopId, orderId, action, note);

			redirectAttributes.addFlashAttribute("successMessage", "Cập nhật trạng thái đơn hàng thành công.");
		} catch (IllegalArgumentException ex) {
			redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
		}

		return "redirect:/seller/orders/detail?shopId=" + shopId + "&orderId=" + orderId;
	}

	@PostMapping("/shipment/create")
	public String createShipment(Authentication authentication, @RequestParam long shopId, @RequestParam long orderId,
			@RequestParam long providerId, @RequestParam String trackingCode, RedirectAttributes ra) {
		String email = getEmail(authentication);
		try {
			Long actorId = sellerOrderRepository.findUserIdByEmail(email);
			shipmentWorkflowService.createShipment(email, actorId, shopId, orderId, providerId, trackingCode);
			ra.addFlashAttribute("successMessage", "Đã lưu vận đơn.");
		} catch (IllegalArgumentException ex) {
			ra.addFlashAttribute("errorMessage", ex.getMessage());
		}
		return "redirect:/seller/orders/detail?shopId=" + shopId + "&orderId=" + orderId;
	}

	@PostMapping("/shipment/status")
	public String updateShipmentStatus(Authentication authentication, @RequestParam long shopId,
			@RequestParam long orderId, @RequestParam long shipmentId, @RequestParam String status,
			@RequestParam(required = false) String note, RedirectAttributes ra) {
		String email = getEmail(authentication);
		try {
			Long actorId = sellerOrderRepository.findUserIdByEmail(email);
			shipmentWorkflowService.updateShipmentStatus(email, actorId, shopId, orderId, shipmentId, status, note);
			ra.addFlashAttribute("successMessage", "Đã cập nhật trạng thái vận chuyển.");
		} catch (IllegalArgumentException ex) {
			ra.addFlashAttribute("errorMessage", ex.getMessage());
		}
		return "redirect:/seller/orders/detail?shopId=" + shopId + "&orderId=" + orderId;
	}

	private String getEmail(Authentication authentication) {
		if (authentication == null || !authentication.isAuthenticated() || authentication.getName() == null
				|| authentication.getName().isBlank()) {
			throw new IllegalArgumentException("Bạn cần đăng nhập để thực hiện thao tác này.");
		}

		return authentication.getName();
	}
}
