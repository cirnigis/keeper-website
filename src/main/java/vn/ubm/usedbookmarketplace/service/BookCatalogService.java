package vn.ubm.usedbookmarketplace.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import vn.ubm.usedbookmarketplace.dto.BookCatalogItem;
import vn.ubm.usedbookmarketplace.repository.BookCatalogRepository;

@Service
public class BookCatalogService {

	private final BookCatalogRepository bookCatalogRepository;

	public BookCatalogService(BookCatalogRepository bookCatalogRepository) {
		this.bookCatalogRepository = bookCatalogRepository;
	}

	@Transactional(readOnly = true)
	public List<BookCatalogItem> findActiveListings(String keyword) {
		return bookCatalogRepository.findActiveListings(keyword);
	}
}