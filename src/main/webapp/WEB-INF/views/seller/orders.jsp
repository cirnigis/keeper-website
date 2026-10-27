
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<!DOCTYPE html>
<html lang="vi">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Quản lý đơn hàng | Keeper</title>
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
	<div class="seller-page">
		<header class="seller-header">
			<div>
				<a class="seller-brand" href="${pageContext.request.contextPath}/">Keeper</a>
				<p class="seller-subtitle">Khu vực người bán</p>
			</div>
			<nav class="seller-nav">
				<a class="seller-nav-link"
					href="${pageContext.request.contextPath}/seller/shops">Shop của
					tôi</a> <a class="seller-nav-link"
					href="${pageContext.request.contextPath}/seller/listings">Sản
					phẩm</a> <a class="seller-nav-link active"
					href="${pageContext.request.contextPath}/seller/orders">Đơn
					hàng</a>
			</nav>
		</header>

		<main class="seller-main">
			<div class="seller-page-heading">
				<div>
					<p class="seller-eyebrow">SELLER CENTER</p>
					<h1>Quản lý đơn hàng</h1>
					<p class="seller-muted">Theo dõi đơn hàng và chuẩn bị sách để
						giao cho khách.</p>
				</div>
			</div>

			<c:if test="${not empty successMessage}">
				<div class="seller-alert seller-alert-success">
					<c:out value="${successMessage}" />
				</div>
			</c:if>
			<c:if test="${not empty errorMessage}">
				<div class="seller-alert seller-alert-error">
					<c:out value="${errorMessage}" />
				</div>
			</c:if>

			<section class="seller-panel">
				<div class="seller-panel-heading">
					<div>
						<h2>Lọc đơn hàng</h2>
						<p class="seller-muted">Chọn shop và trạng thái muốn xem.</p>
					</div>
				</div>

				<form method="get"
					action="${pageContext.request.contextPath}/seller/orders"
					class="seller-filter-form">
					<div class="seller-field">
						<label for="shopId">Shop</label> <select id="shopId" name="shopId"
							required>
							<option value="">-- Chọn shop --</option>
							<c:forEach var="shop" items="${shops}">
								<option value="${shop.shopId}"
									<c:if test="${selectedShopId eq shop.shopId}">selected</c:if>>
									<c:out value="${shop.shopName}" />
								</option>
							</c:forEach>
						</select>
					</div>

					<div class="seller-field">
						<label for="status">Trạng thái</label> <select id="status"
							name="status">
							<option value="ALL">Tất cả trạng thái</option>
							<option value="PENDING_PAYMENT"
								<c:if test="${selectedStatus eq 'PENDING_PAYMENT'}">selected</c:if>>Chờ
								thanh toán</option>
							<option value="PENDING_SELLER"
								<c:if test="${selectedStatus eq 'PENDING_SELLER'}">selected</c:if>>Chờ
								người bán xác nhận</option>
							<option value="CONFIRMED"
								<c:if test="${selectedStatus eq 'CONFIRMED'}">selected</c:if>>Đã
								xác nhận</option>
							<option value="PREPARING"
								<c:if test="${selectedStatus eq 'PREPARING'}">selected</c:if>>Đang
								chuẩn bị</option>
							<option value="READY_TO_SHIP"
								<c:if test="${selectedStatus eq 'READY_TO_SHIP'}">selected</c:if>>Sẵn
								sàng giao</option>
							<option value="SHIPPING"
								<c:if test="${selectedStatus eq 'SHIPPING'}">selected</c:if>>Đang
								giao</option>
							<option value="DELIVERED"
								<c:if test="${selectedStatus eq 'DELIVERED'}">selected</c:if>>Đã
								giao</option>
							<option value="COMPLETED"
								<c:if test="${selectedStatus eq 'COMPLETED'}">selected</c:if>>Hoàn
								tất</option>
							<option value="CANCEL_REQUESTED"
								<c:if test="${selectedStatus eq 'CANCEL_REQUESTED'}">selected</c:if>>Yêu
								cầu hủy</option>
							<option value="CANCELED"
								<c:if test="${selectedStatus eq 'CANCELED'}">selected</c:if>>Đã
								hủy</option>
							<option value="RETURN_REQUESTED"
								<c:if test="${selectedStatus eq 'RETURN_REQUESTED'}">selected</c:if>>Yêu
								cầu trả hàng</option>
							<option value="RETURNING"
								<c:if test="${selectedStatus eq 'RETURNING'}">selected</c:if>>Đang
								trả hàng</option>
							<option value="RETURNED"
								<c:if test="${selectedStatus eq 'RETURNED'}">selected</c:if>>Đã
								trả hàng</option>
						</select>
					</div>

					<div class="seller-filter-actions">
						<button type="submit" class="seller-button seller-button-primary">Lọc</button>
						<a class="seller-button seller-button-outline"
							href="${pageContext.request.contextPath}/seller/orders">Đặt
							lại</a>
					</div>
				</form>
			</section>

			<section class="seller-panel">
				<div class="seller-panel-heading">
					<h2>Danh sách đơn hàng</h2>
					<span class="seller-count"><c:out
							value="${empty orders ? 0 : orders.size()}" /> đơn hàng</span>
				</div>

				<c:choose>
					<c:when test="${empty selectedShopId}">
						<div class="seller-empty">
							<h3>Chọn shop để xem đơn hàng</h3>
							<p>Chọn một shop đã được duyệt và đang hoạt động.</p>
						</div>
					</c:when>
					<c:when test="${empty orders}">
						<div class="seller-empty">
							<h3>Chưa có đơn hàng phù hợp</h3>
							<p>Thử chọn trạng thái khác hoặc quay lại sau.</p>
						</div>
					</c:when>
					<c:otherwise>
						<div class="seller-table-wrap">
							<table class="seller-table">
								<thead>
									<tr>
										<th>Mã đơn</th>
										<th>Người nhận</th>
										<th>Ngày đặt</th>
										<th>Thanh toán</th>
										<th>Trạng thái</th>
										<th class="seller-text-right">Tổng tiền</th>
										<th></th>
									</tr>
								</thead>
								<tbody>
									<c:forEach var="order" items="${orders}">
										<tr>
											<td><strong><c:out value="${order.orderCode}" /></strong>
												<span class="seller-table-subtitle">#<c:out
														value="${order.orderId}" /></span></td>
											<td><strong><c:out
														value="${order.recipientName}" /></strong> <span
												class="seller-table-subtitle"><c:out
														value="${order.recipientPhone}" /></span></td>
											<td><fmt:formatDate value="${order.createdAt}"
													pattern="dd/MM/yyyy HH:mm" /></td>
											<td><c:out value="${order.paymentMethod}" /></td>
											<td><c:out value="${order.orderStatus}" /></td>
											<td class="seller-text-right"><strong><fmt:formatNumber
														value="${order.totalAmount}" pattern="#,##0" /> ₫</strong></td>
											<td><a
												class="seller-button seller-button-small seller-button-outline"
												href="${pageContext.request.contextPath}/seller/orders/detail?shopId=${order.shopId}&orderId=${order.orderId}">
													Chi tiết </a></td>
										</tr>
									</c:forEach>
								</tbody>
							</table>
						</div>
					</c:otherwise>
				</c:choose>
			</section>
		</main>
	</div>
</body>
</html>
