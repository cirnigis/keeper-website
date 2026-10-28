package vn.ubm.usedbookmarketplace.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.ubm.usedbookmarketplace.repository.SupportTicketRepository;

@Controller
public class SupportTicketController {
	private final SupportTicketRepository tickets;

	public SupportTicketController(SupportTicketRepository tickets) {
		this.tickets = tickets;
	}

	@GetMapping("/buyer/support")
	public String buyerSupport(Authentication auth, Model model) {
		var rows = tickets.findForUser(auth.getName());
		for (var row : rows)
			row.put("messages",
					tickets.findMessages(((Number) row.get("ticketId")).longValue(), auth.getName(), false));
		model.addAttribute("tickets", rows);
		return "buyer/support";
	}

	@PostMapping("/buyer/support/create")
	public String create(Authentication auth, @RequestParam(required = false) Long orderId,
			@RequestParam String subject, @RequestParam String description, RedirectAttributes flash) {
		try {
			long id = tickets.create(auth.getName(), orderId, subject, description);
			flash.addFlashAttribute("successMessage", "Đã tạo khiếu nại #" + id + ".");
		} catch (IllegalArgumentException ex) {
			flash.addFlashAttribute("errorMessage", ex.getMessage());
		}
		return "redirect:/buyer/support";
	}

	@GetMapping("/manager/support")
	public String managerQueue(Authentication auth, Model model) {
		var rows = tickets.findQueue(auth.getName(), false);
		for (var row : rows)
			row.put("messages", tickets.findMessages(((Number) row.get("ticketId")).longValue(), auth.getName(), true));
		model.addAttribute("tickets", rows);
		model.addAttribute("isAdmin", false);
		return "support/queue";
	}

	@GetMapping("/admin/support")
	public String adminQueue(Authentication auth, Model model) {
		var rows = tickets.findQueue(auth.getName(), true);
		for (var row : rows)
			row.put("messages", tickets.findMessages(((Number) row.get("ticketId")).longValue(), auth.getName(), true));
		model.addAttribute("tickets", rows);
		model.addAttribute("isAdmin", true);
		return "support/queue";
	}

	@PostMapping("/buyer/support/reply")
	public String buyerReply(Authentication auth, @RequestParam long ticketId, @RequestParam String message,
			RedirectAttributes flash) {
		try {
			tickets.reply(auth.getName(), ticketId, message, false, false, false);
			flash.addFlashAttribute("successMessage", "Đã gửi phản hồi.");
		} catch (IllegalArgumentException ex) {
			flash.addFlashAttribute("errorMessage", ex.getMessage());
		}
		return "redirect:/buyer/support";
	}

	@PostMapping({ "/manager/support/reply", "/admin/support/reply" })
	public String reply(Authentication auth, @RequestParam long ticketId, @RequestParam String message,
			@RequestParam(defaultValue = "false") boolean internal, RedirectAttributes flash) {
		boolean staff = hasRole(auth, "ROLE_MANAGER") || hasRole(auth, "ROLE_ADMIN");
		try {
			tickets.reply(auth.getName(), ticketId, message, internal, staff, hasRole(auth, "ROLE_ADMIN"));
			flash.addFlashAttribute("successMessage", "Đã gửi phản hồi.");
		} catch (IllegalArgumentException ex) {
			flash.addFlashAttribute("errorMessage", ex.getMessage());
		}
		return hasRole(auth, "ROLE_ADMIN") ? "redirect:/admin/support" : "redirect:/manager/support";
	}

	@PostMapping({ "/manager/support/status", "/admin/support/status" })
	public String status(Authentication auth, @RequestParam long ticketId, @RequestParam String status,
			RedirectAttributes flash) {
		boolean admin = hasRole(auth, "ROLE_ADMIN");
		try {
			tickets.updateStatus(auth.getName(), ticketId, status.trim().toUpperCase(), admin);
			flash.addFlashAttribute("successMessage", "Đã cập nhật trạng thái khiếu nại.");
		} catch (IllegalArgumentException ex) {
			flash.addFlashAttribute("errorMessage", ex.getMessage());
		}
		return admin ? "redirect:/admin/support" : "redirect:/manager/support";
	}

	private boolean hasRole(Authentication a, String role) {
		return a != null && a.getAuthorities().stream().map(GrantedAuthority::getAuthority).anyMatch(role::equals);
	}
}
