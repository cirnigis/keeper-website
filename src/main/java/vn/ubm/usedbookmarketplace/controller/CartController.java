package vn.ubm.usedbookmarketplace.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import vn.ubm.usedbookmarketplace.dto.CartItemView;
import vn.ubm.usedbookmarketplace.dto.CheckoutResult;
import vn.ubm.usedbookmarketplace.entity.AppUser;
import vn.ubm.usedbookmarketplace.entity.UserAddress;
import vn.ubm.usedbookmarketplace.repository.AppUserRepository;
import vn.ubm.usedbookmarketplace.service.CartService;
import vn.ubm.usedbookmarketplace.service.CheckoutService;
import vn.ubm.usedbookmarketplace.service.UserAddressService;

@Controller
@RequestMapping("/buyer/cart")
public class CartController {

	private final CartService cartService;
	private final CheckoutService checkoutService;
	private final AppUserRepository appUserRepository;
	private final UserAddressService userAddressService;

	public CartController(CartService cartService, CheckoutService checkoutService, AppUserRepository appUserRepository,
			UserAddressService userAddressService) {
		this.cartService = cartService;
		this.checkoutService = checkoutService;
		this.appUserRepository = appUserRepository;
		this.userAddressService = userAddressService;
	}

	@GetMapping
	public String viewCart(Principal principal, Model model) {
		if (principal == null) {
			return "redirect:/auth/login";
		}

		List<CartItemView> items = cartService.getItems(principal.getName());

		model.addAttribute("items", items);
		model.addAttribute("cartTotal", cartService.calculateTotal(items));

		return "buyer/cart";
	}

	@PostMapping("/add")
	public String addItem(Principal principal, @RequestParam Long listingId,
			@RequestParam(defaultValue = "1") int quantity, RedirectAttributes redirectAttributes) {
		if (principal == null) {
			return "redirect:/auth/login";
		}

		try {
			cartService.addItem(principal.getName(), listingId, quantity);
			redirectAttributes.addFlashAttribute("successMessage", "Book added to your cart.");
		} catch (IllegalArgumentException ex) {
			redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
		}

		return "redirect:/buyer/cart";
	}

	@PostMapping("/update")
	public String updateItem(Principal principal, @RequestParam Long cartItemId, @RequestParam int quantity,
			RedirectAttributes redirectAttributes) {
		if (principal == null) {
			return "redirect:/auth/login";
		}

		try {
			cartService.updateQuantity(principal.getName(), cartItemId, quantity);
			redirectAttributes.addFlashAttribute("successMessage", "Cart updated successfully.");
		} catch (IllegalArgumentException ex) {
			redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
		}

		return "redirect:/buyer/cart";
	}

	@PostMapping("/remove")
	public String removeItem(Principal principal, @RequestParam Long cartItemId,
			RedirectAttributes redirectAttributes) {
		if (principal == null) {
			return "redirect:/auth/login";
		}

		try {
			cartService.removeItem(principal.getName(), cartItemId);
			redirectAttributes.addFlashAttribute("successMessage", "Book removed from your cart.");
		} catch (IllegalArgumentException ex) {
			redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
		}

		return "redirect:/buyer/cart";
	}

	@GetMapping("/checkout")
	public String checkoutPage(Principal principal, Model model, RedirectAttributes redirectAttributes) {
		if (principal == null) {
			return "redirect:/auth/login";
		}

		AppUser user = appUserRepository.findByEmailIgnoreCase(principal.getName())
				.orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tài khoản người dùng."));

		List<CartItemView> items = cartService.getItems(principal.getName());

		if (items.isEmpty()) {
			redirectAttributes.addFlashAttribute("errorMessage", "Giỏ hàng hiện không có sản phẩm.");
			return "redirect:/buyer/cart";
		}

		List<UserAddress> addresses = userAddressService.getAddresses(user.getUserId());

		Long defaultAddressId = addresses.stream().filter(address -> Boolean.TRUE.equals(address.getIsDefault()))
				.map(UserAddress::getAddressId).findFirst()
				.orElse(addresses.isEmpty() ? null : addresses.get(0).getAddressId());

		model.addAttribute("items", items);
		model.addAttribute("cartTotal", cartService.calculateTotal(items));
		model.addAttribute("addresses", addresses);
		model.addAttribute("defaultAddressId", defaultAddressId);

		return "buyer/checkout";
	}

	@PostMapping("/checkout")
	public String placeOrder(Principal principal, @RequestParam Long addressId, @RequestParam String paymentMethod,
			@RequestParam(required = false) String buyerNote, Model model, RedirectAttributes redirectAttributes) {

		if (principal == null) {
			return "redirect:/auth/login";
		}

		try {
			String method = paymentMethod.trim().toUpperCase();

			if (!method.equals("COD") && !method.equals("MOMO")) {
				throw new IllegalArgumentException("Phương thức thanh toán không hợp lệ.");
			}

			CheckoutResult result = checkoutService.placeOrder(principal.getName(), addressId, method, buyerNote);

			model.addAttribute("orderIds", result.getOrderIds());
			model.addAttribute("totalAmount", result.getTotalAmount());
			model.addAttribute("paymentMethod", method);

			if ("MOMO".equals(method)) {
				return "buyer/payment-mock";
			}

			return "buyer/checkout-success";

		} catch (IllegalArgumentException ex) {
			redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
			return "redirect:/buyer/cart/checkout";
		}
	}

}