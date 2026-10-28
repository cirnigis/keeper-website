package vn.ubm.usedbookmarketplace.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.ubm.usedbookmarketplace.repository.AdminManagerAssignmentRepository;

@Controller
@RequestMapping("/admin/manager-assignments")
public class AdminManagerAssignmentController {
	private final AdminManagerAssignmentRepository repository;

	public AdminManagerAssignmentController(AdminManagerAssignmentRepository repository) {
		this.repository = repository;
	}

	@GetMapping
	public String index(Model model) {
		model.addAttribute("managers", repository.findActiveManagers());
		model.addAttribute("shops", repository.findEligibleShops());
		model.addAttribute("assignments", repository.findAssignments());
		return "admin/manager-assignments";
	}

	@PostMapping("/assign")
	public String assign(Authentication auth, @RequestParam long managerUserId, @RequestParam long shopId,
			@RequestParam(required = false, defaultValue = "") String note, RedirectAttributes flash) {
		try {
			repository.assign(auth.getName(), managerUserId, shopId, note);
			flash.addFlashAttribute("successMessage", "Đã phân công Manager phụ trách cửa hàng.");
		} catch (IllegalArgumentException | IllegalStateException ex) {
			flash.addFlashAttribute("errorMessage", ex.getMessage());
		}
		return "redirect:/admin/manager-assignments";
	}

	@PostMapping("/end")
	public String end(@RequestParam long shopId, @RequestParam long managerUserId, RedirectAttributes flash) {
		try {
			repository.endAssignment(managerUserId, shopId);
			flash.addFlashAttribute("successMessage", "Đã kết thúc phân công.");
		} catch (IllegalArgumentException ex) {
			flash.addFlashAttribute("errorMessage", ex.getMessage());
		}
		return "redirect:/admin/manager-assignments";
	}
}
