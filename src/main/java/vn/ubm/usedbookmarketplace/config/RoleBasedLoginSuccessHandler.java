package vn.ubm.usedbookmarketplace.config;

import java.io.IOException;
import java.util.Set;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import vn.ubm.usedbookmarketplace.service.SellerShopService;

@Component
public class RoleBasedLoginSuccessHandler implements AuthenticationSuccessHandler {

	private final SellerShopService sellerShopService;

	public RoleBasedLoginSuccessHandler(SellerShopService sellerShopService) {
		this.sellerShopService = sellerShopService;
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException {

		Set<String> authorities = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority)
				.collect(java.util.stream.Collectors.toSet());

		// Manager/Admin/Shipper use their own areas, regardless of the Buyer/Seller
		// selector.
		String target;
		if (authorities.contains("ROLE_ADMIN")) {
			target = "/admin/dashboard";
		} else if (authorities.contains("ROLE_MANAGER")) {
			target = "/manager/dashboard";
		} else if (authorities.contains("ROLE_SHIPPER")) {
			target = "/shipper/dashboard";
		} else {
			String selectedRole = request.getParameter("loginRole");
			if ("SELLER".equalsIgnoreCase(selectedRole)) {
				if (authorities.contains("ROLE_SELLER")) {
					// A role alone is not enough: seller area requires an approved, active shop.
					boolean hasEligibleShop;
					try {
						hasEligibleShop = !sellerShopService.getMyApprovedActiveShops(authentication.getName())
								.isEmpty();
					} catch (RuntimeException ex) {
						hasEligibleShop = false;
					}
					target = hasEligibleShop ? "/seller/dashboard" : "/seller/shops?status=shop-inactive";
				} else if (authorities.contains("ROLE_BUYER")) {
					// Selecting Seller never grants SELLER; Buyer must submit a shop application.
					target = "/buyer/seller-register?status=apply";
				} else {
					target = "/auth/login?roleError=seller";
				}
			} else if ("BUYER".equalsIgnoreCase(selectedRole)) {
				if (authorities.contains("ROLE_BUYER")) {
					target = "/buyer/dashboard";
				} else if (authorities.contains("ROLE_SELLER")) {
					target = "/seller/shops?status=buyer-role-unavailable";
				} else {
					target = "/auth/login?roleError=buyer";
				}
			} else if (authorities.contains("ROLE_SELLER")) {
				boolean hasEligibleShop;
				try {
					hasEligibleShop = !sellerShopService.getMyApprovedActiveShops(authentication.getName()).isEmpty();
				} catch (RuntimeException ex) {
					hasEligibleShop = false;
				}
				target = hasEligibleShop ? "/seller/dashboard" : "/seller/shops?status=shop-inactive";
			} else {
				target = "/buyer/dashboard";
			}
		}

		response.sendRedirect(response.encodeRedirectURL(request.getContextPath() + target));
	}
}
