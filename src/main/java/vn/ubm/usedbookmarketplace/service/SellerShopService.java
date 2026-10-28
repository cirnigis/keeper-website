
package vn.ubm.usedbookmarketplace.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import vn.ubm.usedbookmarketplace.repository.SellerShopRepository;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

@Service
public class SellerShopService {

	private static final Pattern SHOP_SLUG_PATTERN = Pattern.compile("^[a-z0-9]+(?:-[a-z0-9]+)*$");

	private static final Pattern CCCD_PATTERN = Pattern.compile("^\\d{12}$");

	private static final int MAX_SHOP_NAME_LENGTH = 160;
	private static final int MAX_DESCRIPTION_LENGTH = 2000;
	private static final int MAX_PHONE_LENGTH = 30;
	private static final int MAX_EMAIL_LENGTH = 254;
	private static final int MAX_PROVINCE_LENGTH = 100;
	private static final int MAX_DISTRICT_LENGTH = 100;
	private static final int MAX_ADDRESS_LENGTH = 300;

	private final SellerShopRepository sellerShopRepository;

	public SellerShopService(SellerShopRepository sellerShopRepository) {
		this.sellerShopRepository = sellerShopRepository;
	}

	@Transactional(readOnly = true)
	public List<Map<String, Object>> getMyShops(String email) {
		requireEmail(email);
		return sellerShopRepository.findByOwnerEmail(email.trim());
	}

	@Transactional(readOnly = true)
	public List<Map<String, Object>> getMyApprovedActiveShops(String email) {
		requireEmail(email);
		return sellerShopRepository.findApprovedActiveShopsByOwnerEmail(email.trim());
	}

	@Transactional(readOnly = true)
	public boolean canManageShop(String email, long shopId) {
		if (email == null || email.isBlank() || shopId <= 0) {
			return false;
		}

		return sellerShopRepository.isApprovedActiveShopOwner(email.trim(), shopId);
	}

	/**
	 * Chuẩn hóa và kiểm tra CCCD.
	 */
	private String normalizeCccd(String cccd) {
		if (cccd == null || cccd.isBlank()) {
			throw new IllegalArgumentException("CCCD không được để trống.");
		}

		String normalized = cccd.trim();

		if (!CCCD_PATTERN.matcher(normalized).matches()) {
			throw new IllegalArgumentException("CCCD phải gồm đúng 12 chữ số.");
		}

		return normalized;
	}

	/**
	 * Tạo shop và lưu CCCD cho tài khoản đang đăng nhập.
	 */
	@Transactional
	public void createShop(String email, String cccd, String shopName, String shopSlug, String ownerType,
			String description, String contactPhone, String contactEmail, String province, String district,
			String addressLine) {

		requireEmail(email);

		String normalizedCccd = normalizeCccd(cccd);

		String normalizedShopName = requiredText(shopName, "Tên shop", MAX_SHOP_NAME_LENGTH);

		String normalizedSlug = requiredText(shopSlug, "Đường dẫn shop", 180).toLowerCase(Locale.ROOT);

		if (!SHOP_SLUG_PATTERN.matcher(normalizedSlug).matches()) {
			throw new IllegalArgumentException("Đường dẫn shop chỉ được chứa chữ thường không dấu, "
					+ "số và dấu gạch ngang; không được bắt đầu hoặc " + "kết thúc bằng dấu gạch ngang.");
		}

		String normalizedOwnerType = normalizeOwnerType(ownerType);

		String normalizedDescription = optionalText(description, "Mô tả", MAX_DESCRIPTION_LENGTH);

		String normalizedPhone = optionalText(contactPhone, "Số điện thoại", MAX_PHONE_LENGTH);

		String normalizedContactEmail = optionalText(contactEmail, "Email liên hệ", MAX_EMAIL_LENGTH);

		String normalizedProvince = requiredText(province, "Tỉnh/Thành phố", MAX_PROVINCE_LENGTH);

		String normalizedDistrict = requiredText(district, "Quận/Huyện", MAX_DISTRICT_LENGTH);

		String normalizedAddress = requiredText(addressLine, "Địa chỉ", MAX_ADDRESS_LENGTH);

		if (sellerShopRepository.existsShopSlug(normalizedSlug)) {
			throw new IllegalArgumentException("Đường dẫn shop đã được sử dụng. " + "Vui lòng chọn đường dẫn khác.");
		}

		Long ownerUserId = sellerShopRepository.findUserIdByEmail(email.trim());

		// Không cho phép thay đổi CCCD đã lưu trên tài khoản.
		String existingCccd = sellerShopRepository.findCccdByUserId(ownerUserId);

		if (existingCccd != null && !existingCccd.isBlank() && !existingCccd.trim().equals(normalizedCccd)) {
			throw new IllegalArgumentException("Tài khoản đã có CCCD. " + "Vui lòng sử dụng CCCD đã đăng ký.");
		}

		// Lưu CCCD nếu tài khoản chưa có CCCD.
		if (existingCccd == null || existingCccd.isBlank()) {
			sellerShopRepository.updateUserCccd(ownerUserId, normalizedCccd);
		}

		// Chỉ tạo shop một lần.
		sellerShopRepository.createShop(ownerUserId, normalizedShopName, normalizedSlug, normalizedOwnerType,
				normalizedDescription, normalizedPhone, normalizedContactEmail, normalizedProvince, normalizedDistrict,
				normalizedAddress);
	}

