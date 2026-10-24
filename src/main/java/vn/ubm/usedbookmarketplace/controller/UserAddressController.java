package vn.ubm.usedbookmarketplace.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import vn.ubm.usedbookmarketplace.dto.UserAddress;
import vn.ubm.usedbookmarketplace.entity.AppUser;
import vn.ubm.usedbookmarketplace.repository.AppUserRepository;
import vn.ubm.usedbookmarketplace.service.UserAddressService;

@Controller
@RequestMapping("/buyer/addresses")
public class UserAddressController {

	private final UserAddressService addressService;
	private final AppUserRepository userRepository;

	public UserAddressController(UserAddressService addressService, AppUserRepository userRepository) {
		this.addressService = addressService;
		this.userRepository = userRepository;
	}

	@GetMapping
	public String listAddresses(Authentication authentication, Model model) {
		Long userId = getCurrentUserId(authentication);

		model.addAttribute("addresses", addressService.getAddresses(userId));
		model.addAttribute("newAddress", new UserAddress());

		return "buyer/addresses";
	}

	@PostMapping("/add")
	public String addAddress(@ModelAttribute("newAddress") UserAddress address, Authentication authentication,
			RedirectAttributes redirectAttributes) {

		try {
			addressService.addAddress(getCurrentUserId(authentication), address);
			redirectAttributes.addFlashAttribute("successMessage", "Address added successfully.");
		} catch (IllegalArgumentException ex) {
			redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
		}

		return "redirect:/buyer/addresses";
	}

	@PostMapping("/{addressId}/default")
	public String setDefault(@PathVariable Long addressId, Authentication authentication,
			RedirectAttributes redirectAttributes) {

		try {
			addressService.setDefault(addressId, getCurrentUserId(authentication));
			redirectAttributes.addFlashAttribute("successMessage", "Default address updated.");
		} catch (IllegalArgumentException ex) {
			redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
		}

		return "redirect:/buyer/addresses";
	}

	@PostMapping("/{addressId}/delete")
	public String deleteAddress(@PathVariable Long addressId, Authentication authentication,
			RedirectAttributes redirectAttributes) {

		try {
			addressService.deleteAddress(addressId, getCurrentUserId(authentication));
			redirectAttributes.addFlashAttribute("successMessage", "Address removed successfully.");
		} catch (IllegalArgumentException ex) {
			redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
		}

		return "redirect:/buyer/addresses";
	}

	private Long getCurrentUserId(Authentication authentication) {
		if (authentication == null || !authentication.isAuthenticated()) {
			throw new IllegalStateException("You must sign in first.");
		}

		AppUser user = userRepository.findByEmailIgnoreCase(authentication.getName())
				.orElseThrow(() -> new IllegalStateException("The current user could not be found."));

		return user.getUserId();
	}
}