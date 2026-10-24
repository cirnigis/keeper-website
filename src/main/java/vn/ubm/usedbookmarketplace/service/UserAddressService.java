package vn.ubm.usedbookmarketplace.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import vn.ubm.usedbookmarketplace.dto.UserAddress;
import vn.ubm.usedbookmarketplace.repository.UserAddressRepository;

@Service
public class UserAddressService {

	private final UserAddressRepository addressRepository;

	public UserAddressService(UserAddressRepository addressRepository) {
		this.addressRepository = addressRepository;
	}

	public List<UserAddress> getAddresses(Long userId) {
		return addressRepository.findAllByUserId(userId);
	}

	public void addAddress(Long userId, UserAddress address) {
		validate(address);
		addressRepository.insert(userId, address);

		// The first active address automatically becomes the default.
		List<UserAddress> addresses = addressRepository.findAllByUserId(userId);

		if (addressRepository.findDefaultAddressId(userId) == null && !addresses.isEmpty()) {
			Long firstAddressId = addresses.get(0).getAddressId();
			addressRepository.setDefault(firstAddressId, userId);
		}
	}

	public void setDefault(Long addressId, Long userId) {
		if (!addressRepository.belongsToUser(addressId, userId)) {
			throw new IllegalArgumentException("Address was not found.");
		}

		addressRepository.setDefault(addressId, userId);
	}

	public void deleteAddress(Long addressId, Long userId) {
		if (!addressRepository.belongsToUser(addressId, userId)) {
			throw new IllegalArgumentException("Address was not found.");
		}

		addressRepository.deleteAddress(addressId, userId);
	}

	private void validate(UserAddress address) {
		requireText(address.getRecipientName(), "Recipient name");
		requireText(address.getPhone(), "Phone");
		requireText(address.getProvince(), "Province");
		requireText(address.getDistrict(), "District");
		requireText(address.getWard(), "Ward");
		requireText(address.getAddressLine(), "Street address");

		if (address.getRecipientName().trim().length() > 150 || address.getProvince().trim().length() > 100
				|| address.getDistrict().trim().length() > 100 || address.getWard().trim().length() > 100
				|| address.getAddressLine().trim().length() > 300 || address.getPhone().trim().length() > 20) {
			throw new IllegalArgumentException("One or more address fields exceed the allowed length.");
		}
	}

	private void requireText(String value, String fieldName) {
		if (value == null || value.trim().isEmpty()) {
			throw new IllegalArgumentException(fieldName + " is required.");
		}
	}
}