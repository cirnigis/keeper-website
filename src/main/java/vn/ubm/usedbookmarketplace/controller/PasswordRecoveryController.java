package vn.ubm.usedbookmarketplace.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import vn.ubm.usedbookmarketplace.service.PasswordRecoveryService;

@Controller
@RequestMapping("/auth")
public class PasswordRecoveryController {

	private final PasswordRecoveryService passwordRecoveryService;

	public PasswordRecoveryController(PasswordRecoveryService passwordRecoveryService) {
		this.passwordRecoveryService = passwordRecoveryService;
	}

	@GetMapping("/forgot-password")
	public String forgotPassword() {
		return "auth/forgot-password";
	}

	@PostMapping("/forgot-password")
	public String sendResetCode(@RequestParam("email") String email, RedirectAttributes redirectAttributes) {

		try {
			passwordRecoveryService.requestPasswordReset(email);
		} catch (Exception ex) {
			redirectAttributes.addFlashAttribute("errorMessage",
					"Unable to send the reset code right now. Please try again later.");
			return "redirect:/auth/forgot-password";
		}

		redirectAttributes.addFlashAttribute("successMessage",
				"If this email belongs to an active, verified account, a reset code has been sent.");

		// Carry the email to the next page without putting it in the URL.
		redirectAttributes.addFlashAttribute("resetEmail", email.trim());
		return "redirect:/auth/reset-password";
	}

	@GetMapping("/reset-password")
	public String resetPasswordPage() {
		return "auth/reset-password";
	}

	@PostMapping("/reset-password")
	public String resetPassword(@RequestParam("email") String email, @RequestParam("otp") String otp,
			@RequestParam("newPassword") String newPassword, @RequestParam("confirmPassword") String confirmPassword,
			RedirectAttributes redirectAttributes) {

		if (newPassword == null || newPassword.length() < 8) {
			redirectAttributes.addFlashAttribute("errorMessage",
					"The new password must contain at least 8 characters.");
			redirectAttributes.addFlashAttribute("resetEmail", email);
			return "redirect:/auth/reset-password";
		}

		if (!newPassword.equals(confirmPassword)) {
			redirectAttributes.addFlashAttribute("errorMessage", "The password confirmation does not match.");
			redirectAttributes.addFlashAttribute("resetEmail", email);
			return "redirect:/auth/reset-password";
		}

		try {
			boolean reset = passwordRecoveryService.resetPassword(email, otp, newPassword);

			if (!reset) {
				redirectAttributes.addFlashAttribute("errorMessage",
						"The code is invalid, expired, or has reached its attempt limit.");
				redirectAttributes.addFlashAttribute("resetEmail", email);
				return "redirect:/auth/reset-password";
			}
		} catch (Exception ex) {
			redirectAttributes.addFlashAttribute("errorMessage",
					"Unable to reset the password right now. Please try again.");
			redirectAttributes.addFlashAttribute("resetEmail", email);
			return "redirect:/auth/reset-password";
		}

		redirectAttributes.addFlashAttribute("successMessage",
				"Your password has been reset. Please sign in with your new password.");
		return "redirect:/auth/login";
	}
}
