package vn.ubm.usedbookmarketplace.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import vn.ubm.usedbookmarketplace.repository.ManagerShopApprovalRepository;

@Controller
@RequestMapping("/manager/shop-approvals")
public class ManagerShopApprovalController {
	private final ManagerShopApprovalRepository repository;

	public ManagerShopApprovalController(ManagerShopApprovalRepository repository) {
		this.repository = repository;
	}

	@GetMapping
	public String list(Authentication authentication, Model model) {
		boolean admin = hasRole(authentication, "ROLE_ADMIN");
		model.addAttribute("pendingShops", repository.findPendingShops(authentication.getName(), admin));
		model.addAttribute("isAdmin", admin);
		return "manager/shop-approvals";
	}

	@PostMapping("/review")
	public String review(Authentication authentication, @RequestParam long shopId, @RequestParam String decision,
			@RequestParam String note, RedirectAttributes redirectAttributes) {
		try {
			repository.reviewShop(authentication.getName(), hasRole(authentication, "ROLE_ADMIN"), shopId,
					decision == null ? "" : decision.trim().toUpperCase(), note);
			redirectAttributes.addFlashAttribute("successMessage", "Đã cập nhật kết quả xét duyệt shop.");
		} catch (IllegalArgumentException ex) {
			redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
		}
		return "redirect:/manager/shop-approvals";
	}

	private boolean hasRole(Authentication authentication, String role) {
		return authentication != null
				&& authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).anyMatch(role::equals);
	}
}
