
package vn.ubm.usedbookmarketplace.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import vn.ubm.usedbookmarketplace.service.PasswordRecoveryService;

@Controller
@RequestMapping("/account/change-password")
public class ChangePasswordController {

	private final PasswordRecoveryService passwordRecoveryService;

	public ChangePasswordController(PasswordRecoveryService passwordRecoveryService) {
		this.passwordRecoveryService = passwordRecoveryService;
	}

	@GetMapping
	public String showChangePasswordPage(Authentication authentication) {

		if (authentication == null || !authentication.isAuthenticated()
				|| authentication instanceof org.springframework.security.authentication.AnonymousAuthenticationToken) {
			return "redirect:/auth/login";
		}

		return "account/change-password";
	}

	@PostMapping
	public String changePassword(@RequestParam("currentPassword") String currentPassword,
			@RequestParam("newPassword") String newPassword, @RequestParam("confirmPassword") String confirmPassword,
			Authentication authentication, RedirectAttributes redirectAttributes) {

		if (authentication == null || !authentication.isAuthenticated()
				|| authentication instanceof org.springframework.security.authentication.AnonymousAuthenticationToken) {
			return "redirect:/auth/login";
		}

		if (newPassword == null || newPassword.length() < 8) {
			redirectAttributes.addFlashAttribute("errorMessage",
					"The new password must contain at least 8 characters.");

			return "redirect:/account/change-password";
		}

		if (!newPassword.equals(confirmPassword)) {
			redirectAttributes.addFlashAttribute("errorMessage", "The password confirmation does not match.");

			return "redirect:/account/change-password";
		}

		if (newPassword.equals(currentPassword)) {
			redirectAttributes.addFlashAttribute("errorMessage",
					"The new password must be different from the current password.");

			return "redirect:/account/change-password";
		}

		try {
			passwordRecoveryService.changePassword(authentication.getName(), currentPassword, newPassword);

			redirectAttributes.addFlashAttribute("successMessage", "Your password has been changed successfully.");

		} catch (IllegalArgumentException ex) {
			redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());

		} catch (Exception ex) {
			redirectAttributes.addFlashAttribute("errorMessage",
					"Unable to change the password right now. Please try again.");
		}

		return "redirect:/account/change-password";
	}
}
