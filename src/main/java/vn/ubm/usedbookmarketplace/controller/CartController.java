
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
import vn.ubm.usedbookmarketplace.service.CartService;

@Controller
@RequestMapping("/buyer/cart")
public class CartController {

	private final CartService cartService;

	public CartController(CartService cartService) {
		this.cartService = cartService;
	}

	@GetMapping
	public String viewCart(Principal principal, Model model) {
		List<CartItemView> items = cartService.getItems(principal.getName());

		model.addAttribute("items", items);
		model.addAttribute("cartTotal", cartService.calculateTotal(items));

		return "buyer/cart";
	}

	@PostMapping("/add")
	public String addItem(Principal principal, @RequestParam Long listingId,
			@RequestParam(defaultValue = "1") int quantity, RedirectAttributes redirectAttributes) {

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

		try {
			cartService.removeItem(principal.getName(), cartItemId);

			redirectAttributes.addFlashAttribute("successMessage", "Book removed from your cart.");
		} catch (IllegalArgumentException ex) {
			redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
		}

		return "redirect:/buyer/cart";
	}
}
