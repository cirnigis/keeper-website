
package vn.ubm.usedbookmarketplace.controller;

import java.math.BigDecimal;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import vn.ubm.usedbookmarketplace.service.SellerListingService;

@Controller
@RequestMapping("/seller/listings")
public class SellerListingController {

	private final SellerListingService listingService;

	public SellerListingController(SellerListingService listingService) {
		this.listingService = listingService;
	}

	@GetMapping
	public String listListings(Authentication authentication, @RequestParam(required = false) Long shopId,
			Model model) {

		model.addAttribute("shops", listingService.getMyShops(authentication.getName()));

		if (shopId != null) {
			try {
				model.addAttribute("listings", listingService.getListings(authentication.getName(), shopId));
				model.addAttribute("selectedShopId", shopId);
			} catch (IllegalArgumentException ex) {
				model.addAttribute("errorMessage", ex.getMessage());
			}
		}

		return "seller/listings";
	}

	@GetMapping("/new")
	public String newListing(Authentication authentication, @RequestParam(required = false) Long shopId,
			@RequestParam(required = false) String keyword, Model model) {

		model.addAttribute("shops", listingService.getMyShops(authentication.getName()));

		model.addAttribute("selectedShopId", shopId);
		model.addAttribute("keyword", keyword);

		if (shopId != null) {
			try {
				listingService.getListings(authentication.getName(), shopId);

				model.addAttribute("books", listingService.searchBooks(keyword));
			} catch (IllegalArgumentException ex) {
				model.addAttribute("errorMessage", ex.getMessage());
			}
		}

		return "seller/listing-create";
	}

	@PostMapping("/create")
	public String createListing(Authentication authentication, @RequestParam Long shopId, @RequestParam Long bookId,
			@RequestParam(required = false) String listingTitle, @RequestParam String conditionCode,
			@RequestParam BigDecimal unitPrice, @RequestParam int stockQuantity,
			RedirectAttributes redirectAttributes) {

		try {
			listingService.createListing(authentication.getName(), shopId, bookId, listingTitle, conditionCode,
					unitPrice, stockQuantity);

			redirectAttributes.addFlashAttribute("successMessage", "Đã đăng bán sách thành công.");

		} catch (IllegalArgumentException ex) {
			redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
			return "redirect:/seller/listings/new?shopId=" + shopId;
		}

		return "redirect:/seller/listings?shopId=" + shopId;
	}

	@PostMapping("/update")
	public String updateListing(Authentication authentication, @RequestParam Long shopId, @RequestParam Long listingId,
			@RequestParam(required = false) String listingTitle, @RequestParam String conditionCode,
			@RequestParam BigDecimal unitPrice, @RequestParam int stockQuantity,
			RedirectAttributes redirectAttributes) {

		try {
			listingService.updateListing(authentication.getName(), shopId, listingId, listingTitle, conditionCode,
					unitPrice, stockQuantity);

			redirectAttributes.addFlashAttribute("successMessage", "Đã cập nhật sản phẩm.");

		} catch (IllegalArgumentException ex) {
			redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
		}

		return "redirect:/seller/listings?shopId=" + shopId;
	}

	@PostMapping("/status")
	public String changeStatus(Authentication authentication, @RequestParam Long shopId, @RequestParam Long listingId,
			@RequestParam String status, RedirectAttributes redirectAttributes) {

		try {
			listingService.changeStatus(authentication.getName(), shopId, listingId, status);

			redirectAttributes.addFlashAttribute("successMessage", "Đã cập nhật trạng thái sản phẩm.");

		} catch (IllegalArgumentException ex) {
			redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
		}

		return "redirect:/seller/listings?shopId=" + shopId;
	}
}
