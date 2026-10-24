
package vn.ubm.usedbookmarketplace.controller;

import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vn.ubm.usedbookmarketplace.dto.RegisterRequest;
import vn.ubm.usedbookmarketplace.dto.VerifyEmailRequest;
import vn.ubm.usedbookmarketplace.service.AuthService;

@Controller
@RequestMapping("/auth")
public class AuthController {

	private final AuthService authService;

	private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

	public AuthController(AuthService authService) {
		this.authService = authService;
	}

	@GetMapping("/register")
	public String registerPage(Model model) {
		if (!model.containsAttribute("registerRequest")) {
			model.addAttribute("registerRequest", new RegisterRequest());
		}

		return "auth/register";
	}

	@PostMapping("/register")
	public String register(@Valid @ModelAttribute("registerRequest") RegisterRequest request,
			BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {

		if (bindingResult.hasErrors()) {
			return "auth/register";
		}

		try {
			authService.register(request);

			redirectAttributes.addFlashAttribute("successMessage",
					"Registration initiated successfully. Please check your email for the OTP.");

			redirectAttributes.addFlashAttribute("email", request.getEmail());

			return "redirect:/auth/verify-email";

		} catch (IllegalArgumentException ex) {
			model.addAttribute("errorMessage", ex.getMessage());
			return "auth/register";

		} catch (Exception ex) {
			logger.error("Registration failed for email: {}", request.getEmail(), ex);

			model.addAttribute("errorMessage", "Registration failed. Please check the application log.");

			return "auth/register";
		}
	}

	@GetMapping("/verify-email")
	public String verifyEmailPage(Model model) {
		if (!model.containsAttribute("verifyRequest")) {
			VerifyEmailRequest request = new VerifyEmailRequest();

			Object email = model.getAttribute("email");

			if (email instanceof String value) {
				request.setEmail(value);
			}

			model.addAttribute("verifyRequest", request);
		}

		return "auth/verify-email";
	}

	@PostMapping("/verify-email")
	public String verifyEmail(@Valid @ModelAttribute("verifyRequest") VerifyEmailRequest request,
			BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {

		if (bindingResult.hasErrors()) {
			return "auth/verify-email";
		}

		try {
			authService.verifyEmail(request);

			return "redirect:/auth/registration-success";

		} catch (IllegalArgumentException ex) {
			model.addAttribute("errorMessage", ex.getMessage());
			return "auth/verify-email";
		}
	}

	@GetMapping("/registration-success")
	public String registrationSuccessPage() {
		return "auth/registration-success";
	}

	@GetMapping("/login")
	public String loginPage(@RequestParam(required = false) String error, @RequestParam(required = false) String logout,
			Model model) {

		if (error != null) {
			model.addAttribute("errorMessage", "Login failed. Please check your email, password, and account status.");
		}

		if (logout != null) {
			model.addAttribute("successMessage", "You have been logged out successfully.");
		}

		return "auth/login";
	}
}
