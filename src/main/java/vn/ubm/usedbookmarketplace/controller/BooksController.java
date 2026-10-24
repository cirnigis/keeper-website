package vn.ubm.usedbookmarketplace.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import vn.ubm.usedbookmarketplace.service.BookCatalogService;

@Controller
public class BooksController {

	private final BookCatalogService bookCatalogService;

	public BooksController(BookCatalogService bookCatalogService) {
		this.bookCatalogService = bookCatalogService;
	}

	@GetMapping("/books")
	public String listBooks(@RequestParam(required = false) String keyword, Model model) {

		model.addAttribute("books", bookCatalogService.findActiveListings(keyword));
		model.addAttribute("keyword", keyword);

		return "books/index";
	}
}