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

<style>
.seller-page {
	min-height: 100vh;
	background: #f5f7fb;
	color: #172b4d;
	font-family: Arial, sans-serif;
}

.seller-page .seller-header {
	display: flex;
	align-items: center;
	justify-content: space-between;
	gap: 24px;
	flex-wrap: wrap;
	padding: 20px max(5%, calc(( 100% - 1200px)/2));
	background: #ffffff;
	border-bottom: 1px solid #e8edf5;
	box-shadow: 0 2px 10px rgba(20, 40, 80, 0.04);
}

.seller-page .seller-subtitle {
	margin: 8px 0 0;
	color: #7b879b;
	font-size: 13px;
}

.seller-page .seller-nav {
	display: flex;
	align-items: center;
	gap: 8px;
	flex-wrap: wrap;
}

.seller-page .seller-nav-link {
	display: inline-flex;
	align-items: center;
	padding: 10px 15px;
	border-radius: 9px;
	color: #52627a;
	font-size: 14px;
	font-weight: 600;
	text-decoration: none;
	transition: background 0.2s, color 0.2s;
}

.seller-page .seller-nav-link:hover {
	background: #edf4ff;
	color: #245db5;
	text-decoration: none;
}

.seller-page .seller-nav-link.active {
	background: #e8f1ff;
	color: #245db5;
}

.seller-page .seller-main {
	width: min(1200px, 90%);
	margin: 0 auto;
	padding: 34px 0 56px;
}

.seller-page .seller-page-heading {
	display: flex;
	justify-content: space-between;
	align-items: flex-start;
	gap: 20px;
	margin-bottom: 26px;
}

.seller-page .seller-eyebrow {
	margin: 0 0 10px;
	color: #3975ce;
	font-size: 11px;
	font-weight: 800;
	letter-spacing: 1.8px;
}

.seller-page .seller-page-heading h1 {
	margin: 0;
	color: #172b4d;
	font-size: clamp(26px, 3vw, 34px);
	line-height: 1.25;
	letter-spacing: -0.7px;
}

.seller-page .seller-muted {
	margin: 10px 0 0;
	color: #78869c;
	font-size: 14px;
	line-height: 1.7;
}

.seller-page .seller-panel {
	margin-top: 22px;
	padding: 25px;
	background: #ffffff;
	border: 1px solid #e8edf5;
	border-radius: 16px;
	box-shadow: 0 5px 20px rgba(23, 43, 77, 0.035);
}

.seller-page .seller-panel-heading {
	display: flex;
	align-items: center;
	justify-content: space-between;
	gap: 16px;
	flex-wrap: wrap;
	margin-bottom: 22px;
}

.seller-page .seller-panel-heading h2 {
	margin: 0;
	color: #172b4d;
	font-size: 19px;
	font-weight: 750;
}

.seller-page .seller-panel-heading .seller-muted {
	margin-top: 6px;
	font-size: 13px;
}

.seller-page .seller-filter-form {
	display: grid;
	grid-template-columns: minmax(180px, 1fr) minmax(220px, 1.2fr) auto;
	align-items: end;
	gap: 18px;
}

.seller-page .seller-field {
	display: flex;
	flex-direction: column;
	gap: 9px;
	min-width: 0;
}

.seller-page .seller-field label {
	color: #3b4b63;
	font-size: 13px;
	font-weight: 700;
}

.seller-page .seller-field select {
	width: 100%;
	min-height: 44px;
	padding: 0 13px;
	border: 1px solid #dce3ee;
	border-radius: 9px;
	outline: none;
	background: #ffffff;
	color: #263852;
	font: inherit;
	font-size: 14px;
	transition: border-color 0.2s, box-shadow 0.2s;
}

.seller-page .seller-field select:focus {
	border-color: #4d8de8;
	box-shadow: 0 0 0 3px rgba(77, 141, 232, 0.12);
}

.seller-page .seller-filter-actions {
	display: flex;
	align-items: center;
	gap: 9px;
}

.seller-page .seller-button {
	display: inline-flex;
	justify-content: center;
	align-items: center;
	gap: 6px;
	min-height: 42px;
	padding: 10px 17px;
	border: 1px solid transparent;
	border-radius: 9px;
	font-family: inherit;
	font-size: 13px;
	font-weight: 700;
	line-height: 1.2;
	white-space: nowrap;
	text-decoration: none;
	cursor: pointer;
	transition: background 0.2s, border-color 0.2s, color 0.2s, transform
		0.2s;
}

.seller-page .seller-button:hover {
	transform: translateY(-1px);
	text-decoration: none;
}

.seller-page .seller-button-primary {
	background: #2867c7;
	color: #ffffff;
}

.seller-page .seller-button-primary:hover {
	background: #1f55a8;
	color: #ffffff;
}

.seller-page .seller-button-outline {
	border-color: #d9e2ef;
	background: #ffffff;
	color: #52627a;
}

.seller-page .seller-button-outline:hover {
	border-color: #9dbbe8;
	background: #f4f8ff;
	color: #245db5;
}

