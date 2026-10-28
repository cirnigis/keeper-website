
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<!DOCTYPE html>
<html lang="vi">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Seller Dashboard | Keeper</title>
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/style.css">

<style>
.seller-page {
	margin: 0;
	min-height: 100vh;
	background: #f4f6fb;
	color: #172554;
	font-family: Arial, Helvetica, sans-serif;
}

.seller-page *, .seller-page *::before, .seller-page *::after {
	box-sizing: border-box;
}

.seller-page .seller-header {
	width: 100%;
	margin: 0;
	padding: 0;
	background: #fff;
	border-bottom: 1px solid #e2e8f0;
}

.seller-page .seller-header-inner {
	width: min(1400px, calc(100% - 48px));
	min-height: 76px;
	margin: 0 auto;
	display: flex;
	align-items: center;
	gap: 24px;
}

.seller-page .seller-header .keeper-brand {
	display: inline-flex;
	align-items: center;
	gap: 5px;
	flex: 0 0 auto;
	margin: 0;
	padding: 0;
	font-family: "Trebuchet MS", Arial, sans-serif;
	font-size: 34px;
	font-weight: 700;
	line-height: 1;
	letter-spacing: -1.2px;
	text-decoration: none;
	white-space: nowrap;
}

.seller-page .seller-header .keeper-brand:hover, .seller-page .seller-header .keeper-brand:focus
	{
	text-decoration: none;
}

.seller-page .seller-header .keeper-brand-icon {
	display: inline-flex;
	align-items: center;
	justify-content: center;
	flex: 0 0 34px;
	width: 34px;
	height: 34px;
	border-radius: 9px;
	background: var(--keeper-blue);
	color: #fff;
	font-size: 24px;
	font-weight: 700;
	line-height: 1;
	letter-spacing: -1px;
}

.seller-page .seller-header .keeper-brand-text {
	color: var(--keeper-navy);
	font-family: "Trebuchet MS", Arial, sans-serif;
	font-size: 34px;
	font-weight: 700;
	line-height: 1;
	letter-spacing: -1.2px;
}

.seller-page .seller-subtitle {
	flex: 0 0 auto;
	margin: 0;
	padding: 4px 0 4px 24px;
	border-left: 1px solid #e2e8f0;
	color: #64748b;
	font-size: 15px;
	line-height: 1.5;
	white-space: nowrap;
}

.seller-page .seller-nav {
	display: flex;
	flex: 1 1 auto;
	align-items: center;
	justify-content: flex-end;
	gap: 28px;
	min-width: 0;
	margin: 0 0 0 auto;
	padding: 0;
}

.seller-page .seller-nav-link {
	display: inline-flex;
	flex: 0 0 auto;
	align-items: center;
	margin: 0;
	padding: 10px 0;
	border: 0;
	background: transparent;
	color: #14213d;
	font-size: 15px;
	font-weight: 600;
	line-height: 1.4;
	text-decoration: none;
	white-space: nowrap;
}

.seller-page .seller-nav-link:hover, .seller-page .seller-nav-link:focus,
	.seller-page .seller-nav-link[aria-current="page"] {
	color: #1463f3;
	text-decoration: none;
}

.seller-page .seller-main {
	width: min(1400px, calc(100% - 48px));
	margin: 0 auto;
	padding: 30px 0 48px;
}

.seller-page .seller-page-heading {
	margin: 0 0 24px;
}

.seller-page .seller-eyebrow {
	margin: 0 0 12px;
	color: #64748b;
	font-size: 12px;
	font-weight: 700;
	letter-spacing: 1.2px;
}

.seller-page .seller-page-heading h1 {
	margin: 0 0 12px;
	color: #172554;
	font-size: clamp(26px, 3vw, 34px);
	font-weight: 750;
	line-height: 1.3;
}

.seller-page .seller-muted {
	color: #64748b;
}

.seller-page .seller-page-heading .seller-muted {
	margin: 0;
	font-size: 15px;
	line-height: 1.7;
}

.seller-page .seller-dashboard-grid {
	display: grid;
	grid-template-columns: repeat(4, minmax(0, 1fr));
	gap: 18px;
	margin-bottom: 24px;
}

.seller-page .seller-dashboard-card {
	min-width: 0;
	padding: 24px;
	border: 1px solid #e2e8f0;
	border-radius: 17px;
	background: #fff;
	box-shadow: 0 2px 8px rgba(15, 23, 42, 0.025);
}

