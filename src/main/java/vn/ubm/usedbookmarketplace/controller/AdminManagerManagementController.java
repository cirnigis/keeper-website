package vn.ubm.usedbookmarketplace.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.ubm.usedbookmarketplace.repository.AdminManagerManagementRepository;

@Controller
@RequestMapping("/admin/managers")
public class AdminManagerManagementController {
    private final AdminManagerManagementRepository repository;
    public AdminManagerManagementController(AdminManagerManagementRepository repository) { this.repository = repository; }

    @GetMapping
    public String index(Model model) {
        model.addAttribute("users", repository.findUsers());
        return "admin/manager-management";
    }

    @PostMapping("/role")
    public String changeRole(Authentication authentication, @RequestParam long userId,
            @RequestParam String action, RedirectAttributes flash) {
        try {
            if (!"GRANT".equals(action) && !"REVOKE".equals(action)) throw new IllegalArgumentException("Thao tác không hợp lệ.");
            repository.changeManagerRole(authentication.getName(), userId, "GRANT".equals(action));
            flash.addFlashAttribute("successMessage", "Đã cập nhật quyền Manager. Các quyền khác của tài khoản được giữ nguyên.");
        } catch (IllegalArgumentException | IllegalStateException ex) {
            flash.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/admin/managers";
    }
}
