
package vn.ubm.usedbookmarketplace.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import vn.ubm.usedbookmarketplace.entity.UserAddress;
import vn.ubm.usedbookmarketplace.repository.UserAddressRepository;

@Service
public class UserAddressService {

	private final UserAddressRepository addressRepository;

	public UserAddressService(UserAddressRepository addressRepository) {
		this.addressRepository = addressRepository;
	}

	@Transactional(readOnly = true)
	public List<UserAddress> getAddresses(Long userId) {
		requireValidUserId(userId);
		return addressRepository.findAllByUserId(userId);
	}

	@Transactional
	public void addAddress(Long userId, UserAddress address) {
		requireValidUserId(userId);

		if (address == null) {
			throw new IllegalArgumentException("Address is required.");
		}

		validate(address);

		Long newAddressId = addressRepository.insert(userId, address);

		if (addressRepository.findDefaultAddressId(userId) == null) {
			addressRepository.setDefault(newAddressId, userId);
		}
	}

	@Transactional
	public void setDefault(Long addressId, Long userId) {
		requireValidUserId(userId);
		requireValidAddressId(addressId);

		if (!addressRepository.belongsToUser(addressId, userId)) {
			throw new IllegalArgumentException("Address was not found.");
		}

		addressRepository.setDefault(addressId, userId);
	}

	@Transactional
	public void deleteAddress(Long addressId, Long userId) {
		requireValidUserId(userId);
		requireValidAddressId(addressId);

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

		if (address.getRecipientName().trim().length() > 150 || address.getPhone().trim().length() > 20
				|| address.getProvince().trim().length() > 100 || address.getDistrict().trim().length() > 100
				|| address.getWard().trim().length() > 100 || address.getAddressLine().trim().length() > 300) {
			throw new IllegalArgumentException("One or more address fields exceed the allowed length.");
		}
	}

	private void requireText(String value, String fieldName) {
		if (value == null || value.trim().isEmpty()) {
			throw new IllegalArgumentException(fieldName + " is required.");
		}
	}

	private void requireValidUserId(Long userId) {
		if (userId == null || userId <= 0) {
			throw new IllegalArgumentException("Invalid user ID.");
		}
	}

	private void requireValidAddressId(Long addressId) {
		if (addressId == null || addressId <= 0) {
			throw new IllegalArgumentException("Invalid address ID.");
		}
	}
}
