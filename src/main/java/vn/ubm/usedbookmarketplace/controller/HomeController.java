package vn.ubm.usedbookmarketplace.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import vn.ubm.usedbookmarketplace.service.BookCatalogService;

@Controller
public class HomeController {

	private final BookCatalogService bookCatalogService;

	public HomeController(BookCatalogService bookCatalogService) {
		this.bookCatalogService = bookCatalogService;
	}

	@GetMapping("/")
	public String home(Model model, Authentication authentication) {
		model.addAttribute("books", bookCatalogService.findActiveListings(null));

		String userRole = "";

		if (authentication != null && authentication.isAuthenticated()) {
			userRole = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority)
					.filter(role -> role.startsWith("ROLE_"))
					.sorted((role1, role2) -> Integer.compare(getRolePriority(role1), getRolePriority(role2)))
					.findFirst().orElse("");
		}

		model.addAttribute("userRole", userRole);

		return "home/index";
	}

	private int getRolePriority(String role) {
		return switch (role) {
		case "ROLE_ADMIN" -> 1;
		case "ROLE_MANAGER" -> 2;
		case "ROLE_SELLER" -> 3;
		case "ROLE_SHIPPER" -> 4;
		case "ROLE_BUYER" -> 5;
		default -> 99;
		};
	}
}
