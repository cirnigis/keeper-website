
package vn.ubm.usedbookmarketplace.controller;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

import vn.ubm.usedbookmarketplace.dto.BookDetail;
import vn.ubm.usedbookmarketplace.service.BookDetailService;

@Controller
public class BookDetailController {

	private final BookDetailService bookDetailService;

	public BookDetailController(BookDetailService bookDetailService) {
		this.bookDetailService = bookDetailService;
	}

	@GetMapping("/books/{listingId}")
	public String bookDetail(@PathVariable Long listingId, Model model) {

		BookDetail book = bookDetailService.findActiveListingById(listingId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book listing not found"));

		model.addAttribute("book", book);

		return "books/detail";
	}
}