	/**
	 * Cập nhật thông tin shop.
	 */
	@Transactional
	public void updateShop(String email, long shopId, String shopName, String ownerType, String description,
			String contactPhone, String contactEmail, String province, String district, String addressLine) {

		requireEmail(email);

		if (shopId <= 0) {
			throw new IllegalArgumentException("Shop không hợp lệ.");
		}

		String normalizedShopName = requiredText(shopName, "Tên shop", MAX_SHOP_NAME_LENGTH);

		String normalizedOwnerType = normalizeOwnerType(ownerType);

		String normalizedDescription = optionalText(description, "Mô tả", MAX_DESCRIPTION_LENGTH);

		String normalizedPhone = optionalText(contactPhone, "Số điện thoại", MAX_PHONE_LENGTH);

		String normalizedContactEmail = optionalText(contactEmail, "Email liên hệ", MAX_EMAIL_LENGTH);

		String normalizedProvince = requiredText(province, "Tỉnh/Thành phố", MAX_PROVINCE_LENGTH);

		String normalizedDistrict = requiredText(district, "Quận/Huyện", MAX_DISTRICT_LENGTH);

		String normalizedAddress = requiredText(addressLine, "Địa chỉ", MAX_ADDRESS_LENGTH);

		Long ownerUserId = sellerShopRepository.findUserIdByEmail(email.trim());

		sellerShopRepository.updateShop(shopId, ownerUserId, normalizedShopName, normalizedOwnerType,
				normalizedDescription, normalizedPhone, normalizedContactEmail, normalizedProvince, normalizedDistrict,
				normalizedAddress);
	}

	private void requireEmail(String email) {
		if (email == null || email.isBlank()) {
			throw new IllegalArgumentException("Không xác định được tài khoản đang đăng nhập.");
		}
	}

	private String requiredText(String value, String fieldName, int maxLength) {

		if (value == null || value.isBlank()) {
			throw new IllegalArgumentException(fieldName + " không được để trống.");
		}

		String normalized = value.trim();

		if (normalized.length() > maxLength) {
			throw new IllegalArgumentException(fieldName + " không được vượt quá " + maxLength + " ký tự.");
		}

		return normalized;
	}

	private String optionalText(String value, String fieldName, int maxLength) {

		if (value == null || value.isBlank()) {
			return null;
		}

		String normalized = value.trim();

		if (normalized.length() > maxLength) {
			throw new IllegalArgumentException(fieldName + " không được vượt quá " + maxLength + " ký tự.");
		}

		return normalized;
	}

	private String normalizeOwnerType(String ownerType) {
		if (ownerType == null || ownerType.isBlank()) {
			throw new IllegalArgumentException("Loại chủ shop không được để trống.");
		}

		String normalized = ownerType.trim().toUpperCase(Locale.ROOT);

		if (!normalized.equals("INDIVIDUAL") && !normalized.equals("BUSINESS")) {
			throw new IllegalArgumentException("Loại chủ shop phải là INDIVIDUAL hoặc BUSINESS.");
		}

		return normalized;
	}
}
