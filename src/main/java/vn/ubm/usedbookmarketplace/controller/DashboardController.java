
package vn.ubm.usedbookmarketplace.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;

@Controller
public class DashboardController {

	@GetMapping("/buyer/dashboard")
	public String buyerDashboard(Model model) {
		setDashboard(model, "Buyer Dashboard", "Manage your shopping activity and account.", "BUYER",
				List.of(card("Browse Books", "Search and explore used books.", "/books", "Available"),
						card("Shopping Cart", "Review the books you want to purchase.", "/buyer/cart", "Available"),
						card("Delivery Addresses", "Manage your saved delivery addresses.", "/buyer/addresses",
								"Available"),
						card("Change Password", "Update your account password.", "/buyer/change-password", "Available"),
						card("My Orders", "View purchases and track deliveries.", null, "Coming soon"),
						card("My Reviews", "Review books you have purchased.", null, "Coming soon"),
						card("Open a Shop", "Apply to become a seller.", null, "Coming soon")));
		return "dashboard/index";
	}

	@GetMapping("/seller/dashboard")
	public String sellerDashboard(Model model) {
		setDashboard(model, "Seller Dashboard", "Manage your shop, listings, and customer orders.", "SELLER",
				List.of(card("Shop Overview", "View your shop information and status.", null, "Coming soon"),
						card("Manage Listings", "Create listings and update prices or stock.", null, "Coming soon"),
						card("Manage Orders", "Process orders placed by customers.", null, "Coming soon"),
						card("Sales Reports", "Review sales and shop performance.", null, "Coming soon"),
						card("Customer Messages", "Communicate with buyers.", null, "Coming soon")));
		return "dashboard/index";
	}

	@GetMapping("/manager/dashboard")
	public String managerDashboard(Model model) {
		setDashboard(model, "Manager Dashboard", "Review and support the shops assigned to you.", "MANAGER",
				List.of(card("Assigned Shops", "View and monitor your assigned shops.", null, "Coming soon"),
						card("Shop Applications", "Review shop applications assigned to you.", null, "Coming soon"),
						card("Order Monitoring", "Monitor order issues and shop operations.", null, "Coming soon"),
						card("Support Tickets", "Handle support requests and escalate cases.", null, "Coming soon"),
						card("Messages", "Communicate with buyers and sellers.", null, "Coming soon")));
		return "dashboard/index";
	}

	@GetMapping("/admin/dashboard")
	public String adminDashboard(Model model) {
		setDashboard(model, "Admin Dashboard", "Manage marketplace operations and platform settings.", "ADMIN",
				List.of(card("User Management", "Manage user accounts and access.", null, "Coming soon"),
						card("Shop Management", "Review and manage shops across the platform.", null, "Coming soon"),
						card("Categories", "Manage book categories.", null, "Coming soon"),
						card("Authors and Publishers", "Maintain book metadata.", null, "Coming soon"),
						card("Support Escalations", "Review cases escalated by managers.", null, "Coming soon"),
						card("Commission Settings", "Configure marketplace commission rules.", null, "Coming soon")));
		return "dashboard/index";
	}

	@GetMapping("/shipper/dashboard")
	public String shipperDashboard(Model model) {
		setDashboard(model, "Shipper Dashboard", "Manage your assigned deliveries.", "SHIPPER",
				List.of(card("Assigned Deliveries", "View deliveries assigned to you.", null, "Coming soon"),
						card("Delivery History", "Review completed delivery tasks.", null, "Coming soon"),
						card("Delivery Issues", "Report failed or delayed deliveries.", null, "Coming soon")));
		return "dashboard/index";
	}

	private void setDashboard(Model model, String title, String description, String role,
			List<Map<String, String>> cards) {

		model.addAttribute("pageTitle", title);
		model.addAttribute("pageDescription", description);
		model.addAttribute("roleName", role);
		model.addAttribute("cards", cards);
	}

	private static Map<String, String> card(String title, String description, String url, String status) {

		return Map.of("title", title, "description", description, "url", url == null ? "" : url, "status", status);
	}
}
