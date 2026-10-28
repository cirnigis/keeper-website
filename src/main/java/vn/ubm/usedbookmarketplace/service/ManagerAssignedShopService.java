
package vn.ubm.usedbookmarketplace.service;

import org.springframework.stereotype.Service;
import vn.ubm.usedbookmarketplace.repository.ManagerAssignedShopRepository;

import java.util.List;
import java.util.Map;

@Service
public class ManagerAssignedShopService {

	private final ManagerAssignedShopRepository repository;

	public ManagerAssignedShopService(ManagerAssignedShopRepository repository) {
		this.repository = repository;
	}

	public List<Map<String, Object>> getAssignedShops(int managerUserId) {

		if (managerUserId <= 0) {
			throw new IllegalArgumentException("Manager user ID không hợp lệ.");
		}

		return repository.findAssignedShops(managerUserId);
	}
}