.seller-page .seller-dashboard-card-label {
	margin: 0 0 18px;
	color: #64748b;
	font-size: 14px;
	line-height: 1.5;
}

.seller-page .seller-dashboard-card-value {
	margin: 0 0 12px;
	color: #172554;
	font-size: 30px;
	font-weight: 750;
	line-height: 1.2;
}

.seller-page .seller-dashboard-card-note {
	margin: 0;
	color: #64748b;
	font-size: 13px;
	line-height: 1.6;
}

.seller-page .seller-dashboard-shortcuts {
	display: grid;
	grid-template-columns: repeat(3, minmax(0, 1fr));
	gap: 16px;
	margin-bottom: 24px;
}

.seller-page .seller-dashboard-shortcut {
	display: block;
	min-width: 0;
	padding: 22px;
	border: 1px solid #e2e8f0;
	border-radius: 15px;
	background: #fff;
	color: #172554;
	text-decoration: none;
	transition: border-color .2s, transform .2s, box-shadow .2s;
}

.seller-page .seller-dashboard-shortcut:hover {
	border-color: #93b4f8;
	box-shadow: 0 5px 16px rgba(15, 23, 42, .05);
	transform: translateY(-2px);
	text-decoration: none;
}

.seller-page .seller-dashboard-shortcut strong {
	display: block;
	margin-bottom: 10px;
	color: #172554;
	font-size: 17px;
	line-height: 1.4;
}

.seller-page .seller-dashboard-shortcut span {
	display: block;
	color: #64748b;
	font-size: 13px;
	line-height: 1.7;
}

.seller-page .seller-dashboard-sections {
	display: grid;
	grid-template-columns: minmax(0, 1.5fr) minmax(280px, 1fr);
	gap: 22px;
	align-items: start;
	margin-bottom: 22px;
}

.seller-page .seller-panel {
	min-width: 0;
	padding: 26px;
	border: 1px solid #e2e8f0;
	border-radius: 18px;
	background: #fff;
	box-shadow: 0 2px 8px rgba(15, 23, 42, 0.025);
}

.seller-page .seller-dashboard-panel-heading {
	display: flex;
	align-items: center;
	justify-content: space-between;
	gap: 16px;
	margin-bottom: 22px;
}

.seller-page .seller-dashboard-section-title {
	margin: 0;
	color: #172554;
	font-size: 20px;
	font-weight: 700;
	line-height: 1.4;
}

.seller-page .seller-dashboard-panel-heading a {
	flex: 0 0 auto;
	color: #1463f3;
	font-size: 14px;
	text-decoration: none;
}

.seller-page .seller-dashboard-panel-heading a:hover {
	text-decoration: underline;
}

.seller-page .seller-dashboard-table-wrap {
	width: 100%;
	overflow-x: auto;
}

.seller-page .seller-table {
	width: 100%;
	border-collapse: collapse;
	border-spacing: 0;
	font-size: 14px;
}

.seller-page .seller-table th {
	padding: 13px 14px;
	border-bottom: 1px solid #e2e8f0;
	background: #f8fafc;
	color: #475569;
	font-size: 12px;
	font-weight: 700;
	text-align: left;
	white-space: nowrap;
}

.seller-page .seller-table td {
	padding: 15px 14px;
	border-bottom: 1px solid #edf0f5;
	color: #334155;
	vertical-align: middle;
}

.seller-page .seller-table tbody tr:last-child td {
	border-bottom: 0;
}

.seller-page .seller-table tbody tr:hover {
	background: #fafcff;
}

.seller-page .seller-table a {
	color: #1463f3;
	text-decoration: none;
}

.seller-page .seller-table a:hover {
	text-decoration: underline;
}

.seller-page .seller-text-right {
	text-align: right !important;
	white-space: nowrap;
}

.seller-page .seller-dashboard-empty {
	padding: 34px 12px;
	color: #64748b;
	font-size: 15px;
	line-height: 1.7;
	text-align: center;
}

.seller-page .seller-dashboard-shop-name {
	color: #172554;
	font-weight: 650;
}

.seller-page .seller-dashboard-status {
	display: inline-block;
	padding: 5px 9px;
	border-radius: 999px;
	background: #f1f5f9;
	color: #475569;
	font-size: 12px;
	font-weight: 650;
	line-height: 1.4;
	white-space: nowrap;
}

.seller-page .seller-dashboard-status-approved, .seller-page .seller-dashboard-status-active,
	.seller-page .seller-dashboard-status-completed {
	background: #dcfce7;
	color: #166534;
}

