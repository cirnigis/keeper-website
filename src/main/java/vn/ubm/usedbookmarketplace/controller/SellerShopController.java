
package vn.ubm.usedbookmarketplace.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import vn.ubm.usedbookmarketplace.service.SellerShopService;

@Controller
@RequestMapping("/seller")
public class SellerShopController {

	private final SellerShopService sellerShopService;

	public SellerShopController(SellerShopService sellerShopService) {
		this.sellerShopService = sellerShopService;
	}

	@GetMapping("/shops")
	public String myShops(Authentication authentication, Model model) {
		String email = getEmail(authentication);

		model.addAttribute("shops", sellerShopService.getMyShops(email));

		model.addAttribute("approvedShops", sellerShopService.getMyApprovedActiveShops(email));

		return "seller/shops";
	}

	@GetMapping("/shop-register")
	public String createShopForm() {
		return "seller/shop-register";
	}

	@PostMapping("/shops/create")
	public String createShop(Authentication authentication, @RequestParam String cccd, @RequestParam String shopName,
			@RequestParam String shopSlug, @RequestParam String ownerType,
			@RequestParam(required = false) String description, @RequestParam(required = false) String contactPhone,
			@RequestParam(required = false) String contactEmail, @RequestParam String province,
			@RequestParam String district, @RequestParam String addressLine, RedirectAttributes redirectAttributes) {

		try {
			sellerShopService.createShop(getEmail(authentication), cccd, shopName, shopSlug, ownerType, description,
					contactPhone, contactEmail, province, district, addressLine);

			redirectAttributes.addFlashAttribute("successMessage",
					"Đăng ký shop thành công. Shop đang chờ quản trị viên xét duyệt.");

			return "redirect:/seller/shops";

		} catch (IllegalArgumentException ex) {
			redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());

			return "redirect:/seller/shop-register";
		}
	}

	@PostMapping("/shops/update")
	public String updateShop(Authentication authentication, @RequestParam long shopId, @RequestParam String shopName,
			@RequestParam String ownerType, @RequestParam(required = false) String description,
			@RequestParam(required = false) String contactPhone, @RequestParam(required = false) String contactEmail,
			@RequestParam String province, @RequestParam String district, @RequestParam String addressLine,
			RedirectAttributes redirectAttributes) {

		try {
			sellerShopService.updateShop(getEmail(authentication), shopId, shopName, ownerType, description,
					contactPhone, contactEmail, province, district, addressLine);

			redirectAttributes.addFlashAttribute("successMessage", "Cập nhật thông tin shop thành công.");

		} catch (IllegalArgumentException ex) {
			redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
		}

		return "redirect:/seller/shops";
	}

	private String getEmail(Authentication authentication) {
		if (authentication == null || !authentication.isAuthenticated() || authentication.getName() == null
				|| authentication.getName().isBlank()) {

			throw new IllegalArgumentException("Bạn cần đăng nhập để thực hiện thao tác này.");
		}

		return authentication.getName();
	}
}
