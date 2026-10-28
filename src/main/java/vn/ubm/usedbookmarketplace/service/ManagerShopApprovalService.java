
package vn.ubm.usedbookmarketplace.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import vn.ubm.usedbookmarketplace.repository.ManagerShopApprovalRepository;

@Service
public class ManagerShopApprovalService {

	private final ManagerShopApprovalRepository repository;

	public ManagerShopApprovalService(ManagerShopApprovalRepository repository) {
		this.repository = repository;
	}

	/**
	 * Lấy danh sách shop đang chờ duyệt.
	 *
	 * Admin: xem tất cả shop đang chờ duyệt. Manager: chỉ xem shop thuộc phạm vi
	 * được phân công.
	 */
	@Transactional(readOnly = true)
	public List<Map<String, Object>> findPendingShops(String actorEmail, boolean admin) {

		validateActorEmail(actorEmail);

		return repository.findPendingShops(actorEmail.trim(), admin);
	}

	/**
	 * Duyệt hoặc từ chối đăng ký shop.
	 *
	 * Việc kiểm tra trạng thái, phạm vi được phân công, cấp role SELLER và ghi lịch
	 * sử được xử lý tại repository.
	 */
	@Transactional
	public void reviewShop(String actorEmail, boolean admin, long shopId, String decision, String note) {

		validateActorEmail(actorEmail);

		if (shopId <= 0) {
			throw new IllegalArgumentException("Shop không hợp lệ.");
		}

		if (!StringUtils.hasText(decision)) {
			throw new IllegalArgumentException("Vui lòng chọn quyết định xét duyệt.");
		}

		String normalizedDecision = decision.trim().toUpperCase(java.util.Locale.ROOT);

		if (!"APPROVED".equals(normalizedDecision) && !"REJECTED".equals(normalizedDecision)) {
			throw new IllegalArgumentException("Quyết định duyệt không hợp lệ.");
		}

		if (!StringUtils.hasText(note)) {
			throw new IllegalArgumentException("Vui lòng nhập ghi chú xét duyệt.");
		}

		String normalizedNote = note.trim();

		if (normalizedNote.length() > 1000) {
			throw new IllegalArgumentException("Ghi chú không được vượt quá 1000 ký tự.");
		}

		repository.reviewShop(actorEmail.trim(), admin, shopId, normalizedDecision, normalizedNote);
	}

	private void validateActorEmail(String actorEmail) {
		if (!StringUtils.hasText(actorEmail)) {
			throw new IllegalArgumentException("Không xác định được tài khoản xét duyệt.");
		}
	}
}
