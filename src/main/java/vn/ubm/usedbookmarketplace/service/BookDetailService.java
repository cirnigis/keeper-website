
package vn.ubm.usedbookmarketplace.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import vn.ubm.usedbookmarketplace.dto.BookDetail;
import vn.ubm.usedbookmarketplace.repository.BookDetailRepository;

@Service
public class BookDetailService {

	private final BookDetailRepository bookDetailRepository;

	public BookDetailService(BookDetailRepository bookDetailRepository) {
		this.bookDetailRepository = bookDetailRepository;
	}

	@Transactional(readOnly = true)
	public Optional<BookDetail> findActiveListingById(Long listingId) {
		return bookDetailRepository.findActiveListingById(listingId);
	}
}