.seller-page .seller-dashboard-status-pending, .seller-page .seller-dashboard-status-pending_seller,
	.seller-page .seller-dashboard-status-confirmed, .seller-page .seller-dashboard-status-preparing
	{
	background: #fef3c7;
	color: #92400e;
}

.seller-page .seller-dashboard-status-rejected, .seller-page .seller-dashboard-status-canceled,
	.seller-page .seller-dashboard-status-removed {
	background: #fee2e2;
	color: #991b1b;
}

.seller-page .seller-dashboard-breakdown {
	display: grid;
	gap: 0;
}

.seller-page .seller-dashboard-breakdown-row {
	display: flex;
	justify-content: space-between;
	align-items: center;
	gap: 16px;
	padding: 15px 0;
	border-bottom: 1px solid #e8edf3;
}

.seller-page .seller-dashboard-breakdown-row:first-child {
	padding-top: 4px;
}

.seller-page .seller-dashboard-breakdown-row:last-child {
	padding-bottom: 0;
	border-bottom: 0;
}

.seller-page .seller-dashboard-breakdown-label {
	color: #475569;
	font-size: 14px;
	line-height: 1.6;
}

.seller-page .seller-dashboard-breakdown-value {
	flex: 0 0 auto;
	color: #172554;
	font-weight: 700;
}

.seller-page .seller-button {
	display: inline-flex;
	align-items: center;
	justify-content: center;
	min-height: 40px;
	padding: 10px 16px;
	border-radius: 8px;
	font-size: 14px;
	font-weight: 600;
	text-decoration: none;
}

.seller-page .seller-button-primary {
	border: 1px solid #1463f3;
	background: #1463f3;
	color: #fff;
}

.seller-page .seller-button-primary:hover {
	background: #0f50c4;
	text-decoration: none;
}

@media ( max-width : 1100px) {
	.seller-page .seller-dashboard-grid {
		grid-template-columns: repeat(2, minmax(0, 1fr));
	}
	.seller-page .seller-dashboard-sections {
		grid-template-columns: 1fr;
	}
	.seller-page .seller-header-inner {
		gap: 18px;
	}
	.seller-page .seller-nav {
		gap: 18px;
	}
}

@media ( max-width : 760px) {
	.seller-page .seller-header-inner {
		width: calc(100% - 32px);
		flex-wrap: wrap;
		gap: 12px 18px;
		padding: 14px 0;
	}
	.seller-page .seller-nav {
		flex: 1 1 100%;
		justify-content: flex-start;
		flex-wrap: wrap;
		gap: 8px 22px;
		margin-left: 0;
	}
	.seller-page .seller-main {
		width: calc(100% - 32px);
		padding-top: 24px;
	}
	.seller-page .seller-dashboard-shortcuts {
		grid-template-columns: 1fr;
	}
	.seller-page .seller-panel {
		padding: 20px;
	}
}

@media ( max-width : 480px) {
	.seller-page .seller-header-inner {
		width: calc(100% - 24px);
		gap: 10px 14px;
	}
	.seller-page .seller-header .keeper-brand, .seller-page .seller-header .keeper-brand-text
		{
		font-size: 29px;
	}
	.seller-page .seller-subtitle {
		padding-left: 12px;
		font-size: 13px;
	}
	.seller-page .seller-nav {
		gap: 6px 16px;
	}
	.seller-page .seller-nav-link {
		font-size: 13px;
	}
	.seller-page .seller-main {
		width: calc(100% - 24px);
		padding-top: 20px;
	}
	.seller-page .seller-page-heading h1 {
		font-size: 27px;
	}
	.seller-page .seller-dashboard-grid {
		gap: 10px;
	}
	.seller-page .seller-dashboard-card {
		padding: 15px;
		border-radius: 13px;
	}
	.seller-page .seller-dashboard-card-label {
		margin-bottom: 12px;
		font-size: 12px;
	}
	.seller-page .seller-dashboard-card-value {
		font-size: 25px;
	}
	.seller-page .seller-dashboard-card-note {
		font-size: 12px;
	}
	.seller-page .seller-panel {
		padding: 16px;
	}
	.seller-page .seller-dashboard-panel-heading {
		align-items: flex-start;
		gap: 10px;
	}
	.seller-page .seller-dashboard-section-title {
		font-size: 18px;
	}
}
</style>
</head>

