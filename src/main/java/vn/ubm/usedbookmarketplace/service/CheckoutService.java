
package vn.ubm.usedbookmarketplace.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import vn.ubm.usedbookmarketplace.dto.CheckoutResult;
import vn.ubm.usedbookmarketplace.entity.AppUser;
import vn.ubm.usedbookmarketplace.entity.UserAddress;
import vn.ubm.usedbookmarketplace.repository.AppUserRepository;
import vn.ubm.usedbookmarketplace.repository.CartRepository;
import vn.ubm.usedbookmarketplace.repository.OrderRepository;
import vn.ubm.usedbookmarketplace.repository.UserAddressRepository;

@Service
public class CheckoutService {

	private static final BigDecimal SHIPPING_FEE = BigDecimal.ZERO;

	private final AppUserRepository appUserRepository;
	private final CartRepository cartRepository;
	private final UserAddressRepository userAddressRepository;
	private final OrderRepository orderRepository;
	private final CartService cartService;
	private final PaymentService paymentService;

	public CheckoutService(AppUserRepository appUserRepository, CartRepository cartRepository,
			UserAddressRepository userAddressRepository, OrderRepository orderRepository, CartService cartService,
			PaymentService paymentService) {

		this.appUserRepository = appUserRepository;
		this.cartRepository = cartRepository;
		this.userAddressRepository = userAddressRepository;
		this.orderRepository = orderRepository;
		this.cartService = cartService;
		this.paymentService = paymentService;
	}

	@Transactional
	public CheckoutResult placeOrder(String email, Long addressId, String paymentMethod, String buyerNote) {

		if (email == null || email.isBlank()) {
			throw new IllegalArgumentException("Vui lòng đăng nhập trước khi đặt hàng.");
		}

		if (addressId == null || addressId <= 0) {
			throw new IllegalArgumentException("Vui lòng chọn địa chỉ giao hàng.");
		}

		String normalizedPaymentMethod = normalizePaymentMethod(paymentMethod);
		String normalizedNote = normalizeNote(buyerNote);

		AppUser user = appUserRepository.findByEmailIgnoreCase(email.trim())
				.orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tài khoản người dùng."));

		if (!"ACTIVE".equalsIgnoreCase(user.getAccountStatus())) {
			throw new IllegalArgumentException("Tài khoản chưa hoạt động nên không thể đặt hàng.");
		}

		List<UserAddress> addresses = userAddressRepository.findAllByUserId(user.getUserId());

		UserAddress selectedAddress = addresses.stream().filter(address -> addressId.equals(address.getAddressId()))
				.findFirst().orElseThrow(() -> new IllegalArgumentException(
						"Địa chỉ không tồn tại, không hoạt động hoặc không thuộc tài khoản của bạn."));

		Long cartId = cartRepository.findCartIdByUserId(user.getUserId());

		if (cartId == null) {
			throw new IllegalArgumentException("Không tìm thấy giỏ hàng.");
		}

		List<Map<String, Object>> lines = orderRepository.findCheckoutItemsForUpdate(cartId);

		if (lines == null || lines.isEmpty()) {
			throw new IllegalArgumentException("Giỏ hàng không có sản phẩm hợp lệ để đặt hàng.");
		}

		Map<Long, List<Map<String, Object>>> linesByShop = new LinkedHashMap<>();

		for (Map<String, Object> line : lines) {
			Long listingId = asLong(line.get("listingId"));
			Long shopId = asLong(line.get("shopId"));
			Long cartItemId = asLong(line.get("cartItemId"));

			int quantity = asInt(line.get("quantity"));
			int stockQuantity = asInt(line.get("stockQuantity"));

			if (listingId <= 0 || shopId <= 0 || cartItemId <= 0) {
				throw new IllegalArgumentException("Thông tin sản phẩm trong giỏ hàng không hợp lệ.");
			}

			if (quantity <= 0 || quantity > stockQuantity) {
				throw new IllegalArgumentException("Số lượng sản phẩm trong giỏ không còn phù hợp với tồn kho.");
			}

			String bookTitle = asNonBlankString(line.get("bookTitle"), "Tên sách không hợp lệ.");

			String conditionCode = asNonBlankString(line.get("conditionCode"), "Tình trạng sách không hợp lệ.");

			BigDecimal unitPrice = asBigDecimal(line.get("unitPrice"));

			if (unitPrice.signum() < 0) {
				throw new IllegalArgumentException("Giá sản phẩm không hợp lệ.");
			}

			linesByShop.computeIfAbsent(shopId, key -> new ArrayList<>()).add(line);
		}

		CheckoutResult result = new CheckoutResult();
		BigDecimal grandTotal = BigDecimal.ZERO;

		for (Map.Entry<Long, List<Map<String, Object>>> shopEntry : linesByShop.entrySet()) {

			Long shopId = shopEntry.getKey();
			List<Map<String, Object>> shopLines = shopEntry.getValue();

			BigDecimal subtotal = BigDecimal.ZERO;

			for (Map<String, Object> line : shopLines) {
				BigDecimal unitPrice = asBigDecimal(line.get("unitPrice"));
				int quantity = asInt(line.get("quantity"));

				subtotal = subtotal.add(unitPrice.multiply(BigDecimal.valueOf(quantity)));
			}

			BigDecimal shippingFee = SHIPPING_FEE;
			BigDecimal orderTotal = subtotal.add(shippingFee);

			Long orderId = orderRepository.createOrder(user.getUserId(), shopId, normalizedPaymentMethod, subtotal,
					shippingFee, selectedAddress.getAddressId(), normalizedNote);

			for (Map<String, Object> line : shopLines) {
				Long listingId = asLong(line.get("listingId"));
				Long cartItemId = asLong(line.get("cartItemId"));

				String bookTitle = asNonBlankString(line.get("bookTitle"), "Tên sách không hợp lệ.");

				String conditionCode = asNonBlankString(line.get("conditionCode"), "Tình trạng sách không hợp lệ.");

				BigDecimal unitPrice = asBigDecimal(line.get("unitPrice"));
				int quantity = asInt(line.get("quantity"));

				Long orderItemId = orderRepository.createOrderItem(orderId, listingId, bookTitle, conditionCode,
						unitPrice, quantity);

				orderRepository.reserveInventory(orderItemId, listingId, quantity, normalizedPaymentMethod);

				orderRepository.removeCartItem(cartId, cartItemId);
			}

			paymentService.createPayment(orderId, normalizedPaymentMethod, orderTotal);

			String initialStatus = "COD".equals(normalizedPaymentMethod) ? "PENDING_SELLER" : "PENDING_PAYMENT";

			orderRepository.createStatusHistory(orderId, user.getUserId(), initialStatus, "Đơn hàng được tạo");

			result.addOrderId(orderId);
			grandTotal = grandTotal.add(orderTotal);
		}

		result.setTotalAmount(grandTotal);

		return result;
	}

