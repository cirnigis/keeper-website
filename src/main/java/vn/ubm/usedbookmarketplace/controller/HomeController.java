package vn.ubm.usedbookmarketplace.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import vn.ubm.usedbookmarketplace.service.BookCatalogService;

@Controller
public class HomeController {

	private final BookCatalogService bookCatalogService;

	public HomeController(BookCatalogService bookCatalogService) {
		this.bookCatalogService = bookCatalogService;
	}

	@GetMapping("/")
	public String home(Model model) {
		model.addAttribute("books", bookCatalogService.findActiveListings(null));

		return "home/index";
	}
}