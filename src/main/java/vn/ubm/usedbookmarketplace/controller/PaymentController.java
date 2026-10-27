package vn.ubm.usedbookmarketplace.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import vn.ubm.usedbookmarketplace.service.PaymentService;

@Controller
@RequestMapping("/buyer/cart/payment")
public class PaymentController {

	private final PaymentService paymentService;

	public PaymentController(PaymentService paymentService) {
		this.paymentService = paymentService;
	}

	@PostMapping("/mock-result")
    public String processMockResult(
            Principal principal,
            @RequestParam(required = false) List<Long> orderIds,
            @RequestParam String result,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (principal == null) {
            return "redirect:/login";
        }

        try {
            paymentService.processMomoMockResult(
                    principal.getName(),
                    orderIds,
                    result);

            if ("SUCCESS".equalsIgnoreCase(result)) {
                redirectAttributes.addFlashAttribute(
                        "successMessage",
                        "Thanh toán MoMo thành công.");
            } else {
                redirectAttributes.addFlashAttribute(
                        "errorMessage",
                        "Thanh toán MoMo thất bại. Bạn có thể thử thanh toán lại.");
            }

            return "redirect:/buyer/orders";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("orderIds", orderIds);
            model.addAttribute("result", result);
            return "buyer/payment-result";
        }
    }
}