<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Đặt hàng thành công | Keeper</title>
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/style.css">
<link rel="stylesheet"
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css">
</head>
<body class="checkout-success-page">
	<main class="checkout-success-card">
		<a class="keeper-brand" href="${pageContext.request.contextPath}/"
			aria-label="Keeper - Trang chủ"> <span class="keeper-brand-icon">K</span>
			<span class="keeper-brand-text">eeper</span>
		</a>

		<div class="checkout-success-icon">
			<span>✓</span>
		</div>

		<h1>Đặt hàng thành công!</h1>
		<p class="checkout-success-description">Cảm ơn bạn đã mua sắm tại
			Keeper. Đơn hàng của bạn đã được ghi nhận.</p>

		<c:if test="${not empty orderIds}">
			<section class="checkout-success-orders">
				<h2>Mã đơn hàng</h2>
				<c:forEach var="orderId" items="${orderIds}">
					<div class="checkout-success-order-id">
						<span>Đơn hàng</span> <strong><c:out value="${orderId}" /></strong>
					</div>
				</c:forEach>
			</section>
		</c:if>

		<div class="checkout-success-notice">
			<strong>Bước tiếp theo</strong>
			<p>Bạn có thể xem trạng thái đơn hàng trong mục Đơn hàng của tôi.
				Nếu chọn thanh toán khi nhận hàng, vui lòng chuẩn bị tiền mặt khi
				nhận sách.</p>
		</div>

		<div class="checkout-success-actions">
			<a class="btn keeper-btn-primary"
				href="${pageContext.request.contextPath}/buyer/orders"> Xem đơn
				hàng của tôi </a> <a class="btn keeper-btn-outline"
				href="${pageContext.request.contextPath}/books"> Tiếp tục mua
				sắm </a>
		</div>

		<a class="checkout-success-home"
			href="${pageContext.request.contextPath}/"> Quay về trang chủ </a>
	</main>
</body>
</html>