<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<!DOCTYPE html>
<html lang="vi">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Thanh toán MoMo | Keeper</title>
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/style.css">
<link rel="stylesheet"
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css">
</head>
<body class="payment-page">
	<header class="checkout-navbar">
		<div class="checkout-navbar-inner">
			<a class="keeper-brand" href="${pageContext.request.contextPath}/"
				aria-label="Keeper - Trang chủ"> <span class="keeper-brand-icon">K</span>
				<span class="keeper-brand-text">eeper</span>
			</a> <a class="checkout-back-link"
				href="${pageContext.request.contextPath}/buyer/orders"> Đơn hàng
				của tôi </a>
		</div>
	</header>

	<main class="payment-container">
		<section class="payment-card">
			<div class="payment-brand">
				<div class="payment-momo-logo">M</div>
				<div>
					<h1>Thanh toán MoMo</h1>
					<p>Keeper · Thanh toán mô phỏng</p>
				</div>
			</div>

			<div class="payment-status-badge">
				<span class="payment-status-dot"></span> Đang chờ thanh toán
			</div>

			<p class="payment-description">Kiểm tra thông tin đơn hàng trước
				khi mô phỏng kết quả thanh toán. Đây là môi trường thử nghiệm, không
				phát sinh giao dịch MoMo thật.</p>

			<div class="payment-amount-section">
				<span class="payment-label">Tổng thanh toán</span>
				<div class="payment-amount">
					<fmt:formatNumber value="${totalAmount}" type="number"
						maxFractionDigits="0" />
					₫
				</div>
				<span class="payment-currency">VND</span>
			</div>

			<div class="payment-order-details">
				<div class="payment-detail-row">
					<span>Phương thức</span> <strong>Ví điện tử MoMo</strong>
				</div>

				<div class="payment-detail-row">
					<span>Mã đơn hàng</span> <strong> <c:choose>
							<c:when test="${not empty orderIds}">
								<c:forEach var="orderId" items="${orderIds}" varStatus="status">
									<c:if test="${!status.first}">, </c:if>
                                    #<c:out value="${orderId}" />
								</c:forEach>
							</c:when>
							<c:otherwise>Đang xử lý</c:otherwise>
						</c:choose>
					</strong>
				</div>

				<div class="payment-detail-row">
					<span>Trạng thái</span> <strong class="payment-pending-text">Chờ
						thanh toán</strong>
				</div>
			</div>

			<div class="payment-mock-notice">
				<span class="payment-notice-icon">ⓘ</span>
				<p>Chọn một kết quả bên dưới để kiểm tra luồng xử lý thanh toán.
					Nút thành công sẽ đánh dấu thanh toán đã hoàn tất nếu backend xử lý
					đúng.</p>
			</div>

			<form method="post"
				action="${pageContext.request.contextPath}/buyer/cart/payment/mock-result"
				class="payment-actions">

				<input type="hidden" name="${_csrf.parameterName}"
					value="${_csrf.token}">

				<c:forEach var="orderId" items="${orderIds}">
					<input type="hidden" name="orderIds" value="${orderId}">
				</c:forEach>

				<button type="submit" name="result" value="SUCCESS"
					class="payment-button-success">
					<span>✓</span> Mô phỏng thanh toán thành công
				</button>

				<button type="submit" name="result" value="FAILED"
					class="payment-button-failure">
					<span>×</span> Mô phỏng thanh toán thất bại
				</button>
			</form>

			<a class="payment-back-link"
				href="${pageContext.request.contextPath}/buyer/cart"> Quay lại
				giỏ hàng </a>

			<div class="payment-security-note">
				<span>🔒</span> <span>Đây chỉ là trang mô phỏng, chưa kết nối
					cổng thanh toán MoMo thật.</span>
			</div>
		</section>

		<p class="payment-footer">© Keeper · Nền tảng mua bán sách đã qua
			sử dụng</p>
	</main>
</body>
</html>