	private String normalizePaymentMethod(String paymentMethod) {
		if (paymentMethod == null) {
			throw new IllegalArgumentException("Vui lòng chọn phương thức thanh toán.");
		}

		String method = paymentMethod.trim().toUpperCase();

		if (!method.equals("COD") && !method.equals("MOMO")) {
			throw new IllegalArgumentException("Phương thức thanh toán không hợp lệ.");
		}

		return method;
	}

	private String normalizeNote(String buyerNote) {
		if (buyerNote == null || buyerNote.isBlank()) {
			return null;
		}

		String note = buyerNote.trim();

		if (note.length() > 2000) {
			throw new IllegalArgumentException("Ghi chú đơn hàng không được vượt quá 2000 ký tự.");
		}

		return note;
	}

	private Long asLong(Object value) {
		if (value instanceof Number number) {
			return number.longValue();
		}

		throw new IllegalArgumentException("Dữ liệu ID sản phẩm không hợp lệ.");
	}

	private int asInt(Object value) {
		if (value instanceof Number number) {
			return number.intValue();
		}

		throw new IllegalArgumentException("Số lượng sản phẩm không hợp lệ.");
	}

	private BigDecimal asBigDecimal(Object value) {
		if (value instanceof BigDecimal decimal) {
			return decimal;
		}

		if (value instanceof Number number) {
			return new BigDecimal(number.toString());
		}

		throw new IllegalArgumentException("Giá sản phẩm không hợp lệ.");
	}

	private String asNonBlankString(Object value, String errorMessage) {
		if (value == null || value.toString().isBlank()) {
			throw new IllegalArgumentException(errorMessage);
		}

		return value.toString().trim();
	}
}
