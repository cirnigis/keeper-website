package vn.ubm.usedbookmarketplace.controller;

import java.util.Set;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import vn.ubm.usedbookmarketplace.service.SellerShopService;

@Controller
@RequestMapping("/buyer/seller-register")
public class BuyerSellerRegistrationController {

	private final SellerShopService sellerShopService;

	public BuyerSellerRegistrationController(SellerShopService sellerShopService) {
		this.sellerShopService = sellerShopService;
	}

	@GetMapping
	public String showForm(Authentication authentication, RedirectAttributes redirectAttributes) {
		if (!hasRole(authentication, "ROLE_BUYER")) {
			redirectAttributes.addFlashAttribute("errorMessage",
					"Bạn cần đăng nhập bằng tài khoản Buyer để đăng ký làm người bán.");
			return "redirect:/auth/login";
		}
		if (hasRole(authentication, "ROLE_SELLER")) {
			return "redirect:/seller/shop-register";
		}
		return "buyer/seller-register";
	}

	@PostMapping
	public String submitForm(Authentication authentication, @RequestParam String cccd, @RequestParam String shopName,
			@RequestParam String shopSlug, @RequestParam String ownerType,
			@RequestParam(required = false) String description, @RequestParam(required = false) String contactPhone,
			@RequestParam(required = false) String contactEmail, @RequestParam String province,
			@RequestParam String district, @RequestParam String addressLine, RedirectAttributes redirectAttributes,
			Model model) {

		if (!hasRole(authentication, "ROLE_BUYER")) {
			return "redirect:/auth/login";
		}
		if (hasRole(authentication, "ROLE_SELLER")) {
			return "redirect:/seller/shop-register";
		}

		try {
			sellerShopService.createShop(authentication.getName(), cccd, shopName, shopSlug, ownerType, description,
					contactPhone, contactEmail, province, district, addressLine);
			redirectAttributes.addFlashAttribute("successMessage",
					"Đã gửi yêu cầu đăng ký người bán. Shop đang chờ Manager/Admin xét duyệt.");
			return "redirect:/buyer/dashboard";
		} catch (IllegalArgumentException ex) {
			model.addAttribute("errorMessage", ex.getMessage());
			model.addAttribute("cccdValue", cccd);
			model.addAttribute("shopNameValue", shopName);
			model.addAttribute("shopSlugValue", shopSlug);
			model.addAttribute("contactPhoneValue", contactPhone);
			model.addAttribute("contactEmailValue", contactEmail);
			model.addAttribute("provinceValue", province);
			model.addAttribute("districtValue", district);
			model.addAttribute("addressLineValue", addressLine);
			model.addAttribute("descriptionValue", description);
			return "buyer/seller-register";
		}
	}

	private boolean hasRole(Authentication authentication, String role) {
		if (authentication == null || !authentication.isAuthenticated())
			return false;
		return authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).anyMatch(role::equals);
	}
}