<body class="seller-page">
	<header class="seller-header">
		<div class="seller-header-inner">
			<a class="keeper-brand" href="${pageContext.request.contextPath}/">
				<span class="keeper-brand-icon">K</span><span
				class="keeper-brand-text">Keeper</span>
			</a>

			<div class="seller-subtitle">Kênh người bán</div>

			<nav class="seller-nav">
				<a class="seller-nav-link"
					href="${pageContext.request.contextPath}/seller/dashboard"
					aria-current="page">Tổng quan</a> <a class="seller-nav-link"
					href="${pageContext.request.contextPath}/seller/shops">Cửa hàng</a>
				<a class="seller-nav-link"
					href="${pageContext.request.contextPath}/seller/listings">Sách
					đăng bán</a> <a class="seller-nav-link"
					href="${pageContext.request.contextPath}/seller/orders">Đơn
					hàng</a>
			</nav>
		</div>
	</header>

	<main class="seller-main">
		<section class="seller-page-heading">
			<p class="seller-eyebrow">SELLER CENTER</p>
			<h1>Trang tổng quan</h1>
			<p class="seller-muted">Theo dõi cửa hàng, sách đăng bán và đơn
				hàng của bạn.</p>
		</section>

		<section class="seller-dashboard-grid">
			<article class="seller-dashboard-card">
				<p class="seller-dashboard-card-label">Tổng cửa hàng</p>
				<p class="seller-dashboard-card-value">
					<fmt:formatNumber value="${shopSummary.totalShops}" pattern="#,##0" />
				</p>
				<p class="seller-dashboard-card-note">
					<fmt:formatNumber value="${shopSummary.approvedShops}"
						pattern="#,##0" />
					đã duyệt và đang hoạt động
				</p>
			</article>

			<article class="seller-dashboard-card">
				<p class="seller-dashboard-card-label">Sách đang đăng bán</p>
				<p class="seller-dashboard-card-value">
					<fmt:formatNumber value="${listingSummary.activeListings}"
						pattern="#,##0" />
				</p>
				<p class="seller-dashboard-card-note">
					Tổng cộng
					<fmt:formatNumber value="${listingSummary.totalListings}"
						pattern="#,##0" />
					tin đăng
				</p>
			</article>

			<article class="seller-dashboard-card">
				<p class="seller-dashboard-card-label">Tổng đơn hàng</p>
				<p class="seller-dashboard-card-value">
					<fmt:formatNumber value="${orderSummary.totalOrders}"
						pattern="#,##0" />
				</p>
				<p class="seller-dashboard-card-note">Trên tất cả cửa hàng của
					bạn</p>
			</article>

			<article class="seller-dashboard-card">
				<p class="seller-dashboard-card-label">Đơn cần xử lý</p>
				<p class="seller-dashboard-card-value">
					<fmt:formatNumber value="${orderSummary.ordersNeedingAction}"
						pattern="#,##0" />
				</p>
				<p class="seller-dashboard-card-note">Chờ xác nhận hoặc chuẩn bị
					hàng</p>
			</article>
		</section>

		<section class="seller-dashboard-shortcuts">
			<a class="seller-dashboard-shortcut"
				href="${pageContext.request.contextPath}/seller/shops"> <strong>Quản
					lý cửa hàng</strong> <span>Xem trạng thái duyệt và thông tin cửa
					hàng.</span>
			</a> <a class="seller-dashboard-shortcut"
				href="${pageContext.request.contextPath}/seller/listings"> <strong>Quản
					lý sách</strong> <span>Cập nhật giá, tồn kho và trạng thái đăng bán.</span>
			</a> <a class="seller-dashboard-shortcut"
				href="${pageContext.request.contextPath}/seller/orders"> <strong>Quản
					lý đơn hàng</strong> <span>Xem đơn hàng và cập nhật tiến trình xử lý.</span>
			</a>
		</section>

		<section class="seller-dashboard-sections">
			<div class="seller-panel">
				<div class="seller-dashboard-panel-heading">
					<h2 class="seller-dashboard-section-title">Đơn hàng gần đây</h2>
					<a href="${pageContext.request.contextPath}/seller/orders">Xem
						tất cả</a>
				</div>

				<c:choose>
					<c:when test="${not empty recentOrders}">
						<div class="seller-dashboard-table-wrap">
							<table class="seller-table">
								<thead>
									<tr>
										<th>Mã đơn</th>
										<th>Cửa hàng</th>
										<th>Trạng thái</th>
										<th class="seller-text-right">Tổng tiền</th>
										<th>Ngày tạo</th>
									</tr>
								</thead>
								<tbody>
									<c:forEach var="order" items="${recentOrders}">
										<tr>
											<td><strong><c:out value="${order.orderCode}" /></strong></td>
											<td><c:out value="${order.shopName}" /></td>
											<td><span
												class="seller-dashboard-status seller-dashboard-status-${order.orderStatus.toLowerCase()}">
													<c:out value="${order.orderStatus}" />
											</span></td>
											<td class="seller-text-right"><fmt:formatNumber
													value="${order.totalAmount}" pattern="#,##0" /> đ</td>
											<td><fmt:formatDate value="${order.createdAt}"
													pattern="dd/MM/yyyy HH:mm" /></td>
										</tr>
									</c:forEach>
								</tbody>
							</table>
						</div>
					</c:when>
					<c:otherwise>
						<div class="seller-dashboard-empty">Chưa có đơn hàng nào.</div>
					</c:otherwise>
				</c:choose>
			</div>

			<div class="seller-panel">
				<div class="seller-dashboard-panel-heading">
					<h2 class="seller-dashboard-section-title">Trạng thái đơn hàng</h2>
				</div>

				<div class="seller-dashboard-breakdown">
					<div class="seller-dashboard-breakdown-row">
						<span class="seller-dashboard-breakdown-label">Chờ người
							bán xác nhận</span> <span class="seller-dashboard-breakdown-value">
							<fmt:formatNumber value="${orderSummary.pendingSellerOrders}"
								pattern="#,##0" />
						</span>
					</div>
					<div class="seller-dashboard-breakdown-row">
						<span class="seller-dashboard-breakdown-label">Đã xác nhận</span>
						<span class="seller-dashboard-breakdown-value"> <fmt:formatNumber
								value="${orderSummary.confirmedOrders}" pattern="#,##0" />
						</span>
					</div>
					<div class="seller-dashboard-breakdown-row">
						<span class="seller-dashboard-breakdown-label">Đang chuẩn
							bị</span> <span class="seller-dashboard-breakdown-value"> <fmt:formatNumber
								value="${orderSummary.preparingOrders}" pattern="#,##0" />
						</span>
					</div>
					<div class="seller-dashboard-breakdown-row">
						<span class="seller-dashboard-breakdown-label">Chờ bàn giao
							vận chuyển</span> <span class="seller-dashboard-breakdown-value">
							<fmt:formatNumber value="${orderSummary.readyToShipOrders}"
								pattern="#,##0" />
						</span>
					</div>
					<div class="seller-dashboard-breakdown-row">
						<span class="seller-dashboard-breakdown-label">Đang vận
							chuyển</span> <span class="seller-dashboard-breakdown-value"> <fmt:formatNumber
								value="${orderSummary.shippingOrders}" pattern="#,##0" />
						</span>
					</div>
					<div class="seller-dashboard-breakdown-row">
						<span class="seller-dashboard-breakdown-label">Đã giao</span> <span
							class="seller-dashboard-breakdown-value"> <fmt:formatNumber
								value="${orderSummary.deliveredOrders}" pattern="#,##0" />
						</span>
					</div>
					<div class="seller-dashboard-breakdown-row">
						<span class="seller-dashboard-breakdown-label">Hoàn tất</span> <span
							class="seller-dashboard-breakdown-value"> <fmt:formatNumber
								value="${orderSummary.completedOrders}" pattern="#,##0" />
						</span>
					</div>
				</div>
			</div>
		</section>

		<section class="seller-panel" style="margin-top: 22px;">
			<div class="seller-dashboard-panel-heading">
				<h2 class="seller-dashboard-section-title">Cửa hàng của bạn</h2>
				<a href="${pageContext.request.contextPath}/seller/shops">Quản
					lý cửa hàng</a>
			</div>

			<c:choose>
				<c:when test="${not empty shops}">
					<div class="seller-dashboard-table-wrap">
						<table class="seller-table">
							<thead>
								<tr>
									<th>Tên cửa hàng</th>
									<th>Trạng thái duyệt</th>
									<th>Hoạt động</th>
									<th class="seller-text-right">Tin đăng</th>
									<th class="seller-text-right">Đơn hàng</th>
									<th>Thao tác</th>
								</tr>
							</thead>
							<tbody>
								<c:forEach var="shop" items="${shops}">
									<tr>
										<td>
											<div class="seller-dashboard-shop-name">
												<c:out value="${shop.shopName}" />
											</div> <small class="seller-muted">/<c:out
													value="${shop.shopSlug}" /></small>
										</td>
										<td><span
											class="seller-dashboard-status seller-dashboard-status-${shop.approvalStatus.toLowerCase()}">
												<c:out value="${shop.approvalStatus}" />
										</span></td>
										<td><c:choose>
												<c:when test="${shop.isActive}">
													<span
														class="seller-dashboard-status seller-dashboard-status-active">Đang
														hoạt động</span>
												</c:when>
												<c:otherwise>
													<span class="seller-dashboard-status">Đã tắt</span>
												</c:otherwise>
											</c:choose></td>
										<td class="seller-text-right"><fmt:formatNumber
												value="${shop.totalListings}" pattern="#,##0" /></td>
										<td class="seller-text-right"><fmt:formatNumber
												value="${shop.totalOrders}" pattern="#,##0" /></td>
										<td><c:if
												test="${shop.approvalStatus eq 'APPROVED' and shop.isActive}">
												<a
													href="${pageContext.request.contextPath}/seller/listings?shopId=${shop.shopId}">Sách</a>
												<span> · </span>
												<a
													href="${pageContext.request.contextPath}/seller/orders?shopId=${shop.shopId}">Đơn
													hàng</a>
											</c:if></td>
									</tr>
								</c:forEach>
							</tbody>
						</table>
					</div>
				</c:when>
				<c:otherwise>
					<div class="seller-dashboard-empty">
						Bạn chưa có cửa hàng.
						<div style="margin-top: 12px;">
							<a class="seller-button seller-button-primary"
								href="${pageContext.request.contextPath}/seller/shops/new">Tạo
								cửa hàng</a>
						</div>
					</div>
				</c:otherwise>
			</c:choose>
		</section>

		<section class="seller-panel" style="margin-top: 22px;">
			<div class="seller-dashboard-panel-heading">
				<h2 class="seller-dashboard-section-title">Tình hình sách đăng
					bán</h2>
				<a href="${pageContext.request.contextPath}/seller/listings">Quản
					lý sách</a>
			</div>

			<div class="seller-dashboard-breakdown">
				<div class="seller-dashboard-breakdown-row">
					<span class="seller-dashboard-breakdown-label">Đang bán</span> <span
						class="seller-dashboard-breakdown-value"> <fmt:formatNumber
							value="${listingSummary.activeListings}" pattern="#,##0" />
					</span>
				</div>
				<div class="seller-dashboard-breakdown-row">
					<span class="seller-dashboard-breakdown-label">Hết hàng</span> <span
						class="seller-dashboard-breakdown-value"> <fmt:formatNumber
							value="${listingSummary.soldOutListings}" pattern="#,##0" />
					</span>
				</div>
				<div class="seller-dashboard-breakdown-row">
					<span class="seller-dashboard-breakdown-label">Bản nháp</span> <span
						class="seller-dashboard-breakdown-value"> <fmt:formatNumber
							value="${listingSummary.draftListings}" pattern="#,##0" />
					</span>
				</div>
				<div class="seller-dashboard-breakdown-row">
					<span class="seller-dashboard-breakdown-label">Đang ẩn</span> <span
						class="seller-dashboard-breakdown-value"> <fmt:formatNumber
							value="${listingSummary.hiddenListings}" pattern="#,##0" />
					</span>
				</div>
				<div class="seller-dashboard-breakdown-row">
					<span class="seller-dashboard-breakdown-label">Đã gỡ</span> <span
						class="seller-dashboard-breakdown-value"> <fmt:formatNumber
							value="${listingSummary.removedListings}" pattern="#,##0" />
					</span>
				</div>
				<div class="seller-dashboard-breakdown-row">
					<span class="seller-dashboard-breakdown-label">Tổng tồn kho</span>
					<span class="seller-dashboard-breakdown-value"> <fmt:formatNumber
							value="${listingSummary.totalStock}" pattern="#,##0" />
					</span>
				</div>
				<div class="seller-dashboard-breakdown-row">
					<span class="seller-dashboard-breakdown-label">Tổng số lượng
						đã bán</span> <span class="seller-dashboard-breakdown-value"> <fmt:formatNumber
							value="${listingSummary.totalSold}" pattern="#,##0" />
					</span>
				</div>
			</div>
		</section>
	</main>
</body>
</html>
