package vn.ubm.usedbookmarketplace.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import vn.ubm.usedbookmarketplace.service.SellerDashboardService;
import vn.ubm.usedbookmarketplace.repository.AdminManagerDashboardRepository;

@Controller
public class DashboardController {

	private final SellerDashboardService sellerDashboardService;
	private final AdminManagerDashboardRepository adminManagerDashboardRepository;

	public DashboardController(SellerDashboardService sellerDashboardService,
			AdminManagerDashboardRepository adminManagerDashboardRepository) {
		this.sellerDashboardService = sellerDashboardService;
		this.adminManagerDashboardRepository = adminManagerDashboardRepository;
	}

	@GetMapping("/buyer/dashboard")
	public String buyerDashboard() {
		return "buyer/dashboard";
	}

	@GetMapping("/seller/dashboard")
	public String sellerDashboard(Authentication authentication, Model model) {
		model.addAllAttributes(sellerDashboardService.getDashboard(authentication.getName()));
		return "seller/dashboard";
	}

	@GetMapping("/manager/dashboard")
	public String managerDashboard(Authentication authentication, Model model) {
		model.addAttribute("summary", adminManagerDashboardRepository.getManagerSummary(authentication.getName()));
		model.addAttribute("shops", adminManagerDashboardRepository.findAssignedShops(authentication.getName()));
		return "manager/dashboard";
	}

	@GetMapping("/admin/dashboard")
	public String adminDashboard(Model model) {
		model.addAttribute("summary", adminManagerDashboardRepository.getAdminSummary());
		model.addAttribute("managers", adminManagerDashboardRepository.findManagers());
		return "admin/dashboard";
	}
}