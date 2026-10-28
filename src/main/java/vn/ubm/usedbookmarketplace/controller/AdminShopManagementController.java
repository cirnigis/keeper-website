package vn.ubm.usedbookmarketplace.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.ubm.usedbookmarketplace.repository.AdminShopManagementRepository;

@Controller
@RequestMapping("/admin/shops/manage")
public class AdminShopManagementController {
    private final AdminShopManagementRepository repository;
    public AdminShopManagementController(AdminShopManagementRepository repository) { this.repository=repository; }
    @GetMapping public String index(Model model) { model.addAttribute("shops", repository.findAll()); return "admin/shop-management"; }
    @PostMapping public String update(Authentication auth, @RequestParam long shopId, @RequestParam String action,
            @RequestParam String reason, RedirectAttributes flash) {
        try { repository.changeStatus(auth.getName(), shopId, action.trim().toUpperCase(), reason); flash.addFlashAttribute("successMessage", "Đã cập nhật cửa hàng."); }
        catch (IllegalArgumentException ex) { flash.addFlashAttribute("errorMessage", ex.getMessage()); }
        return "redirect:/admin/shops/manage";
    }
}
