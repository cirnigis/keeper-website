package vn.ubm.usedbookmarketplace.service;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import vn.ubm.usedbookmarketplace.repository.SellerDashboardRepository;

@Service
public class SellerDashboardService {

	private final SellerDashboardRepository dashboardRepository;

	public SellerDashboardService(SellerDashboardRepository dashboardRepository) {
		this.dashboardRepository = dashboardRepository;
	}

	public Map<String, Object> getDashboard(String email) {
		Map<String, Object> dashboard = new LinkedHashMap<>();

		dashboard.put("shopSummary", dashboardRepository.getShopSummary(email));
		dashboard.put("listingSummary", dashboardRepository.getListingSummary(email));
		dashboard.put("orderSummary", dashboardRepository.getOrderSummary(email));
		dashboard.put("recentOrders", dashboardRepository.findRecentOrders(email, 5));
		dashboard.put("shops", dashboardRepository.findMyShops(email));

		return dashboard;
	}
}