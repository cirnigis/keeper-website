
package vn.ubm.usedbookmarketplace.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import vn.ubm.usedbookmarketplace.repository.SellerListingRepository;

@Service
public class SellerListingService {

	private final SellerListingRepository listingRepository;
	private final SellerShopService shopService;

	private static final Set<String> CONDITIONS = Set.of("ACCEPTABLE", "GOOD", "VERY_GOOD", "LIKE_NEW");

	private static final Set<String> STATUSES = Set.of("ACTIVE", "HIDDEN", "REMOVED");

	public SellerListingService(SellerListingRepository listingRepository, SellerShopService shopService) {
		this.listingRepository = listingRepository;
		this.shopService = shopService;
	}

	public List<Map<String, Object>> getMyShops(String email) {
		return shopService.getMyShops(email);
	}

	public List<Map<String, Object>> getListings(String email, Long shopId) {
		requireApprovedShop(email, shopId);
		return listingRepository.findListings(email, shopId);
	}

	public List<Map<String, Object>> searchBooks(String keyword) {
		if (keyword != null && keyword.trim().length() > 200) {
			throw new IllegalArgumentException("Từ khóa tìm kiếm không được vượt quá 200 ký tự.");
		}

		return listingRepository.searchBooks(keyword);
	}

	@Transactional
	public void createListing(String email, Long shopId, Long bookId, String listingTitle, String conditionCode,
			BigDecimal unitPrice, int stockQuantity) {

		validateListing(shopId, bookId, listingTitle, conditionCode, unitPrice, stockQuantity);
		requireApprovedShop(email, shopId);

		listingRepository.createListing(email, shopId, bookId, clean(listingTitle), conditionCode, unitPrice,
				stockQuantity);
	}

	@Transactional
	public void updateListing(String email, Long shopId, Long listingId, String listingTitle, String conditionCode,
			BigDecimal unitPrice, int stockQuantity) {

		if (listingId == null || listingId <= 0) {
			throw new IllegalArgumentException("Sản phẩm không hợp lệ.");
		}

		validateListing(shopId, 1L, listingTitle, conditionCode, unitPrice, stockQuantity);
		requireApprovedShop(email, shopId);

		listingRepository.updateListing(email, shopId, listingId, clean(listingTitle), conditionCode, unitPrice,
				stockQuantity);
	}

	@Transactional
	public void changeStatus(String email, Long shopId, Long listingId, String status) {

		if (shopId == null || shopId <= 0 || listingId == null || listingId <= 0) {
			throw new IllegalArgumentException("Shop hoặc sản phẩm không hợp lệ.");
		}

		if (status == null || !STATUSES.contains(status)) {
			throw new IllegalArgumentException("Trạng thái sản phẩm không hợp lệ.");
		}

		requireApprovedShop(email, shopId);

		listingRepository.changeListingStatus(email, shopId, listingId, status);
	}

	private void requireApprovedShop(String email, Long shopId) {
		if (shopId == null || shopId <= 0) {
			throw new IllegalArgumentException("Vui lòng chọn shop.");
		}

		boolean exists = shopService.getMyShops(email).stream()
				.anyMatch(shop -> shopId.equals(toLong(shop.get("shopId")))
						&& "APPROVED".equals(String.valueOf(shop.get("approvalStatus")))
						&& isActive(shop.get("isActive")));

		if (!exists) {
			throw new IllegalArgumentException("Shop không tồn tại, chưa được duyệt hoặc không hoạt động.");
		}
	}

	private void validateListing(Long shopId, Long bookId, String listingTitle, String conditionCode,
			BigDecimal unitPrice, int stockQuantity) {

		if (shopId == null || shopId <= 0 || bookId == null || bookId <= 0) {
			throw new IllegalArgumentException("Vui lòng chọn shop và sách hợp lệ.");
		}

		if (listingTitle != null && listingTitle.trim().length() > 300) {
			throw new IllegalArgumentException("Tên sản phẩm không được vượt quá 300 ký tự.");
		}

		if (conditionCode == null || !CONDITIONS.contains(conditionCode)) {
			throw new IllegalArgumentException("Tình trạng sách không hợp lệ.");
		}

		if (unitPrice == null || unitPrice.compareTo(BigDecimal.ZERO) < 0 || unitPrice.scale() > 2
				|| unitPrice.precision() > 18) {
			throw new IllegalArgumentException("Giá bán phải hợp lệ và có tối đa 2 chữ số thập phân.");
		}

		if (stockQuantity < 0) {
			throw new IllegalArgumentException("Số lượng tồn kho không được âm.");
		}
	}

	private String clean(String value) {
		return value == null || value.isBlank() ? null : value.trim();
	}

	private Long toLong(Object value) {
		if (value instanceof Number number) {
			return number.longValue();
		}

		return value == null ? null : Long.valueOf(value.toString());
	}

	private boolean isActive(Object value) {
		if (value instanceof Boolean bool) {
			return bool;
		}

		if (value instanceof Number number) {
			return number.intValue() != 0;
		}

		return Boolean.parseBoolean(String.valueOf(value));
	}
}
