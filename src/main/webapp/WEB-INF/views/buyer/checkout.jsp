<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<!DOCTYPE html>
<html lang="vi">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Thanh toán | Keeper</title>
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/style.css">
<link rel="stylesheet"
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css">
</head>
<body class="checkout-page">
	<header class="checkout-navbar">
		<div class="checkout-navbar-inner">
			<a class="keeper-brand" href="${pageContext.request.contextPath}/"
				aria-label="Keeper - Trang chủ"> <span class="keeper-brand-icon">K</span>
				<span class="keeper-brand-text">eeper</span>
			</a> <a class="checkout-back-link"
				href="${pageContext.request.contextPath}/buyer/cart"> Quay lại
				giỏ hàng </a>
		</div>
	</header>

	<main class="checkout-container">
		<div class="checkout-heading">
			<h1>Thanh toán</h1>
			<p>Kiểm tra thông tin giao hàng và xác nhận đơn hàng của bạn.</p>
		</div>

		<c:if test="${not empty errorMessage}">
			<div class="alert alert-danger" role="alert">
				<c:out value="${errorMessage}" />
			</div>
		</c:if>

		<c:if test="${empty items}">
			<section class="checkout-empty">
				<div class="checkout-empty-icon">🛒</div>
				<h2>Giỏ hàng đang trống</h2>
				<p>Hãy chọn sách trước khi tiến hành thanh toán.</p>
				<a class="btn keeper-btn-primary"
					href="${pageContext.request.contextPath}/books"> Khám phá sách
				</a>
			</section>
		</c:if>

		<c:if test="${not empty items}">
			<form method="post"
				action="${pageContext.request.contextPath}/buyer/cart/checkout">
				<input type="hidden" name="${_csrf.parameterName}"
					value="${_csrf.token}">

				<div class="checkout-layout">
					<div class="checkout-main">
						<section class="checkout-panel">
							<div class="checkout-section-heading">
								<span class="checkout-step">1</span>
								<div>
									<h2>Địa chỉ giao hàng</h2>
									<p>Chọn địa chỉ nhận sách của bạn.</p>
								</div>
							</div>

							<c:choose>
								<c:when test="${not empty addresses}">
									<div class="checkout-address-list">
										<c:forEach var="address" items="${addresses}">
											<label class="checkout-address-option"> <input
												type="radio" name="addressId" value="${address.addressId}"
												<c:if test="${address.addressId eq defaultAddressId or (empty defaultAddressId and address['default'])}">checked</c:if>
												required> <span class="checkout-radio-mark"></span>
												<span class="checkout-address-content"> <strong><c:out
															value="${address.recipientName}" /></strong> <c:if
														test="${not empty address.phone}">
														<span><c:out value="${address.phone}" /></span>
													</c:if> <span><c:out value="${address.addressLine}" /></span> <c:if
														test="${not empty address.ward}">
														<span><c:out value="${address.ward}" /></span>
													</c:if> <c:if test="${not empty address.district}">
														<span><c:out value="${address.district}" /></span>
													</c:if> <c:if test="${not empty address.city}">
														<span><c:out value="${address.city}" /></span>
													</c:if> <c:if test="${address['default']}">
														<span class="checkout-default-badge">Mặc định</span>
													</c:if>
											</span>
											</label>
										</c:forEach>
									</div>
								</c:when>

								<c:otherwise>
									<div class="checkout-inline-alert">Bạn chưa có địa chỉ
										giao hàng. Vui lòng thêm địa chỉ trước khi đặt hàng.</div>
									<a class="checkout-secondary-link"
										href="${pageContext.request.contextPath}/buyer/addresses">
										Quản lý địa chỉ </a>
								</c:otherwise>
							</c:choose>
						</section>

						<section class="checkout-panel">
							<div class="checkout-section-heading">
								<span class="checkout-step">2</span>
								<div>
									<h2>Phương thức thanh toán</h2>
									<p>Chọn cách thanh toán phù hợp với bạn.</p>
								</div>
							</div>

							<div class="checkout-payment-list">
								<label class="checkout-payment-option"> <input
									type="radio" name="paymentMethod" value="COD" checked required>
									<span class="checkout-radio-mark"></span> <span
									class="checkout-payment-icon">📦</span> <span
									class="checkout-payment-content"> <strong>Thanh
											toán khi nhận hàng (COD)</strong> <span>Bạn thanh toán khi đơn
											hàng được giao đến.</span>
								</span>
								</label> <label class="checkout-payment-option"> <input
									type="radio" name="paymentMethod" value="MOMO" required>
									<span class="checkout-radio-mark"></span> <span
									class="checkout-payment-icon">💗</span> <span
									class="checkout-payment-content"> <strong>Ví
											điện tử MoMo</strong> <span>Tiếp tục đến trang thanh toán MoMo
											để xác nhận giao dịch.</span>
								</span>
								</label>
							</div>
						</section>

						<section class="checkout-panel">
							<div class="checkout-section-heading">
								<span class="checkout-step">3</span>
								<div>
									<h2>Ghi chú đơn hàng</h2>
									<p>Thông tin bổ sung dành cho người bán hoặc đơn vị giao
										hàng.</p>
								</div>
							</div>

							<label class="form-label" for="buyerNote">Ghi chú</label>
							<textarea class="form-control checkout-note" id="buyerNote"
								name="buyerNote" rows="4" maxlength="1000"
								placeholder="Ví dụ: Gọi điện trước khi giao hàng"><c:out
									value="${buyerNote}" /></textarea>
						</section>
					</div>

					<aside class="checkout-summary">
						<h2>Đơn hàng của bạn</h2>

						<div class="checkout-summary-items">
							<c:forEach var="item" items="${items}">
								<div class="checkout-summary-item">
									<div class="checkout-item-info">
										<strong><c:out value="${item.bookTitle}" /></strong> <span>Số
											lượng: <c:out value="${item.quantity}" />
										</span>

										<c:if test="${not empty item.shopName}">
											<span>Shop: <c:out value="${item.shopName}" /></span>
										</c:if>
									</div>

									<div class="checkout-item-price">
										<fmt:formatNumber value="${item.unitPrice * item.quantity}"
											type="number" maxFractionDigits="0" />
										₫
									</div>
								</div>
							</c:forEach>
						</div>

						<div class="checkout-summary-row">
							<span>Tạm tính</span> <strong> <fmt:formatNumber
									value="${cartTotal}" type="number" maxFractionDigits="0" /> ₫
							</strong>
						</div>

						<div class="checkout-summary-row">
							<span>Phí vận chuyển</span> <strong>Chưa cấu hình</strong>
						</div>

						<div class="checkout-summary-total">
							<span>Tổng tiền tạm tính</span> <strong> <fmt:formatNumber
									value="${cartTotal}" type="number" maxFractionDigits="0" /> ₫
							</strong>
						</div>

						<p class="checkout-summary-note">Phí vận chuyển chưa được tính
							vào tổng tiền.</p>

						<button class="btn keeper-btn-primary checkout-submit"
							type="submit" <c:if test="${empty addresses}">disabled</c:if>>
							Xác nhận đặt hàng</button>

						<a class="checkout-continue-link"
							href="${pageContext.request.contextPath}/buyer/cart"> Quay
							lại giỏ hàng </a>
					</aside>
				</div>
			</form>
		</c:if>
	</main>
</body>
</html>

