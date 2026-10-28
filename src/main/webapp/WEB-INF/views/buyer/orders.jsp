<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>

<!DOCTYPE html>

<html lang="vi">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Đơn hàng của tôi | Keeper</title>
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/style.css">
<link rel="stylesheet"
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css">
</head>
<body class="orders-page">
	<header class="orders-navbar">
		<div class="orders-navbar-inner">
			<a class="keeper-brand" href="${pageContext.request.contextPath}/"
				aria-label="Keeper - Trang chủ"> <span class="keeper-brand-icon">K</span>
				<span class="keeper-brand-text">eeper</span>
			</a>
			<nav class="orders-nav-links">
				<a href="${pageContext.request.contextPath}/books">Mua sách</a> <a
					href="${pageContext.request.contextPath}/buyer/cart">Giỏ hàng</a>
			</nav>
		</div>
	</header>

	<main class="orders-container">
		<div class="orders-heading">
			<div>
				<p class="orders-eyebrow">TÀI KHOẢN CỦA BẠN</p>
				<h1>Đơn hàng của tôi</h1>
				<p>Theo dõi và quản lý các đơn hàng bạn đã đặt tại Keeper.</p>
			</div>
			<a class="btn keeper-btn-primary"
				href="${pageContext.request.contextPath}/books">Tiếp tục mua sắm</a>
		</div>

		<c:if test="${not empty successMessage}">
			<div class="alert alert-success" role="alert">
				<c:out value="${successMessage}" />
			</div>
		</c:if>

		<c:if test="${not empty errorMessage}">
			<div class="alert alert-danger" role="alert">
				<c:out value="${errorMessage}" />
			</div>
		</c:if>

		<section class="orders-panel">
			<div class="orders-panel-heading">
				<div>
					<h2>Lịch sử đặt hàng</h2>
					<p>Các đơn hàng được tạo từ tài khoản của bạn.</p>
				</div>
				<span class="orders-count"> <c:out
						value="${empty orders ? 0 : orders.size()}" /> đơn hàng
				</span>
			</div>

			<c:choose>
				<c:when test="${not empty orders}">
					<div class="orders-table-wrapper">
						<table class="orders-table">
							<thead>
								<tr>
									<th>Mã đơn hàng</th>
									<th>Ngày đặt</th>
									<th>Trạng thái</th>
									<th>Thanh toán</th>
									<th class="orders-amount-column">Tổng tiền</th>
									<th class="orders-action-column">Chi tiết</th>
								</tr>
							</thead>
							<tbody>
								<c:forEach var="order" items="${orders}">
									<tr>
										<td><strong class="orders-order-id"> #<c:out
													value="${order.orderId}" />
										</strong></td>
										<td class="orders-date"><c:choose>
												<c:when test="${not empty order.createdAt}">
													<fmt:formatDate value="${order.createdAt}"
														pattern="dd/MM/yyyy HH:mm" />
												</c:when>
												<c:otherwise>—</c:otherwise>
											</c:choose></td>
										<td><c:set var="statusCode" value="${order.status}" /> <c:choose>
												<c:when
													test="${statusCode eq 'PENDING' or statusCode eq 'PENDING_SELLER' or statusCode eq 'PENDING_PAYMENT'}">
													<span class="orders-status status-pending">Chờ xác
														nhận</span>
												</c:when>
												<c:when test="${statusCode eq 'CONFIRMED'}">
													<span class="orders-status status-confirmed">Đã xác
														nhận</span>
												</c:when>
												<c:when
													test="${statusCode eq 'PROCESSING' or statusCode eq 'PREPARING'}">
													<span class="orders-status status-processing">Đang
														xử lý</span>
												</c:when>
												<c:when
													test="${statusCode eq 'SHIPPED' or statusCode eq 'READY_TO_SHIP' or statusCode eq 'SHIPPING'}">
													<span class="orders-status status-shipped">Đang giao
														hàng</span>
												</c:when>
												<c:when
													test="${statusCode eq 'DELIVERED' or statusCode eq 'COMPLETED'}">
													<span class="orders-status status-delivered">Đã giao
														hàng</span>
												</c:when>
												<c:when
													test="${statusCode eq 'CANCELLED' or statusCode eq 'CANCELED'}">
													<span class="orders-status status-cancelled">Đã hủy</span>
												</c:when>
												<c:when test="${statusCode eq 'RETURNED'}">
													<span class="orders-status status-cancelled">Đã trả
														hàng</span>
												</c:when>
												<c:otherwise>
													<span class="orders-status status-default"><c:out
															value="${statusCode}" /></span>
												</c:otherwise>
											</c:choose></td>
										<td><c:choose>
												<c:when test="${order.paymentStatus eq 'PAID'}">
													<span class="orders-payment payment-paid">Đã thanh
														toán</span>
												</c:when>
												<c:when test="${order.paymentStatus eq 'FAILED'}">
													<span class="orders-payment payment-failed">Thanh
														toán thất bại</span>
												</c:when>
												<c:when test="${order.paymentStatus eq 'REFUNDED'}">
													<span class="orders-payment payment-refunded">Đã
														hoàn tiền</span>
												</c:when>
												<c:when test="${order.paymentStatus eq 'PENDING'}">
													<span class="orders-payment payment-pending">Chờ
														thanh toán</span>
												</c:when>
												<c:otherwise>
													<span class="orders-payment payment-pending">Chưa
														cập nhật</span>
												</c:otherwise>
											</c:choose></td>
										<td class="orders-amount-column"><strong
											class="orders-total"> <fmt:formatNumber
													value="${order.totalAmount}" type="number"
													maxFractionDigits="0" /> ₫
										</strong></td>
										<td class="orders-action-column"><a
											class="orders-detail-link"
											href="${pageContext.request.contextPath}/buyer/orders/${order.orderId}">
												Xem chi tiết </a></td>
									</tr>
								</c:forEach>
							</tbody>
						</table>
					</div>

					<div class="orders-mobile-list">
						<c:forEach var="order" items="${orders}">
							<article class="orders-mobile-card">
								<div class="orders-mobile-card-top">
									<strong class="orders-order-id">Đơn #<c:out
											value="${order.orderId}" /></strong> <strong class="orders-total">
										<fmt:formatNumber value="${order.totalAmount}" type="number"
											maxFractionDigits="0" /> ₫
									</strong>
								</div>
								<div class="orders-mobile-meta">
									<span>Ngày đặt</span> <strong> <c:choose>
											<c:when test="${not empty order.createdAt}">
												<fmt:formatDate value="${order.createdAt}"
													pattern="dd/MM/yyyy HH:mm" />
											</c:when>
											<c:otherwise>—</c:otherwise>
										</c:choose>
									</strong>
								</div>
								<div class="orders-mobile-meta">
									<span>Trạng thái</span> <strong><c:out
											value="${order.status}" /></strong>
								</div>
								<a class="orders-detail-link orders-mobile-detail"
									href="${pageContext.request.contextPath}/buyer/orders/${order.orderId}">
									Xem chi tiết đơn hàng </a>
							</article>
						</c:forEach>
					</div>
				</c:when>
				<c:otherwise>
					<div class="orders-empty">
						<div class="orders-empty-icon">📚</div>
						<h3>Bạn chưa có đơn hàng nào</h3>
						<p>Khi đặt mua sách thành công, đơn hàng của bạn sẽ xuất hiện
							tại đây.</p>
						<a class="btn keeper-btn-primary"
							href="${pageContext.request.contextPath}/books">Khám phá sách</a>
					</div>
				</c:otherwise>
			</c:choose>
		</section>

	</main>
</body>
</html>