.seller-page .seller-button-small {
	min-height: 34px;
	padding: 7px 11px;
	font-size: 12px;
}

.seller-page .seller-count {
	display: inline-flex;
	align-items: center;
	padding: 7px 12px;
	border-radius: 20px;
	background: #edf4ff;
	color: #2867c7;
	font-size: 12px;
	font-weight: 700;
}

.seller-page .seller-table-wrap {
	width: 100%;
	overflow-x: auto;
	border: 1px solid #edf0f6;
	border-radius: 12px;
}

.seller-page .seller-table {
	width: 100%;
	min-width: 850px;
	border-collapse: separate;
	border-spacing: 0;
	text-align: left;
}

.seller-page .seller-table thead th {
	padding: 15px 16px;
	background: #f7f9fc;
	border-bottom: 1px solid #e8edf5;
	color: #65748b;
	font-size: 11px;
	font-weight: 800;
	letter-spacing: 0.5px;
	text-transform: uppercase;
	white-space: nowrap;
}

.seller-page .seller-table tbody td {
	padding: 17px 16px;
	border-bottom: 1px solid #edf0f6;
	color: #3b4b63;
	font-size: 13px;
	vertical-align: middle;
}

.seller-page .seller-table tbody tr:last-child td {
	border-bottom: none;
}

.seller-page .seller-table tbody tr {
	transition: background 0.15s;
}

.seller-page .seller-table tbody tr:hover {
	background: #fafcff;
}

.seller-page .seller-table td strong {
	color: #243650;
	font-size: 13px;
	font-weight: 750;
}

.seller-page .seller-table-subtitle {
	display: block;
	margin-top: 5px;
	color: #8793a6;
	font-size: 12px;
	line-height: 1.5;
}

.seller-page .seller-text-right {
	text-align: right;
	white-space: nowrap;
}

.seller-page .seller-empty {
	padding: 45px 20px;
	border: 1px dashed #dce4ef;
	border-radius: 12px;
	background: #fbfcfe;
	text-align: center;
}

.seller-page .seller-empty h3 {
	margin: 0;
	color: #2c3e57;
	font-size: 17px;
}

.seller-page .seller-empty p {
	margin: 10px 0 0;
	color: #8490a3;
	font-size: 13px;
	line-height: 1.6;
}

.seller-page .seller-alert {
	margin: 18px 0;
	padding: 14px 17px;
	border: 1px solid transparent;
	border-radius: 10px;
	font-size: 14px;
	line-height: 1.6;
}

.seller-page .seller-alert-success {
	border-color: #bce8d0;
	background: #effbf4;
	color: #207448;
}

.seller-page .seller-alert-error {
	border-color: #f3c7c7;
	background: #fff3f3;
	color: #b42332;
}

@media ( max-width : 900px) {
	.seller-page .seller-header {
		padding: 18px 5%;
	}
	.seller-page .seller-main {
		width: 92%;
		padding-top: 26px;
	}
	.seller-page .seller-filter-form {
		grid-template-columns: 1fr 1fr;
	}
	.seller-page .seller-filter-actions {
		grid-column: 1/-1;
	}
}

@media ( max-width : 600px) {
	.seller-page .seller-header {
		align-items: flex-start;
		flex-direction: column;
		gap: 17px;
	}
	.seller-page .seller-nav {
		width: 100%;
		gap: 5px;
	}
	.seller-page .seller-nav-link {
		padding: 9px 10px;
		font-size: 12px;
	}
	.seller-page .seller-main {
		width: 92%;
		padding: 24px 0 38px;
	}
	.seller-page .seller-page-heading h1 {
		font-size: 27px;
	}
	.seller-page .seller-panel {
		padding: 17px 14px;
		border-radius: 13px;
	}
	.seller-page .seller-filter-form {
		grid-template-columns: 1fr;
		gap: 15px;
	}
	.seller-page .seller-filter-actions {
		grid-column: auto;
		width: 100%;
	}
	.seller-page .seller-filter-actions .seller-button {
		flex: 1;
	}
	.seller-page .seller-panel-heading h2 {
		font-size: 17px;
	}
	.seller-page .seller-empty {
		padding: 34px 14px;
	}
}
</style>
</head>
<body>
	<div class="seller-page">
		<header class="seller-header">
			<div class="seller-header-brand">
				<a class="keeper-brand" href="${pageContext.request.contextPath}/">
					<span class="keeper-brand-icon">K</span> <span
					class="keeper-brand-text">Keeper</span>
				</a> <span class="seller-subtitle">Khu vực người bán</span>
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
							<option value="ALL"
								<c:if test="${selectedStatus eq 'ALL'}">selected</c:if>>Tất
								cả trạng thái</option>
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
					<span class="seller-count"> <c:out
							value="${empty orders ? 0 : orders.size()}" /> đơn hàng
					</span>
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
											<td class="seller-text-right"><strong> <fmt:formatNumber
														value="${order.totalAmount}" pattern="#,##0" /> ₫
											</strong></td>
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
