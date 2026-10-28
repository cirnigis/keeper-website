
package vn.ubm.usedbookmarketplace.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import vn.ubm.usedbookmarketplace.repository.ManagerDashboardRepository;
import vn.ubm.usedbookmarketplace.repository.AppUserRepository;

@Service
public class ManagerDashboardService {

	private final ManagerDashboardRepository dashboardRepository;
	private final AppUserRepository appUserRepository;

	public ManagerDashboardService(ManagerDashboardRepository dashboardRepository,
			AppUserRepository appUserRepository) {
		this.dashboardRepository = dashboardRepository;
		this.appUserRepository = appUserRepository;
	}

	@Transactional(readOnly = true)
	public List<Map<String, Object>> getAssignedShops(String email) {

		var user = appUserRepository.findByEmailIgnoreCase(email)
				.orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tài khoản đang đăng nhập."));

		return dashboardRepository.findAssignedShops(user.getUserId());
	}
}
