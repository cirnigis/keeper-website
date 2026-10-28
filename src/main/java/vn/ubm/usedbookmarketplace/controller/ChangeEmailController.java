
package vn.ubm.usedbookmarketplace.controller;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import vn.ubm.usedbookmarketplace.entity.AppUser;
import vn.ubm.usedbookmarketplace.repository.AppUserRepository;
import vn.ubm.usedbookmarketplace.service.AuthService;

@Controller
@RequestMapping("/account/change-email")
public class ChangeEmailController {

	private static final String PENDING_EMAIL_SESSION_KEY = "pendingEmailChange";

	private final AuthService authService;
	private final AppUserRepository userRepository;

	public ChangeEmailController(AuthService authService, AppUserRepository userRepository) {
		this.authService = authService;
		this.userRepository = userRepository;
	}

	@GetMapping
	public String changeEmailPage(Principal principal, HttpSession session, Model model) {

		if (principal == null) {
			return "redirect:/auth/login";
		}

		AppUser user = userRepository.findByEmailIgnoreCase(principal.getName()).orElse(null);

		if (user == null) {
			model.addAttribute("errorMessage",
					"Vui lòng đăng xuất và đăng nhập lại bằng email mới để tiếp tục sử dụng.");

			model.addAttribute("currentEmail", principal.getName());
		} else {
			model.addAttribute("currentEmail", user.getEmail());
		}

		if (!model.containsAttribute("newEmail")) {
			Object pendingEmail = session.getAttribute(PENDING_EMAIL_SESSION_KEY);

			if (pendingEmail instanceof String email) {
				model.addAttribute("newEmail", email);
			}
		}

		return "account/change-email";
	}

	@PostMapping("/email/send-otp")
	public String sendEmailChangeOtp(@RequestParam("newEmail") String newEmail, Principal principal,
			HttpSession session, RedirectAttributes redirectAttributes) {

		if (principal == null) {
			return "redirect:/auth/login";
		}

		try {
			authService.sendEmailChangeOtp(principal.getName(), newEmail);

			session.setAttribute(PENDING_EMAIL_SESSION_KEY, newEmail.trim());

			redirectAttributes.addFlashAttribute("successMessage",
					"Mã xác minh đã được gửi đến email mới. Vui lòng kiểm tra hộp thư.");

			redirectAttributes.addFlashAttribute("newEmail", newEmail.trim());

		} catch (IllegalArgumentException ex) {
			redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());

			redirectAttributes.addFlashAttribute("newEmail", newEmail);

		} catch (Exception ex) {
			redirectAttributes.addFlashAttribute("errorMessage", "Không thể gửi mã xác minh. Vui lòng thử lại sau.");

			redirectAttributes.addFlashAttribute("newEmail", newEmail);
		}

		return "redirect:/account/change-email";
	}

	@PostMapping("/email/verify")
	public String verifyEmailChange(@RequestParam("otp") String otp, Principal principal, HttpSession session,
			RedirectAttributes redirectAttributes) {

		if (principal == null) {
			return "redirect:/auth/login";
		}

		Object pendingEmail = session.getAttribute(PENDING_EMAIL_SESSION_KEY);

		if (!(pendingEmail instanceof String newEmail) || newEmail.isBlank()) {

			redirectAttributes.addFlashAttribute("errorMessage",
					"Không tìm thấy email cần xác minh. Vui lòng yêu cầu gửi mã mới.");

			return "redirect:/account/change-email";
		}

		try {
			authService.verifyEmailChange(principal.getName(), newEmail, otp.trim());

			session.removeAttribute(PENDING_EMAIL_SESSION_KEY);

			redirectAttributes.addFlashAttribute("successMessage", "Đổi email thành công.");

		} catch (IllegalArgumentException ex) {
			redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());

		} catch (Exception ex) {
			redirectAttributes.addFlashAttribute("errorMessage", "Không thể xác minh email lúc này. Vui lòng thử lại.");
		}

		return "redirect:/account/change-email";
	}
}
