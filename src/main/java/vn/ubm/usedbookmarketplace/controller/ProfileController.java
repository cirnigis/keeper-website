
package vn.ubm.usedbookmarketplace.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.UUID;

import javax.imageio.ImageIO;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import vn.ubm.usedbookmarketplace.entity.AppUser;
import vn.ubm.usedbookmarketplace.repository.AppUserRepository;

@Controller
public class ProfileController {

	private static final long MAX_AVATAR_SIZE = 5L * 1024 * 1024;

	private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of("image/jpeg", "image/png");

	private static final String[] PROFILE_PATHS = { "/buyer/profile", "/seller/profile", "/manager/profile",
			"/admin/profile", "/shipper/profile" };

	private final AppUserRepository userRepository;

	@Value("${app.upload-dir:uploads}")
	private String uploadDir;

	public ProfileController(AppUserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@GetMapping({ "/buyer/profile", "/seller/profile", "/manager/profile", "/admin/profile", "/shipper/profile" })
	public String showProfile(Authentication authentication, HttpServletRequest request, Model model) {

		if (!isAuthenticated(authentication)) {
			return "redirect:/auth/login";
		}

		AppUser user = userRepository.findByEmailIgnoreCase(authentication.getName())
				.orElseThrow(() -> new IllegalStateException("Không tìm thấy tài khoản hiện tại."));

		String profilePath = getProfilePath(request);

		model.addAttribute("profileUser", user);
		model.addAttribute("profilePath", profilePath);
		model.addAttribute("dashboardPath", getDashboardPath(profilePath));

		// Dùng để hiển thị tên vai trò nếu JSP có sử dụng.
		model.addAttribute("roleName", authentication.getAuthorities().stream()
				.map(authority -> authority.getAuthority()).findFirst().orElse("Tài khoản Keeper"));

		return "profile/index";
	}

	@PostMapping({ "/buyer/profile", "/seller/profile", "/manager/profile", "/admin/profile", "/shipper/profile" })
	public String updateProfile(Authentication authentication, HttpServletRequest request,
			@RequestParam("fullName") String fullName, @RequestParam(value = "phone", required = false) String phone,
			@RequestParam(value = "avatarFile", required = false) MultipartFile avatarFile,
			RedirectAttributes redirectAttributes) {

		if (!isAuthenticated(authentication)) {
			return "redirect:/auth/login";
		}

		String profilePath = getProfilePath(request);
		String cleanName = fullName == null ? "" : fullName.trim();
		String cleanPhone = phone == null ? "" : phone.trim();

		if (cleanName.isEmpty() || cleanName.length() > 150) {
			redirectAttributes.addFlashAttribute("errorMessage", "Họ tên không được để trống và tối đa 150 ký tự.");

			return "redirect:" + profilePath;
		}

		if (cleanPhone.length() > 20) {
			redirectAttributes.addFlashAttribute("errorMessage", "Số điện thoại không được vượt quá 20 ký tự.");

			return "redirect:" + profilePath;
		}

		AppUser user = userRepository.findByEmailIgnoreCase(authentication.getName())
				.orElseThrow(() -> new IllegalStateException("Không tìm thấy tài khoản hiện tại."));

		if (avatarFile != null && !avatarFile.isEmpty()) {
			String validationError = validateAvatar(avatarFile);

			if (validationError != null) {
				redirectAttributes.addFlashAttribute("errorMessage", validationError);

				return "redirect:" + profilePath;
			}

			try {
				String avatarUrl = saveAvatar(avatarFile);
				user.setAvatarUrl(avatarUrl);
			} catch (IOException exception) {
				redirectAttributes.addFlashAttribute("errorMessage", "Không thể lưu ảnh đại diện. Vui lòng thử lại.");

				return "redirect:" + profilePath;
			}
		}

		user.setFullName(cleanName);
		user.setPhone(cleanPhone.isEmpty() ? null : cleanPhone);

		userRepository.save(user);

		redirectAttributes.addFlashAttribute("successMessage", "Cập nhật hồ sơ thành công.");

		return "redirect:" + profilePath;
	}

	private boolean isAuthenticated(Authentication authentication) {
		return authentication != null && authentication.isAuthenticated()
				&& !(authentication instanceof AnonymousAuthenticationToken);
	}

	private String validateAvatar(MultipartFile file) {
		if (file.getSize() > MAX_AVATAR_SIZE) {
			return "Ảnh đại diện không được vượt quá 5 MB.";
		}

		String contentType = file.getContentType();

		if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
			return "Chỉ chấp nhận ảnh PNG hoặc JPG.";
		}

		try (var inputStream = file.getInputStream()) {
			if (ImageIO.read(inputStream) == null) {
				return "Tệp tải lên không phải ảnh hợp lệ.";
			}
		} catch (IOException exception) {
			return "Không thể đọc tệp ảnh. Vui lòng thử lại.";
		}

		return null;
	}

	private String saveAvatar(MultipartFile file) throws IOException {
		String contentType = file.getContentType();
		String extension = "image/png".equals(contentType) ? ".png" : ".jpg";

		Path avatarDirectory = Paths.get(uploadDir, "avatars").toAbsolutePath().normalize();

		Files.createDirectories(avatarDirectory);

		String fileName = UUID.randomUUID() + extension;
		Path destination = avatarDirectory.resolve(fileName).normalize();

		if (!destination.startsWith(avatarDirectory)) {
			throw new IOException("Đường dẫn lưu ảnh không hợp lệ.");
		}

		file.transferTo(destination);

		return "/uploads/avatars/" + fileName;
	}

	private String getProfilePath(HttpServletRequest request) {
		String path = request.getRequestURI().substring(request.getContextPath().length());

		for (String profilePath : PROFILE_PATHS) {
			if (profilePath.equals(path)) {
				return path;
			}
		}

		return "/buyer/profile";
	}

	private String getDashboardPath(String profilePath) {
		if (profilePath.startsWith("/seller/")) {
			return "/seller/dashboard";
		}

		if (profilePath.startsWith("/manager/")) {
			return "/manager/dashboard";
		}

		if (profilePath.startsWith("/admin/")) {
			return "/admin/dashboard";
		}

		if (profilePath.startsWith("/shipper/")) {
			return "/shipper/dashboard";
		}

		return "/buyer/dashboard";
	}
}
