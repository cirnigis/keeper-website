
package vn.ubm.usedbookmarketplace.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import vn.ubm.usedbookmarketplace.dto.CartItemView;
import vn.ubm.usedbookmarketplace.entity.AppUser;
import vn.ubm.usedbookmarketplace.repository.AppUserRepository;
import vn.ubm.usedbookmarketplace.repository.CartRepository;

@Service
public class CartService {

	private final CartRepository cartRepository;
	private final AppUserRepository userRepository;

	public CartService(CartRepository cartRepository, AppUserRepository userRepository) {
		this.cartRepository = cartRepository;
		this.userRepository = userRepository;
	}

	private Long getUserId(String email) {
		AppUser user = userRepository.findByEmailIgnoreCase(email)
				.orElseThrow(() -> new IllegalArgumentException("User account was not found."));

		if (!"ACTIVE".equalsIgnoreCase(user.getAccountStatus())) {
			throw new IllegalArgumentException("Your account is not active.");
		}

		return user.getUserId();
	}

	private Long getOrCreateCartId(Long userId) {
		Long cartId = cartRepository.findCartIdByUserId(userId);

		return cartId != null ? cartId : cartRepository.createCart(userId);
	}

	@Transactional
	public void addItem(String email, Long listingId, int quantity) {
		if (listingId == null || listingId <= 0) {
			throw new IllegalArgumentException("Invalid book listing.");
		}

		if (quantity < 1) {
			throw new IllegalArgumentException("Quantity must be at least 1.");
		}

		Long userId = getUserId(email);

		Map<String, Object> listing = cartRepository.findAvailableListing(listingId);

		if (listing == null) {
			throw new IllegalArgumentException("This book is no longer available.");
		}

		int stock = ((Number) listing.get("stockQuantity")).intValue();
		Long cartId = getOrCreateCartId(userId);

		Integer existingQuantity = cartRepository.findCartItemQuantity(cartId, listingId);

		int newQuantity = quantity;

		if (existingQuantity != null) {
			newQuantity += existingQuantity;
		}

		if (newQuantity > stock) {
			throw new IllegalArgumentException("The requested quantity exceeds available stock (" + stock + ").");
		}

		if (existingQuantity == null) {
			cartRepository.insertItem(cartId, listingId, newQuantity);
		} else {
			cartRepository.updateItemQuantity(cartId, listingId, newQuantity);
		}

		cartRepository.updateCartTimestamp(cartId);
	}

	@Transactional
	public List<CartItemView> getItems(String email) {
		Long userId = getUserId(email);
		Long cartId = cartRepository.findCartIdByUserId(userId);

		if (cartId == null) {
			return List.of();
		}

		// Remove listings that are no longer eligible for sale.
		cartRepository.removeUnavailableItems(cartId);

		List<CartItemView> items = cartRepository.findCartItems(cartId);

		// If stock has decreased, adjust the saved quantity.
		for (CartItemView item : items) {
			if (item.getQuantity() > item.getStockQuantity()) {
				item.setQuantity(item.getStockQuantity());

				cartRepository.updateQuantityByItemId(cartId, item.getCartItemId(), item.getQuantity());
			}
		}

		cartRepository.updateCartTimestamp(cartId);
		return items;
	}

	@Transactional
	public void updateQuantity(String email, Long cartItemId, int quantity) {

		if (cartItemId == null || cartItemId <= 0) {
			throw new IllegalArgumentException("Invalid cart item.");
		}

		if (quantity < 1) {
			throw new IllegalArgumentException("Quantity must be at least 1.");
		}

		Long userId = getUserId(email);
		Long cartId = cartRepository.findCartIdByUserId(userId);

		if (cartId == null) {
			throw new IllegalArgumentException("Your shopping cart is empty.");
		}

		List<CartItemView> items = cartRepository.findCartItems(cartId);

		CartItemView selected = items.stream().filter(item -> item.getCartItemId().equals(cartItemId)).findFirst()
				.orElseThrow(() -> new IllegalArgumentException("This item is no longer available in your cart."));

		if (quantity > selected.getStockQuantity()) {
			throw new IllegalArgumentException("Only " + selected.getStockQuantity() + " copy/copies are available.");
		}

		int updated = cartRepository.updateQuantityByItemId(cartId, cartItemId, quantity);

		if (updated == 0) {
			throw new IllegalArgumentException("The cart item could not be updated.");
		}

		cartRepository.updateCartTimestamp(cartId);
	}

	@Transactional
	public void removeItem(String email, Long cartItemId) {
		Long userId = getUserId(email);
		Long cartId = cartRepository.findCartIdByUserId(userId);

		if (cartId == null) {
			throw new IllegalArgumentException("Your shopping cart is empty.");
		}

		int deleted = cartRepository.deleteItem(cartId, cartItemId);

		if (deleted == 0) {
			throw new IllegalArgumentException("The cart item was not found.");
		}

		cartRepository.updateCartTimestamp(cartId);
	}

	public BigDecimal calculateTotal(List<CartItemView> items) {
		return items.stream().map(CartItemView::getLineTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
	}
}
