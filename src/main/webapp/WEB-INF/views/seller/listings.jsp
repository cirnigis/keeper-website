<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<!DOCTYPE html>
<html lang="vi">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Quản lý sản phẩm | Keeper</title>

<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/style.css">

<style>
.seller-page {
	min-height: 100vh;
	padding: 0 0 48px;
	background: #f5f7fb;
	color: #25324b;
}

.seller-container {
	width: min(1440px, calc(100% - 48px));
	margin: 0 auto;
}

.seller-page .seller-header {
	width: 100%;
	margin: 0 0 32px;
	padding: 0;
	background: #fff;
	border: none;
	border-bottom: 1px solid #e1e5eb;
	border-radius: 0;
	box-shadow: none;
	box-sizing: border-box;
}

.seller-page .seller-header *, .seller-page .seller-header *::before,
	.seller-page .seller-header *::after {
	box-sizing: border-box;
}

.seller-page .seller-header-inner {
	width: min(1760px, 100%);
	min-height: 92px;
	margin: 0 auto;
	padding: 0 4%;
	display: flex;
	align-items: center;
	justify-content: space-between;
	gap: 28px;
}

.seller-page .seller-header-brand {
	display: flex;
	align-items: center;
	flex: 0 0 auto;
	gap: 28px;
	min-width: 0;
}

.seller-page .seller-header-brand .keeper-brand {
	display: inline-flex;
	align-items: center;
	flex: 0 0 auto;
	margin: 0;
	white-space: nowrap;
	text-decoration: none;
}

.seller-page .seller-subtitle {
	flex: 0 0 auto;
	margin: 0;
	padding: 8px 0 8px 28px;
	border-left: 1px solid #e1e5eb;
	color: #64748b;
	font-size: 16px;
	line-height: 1.5;
	white-space: nowrap;
}

.seller-page .seller-nav {
	display: flex;
	align-items: center;
	justify-content: flex-end;
	flex: 1 1 auto;
	gap: 30px;
	min-width: 0;
	margin: 0;
	padding: 0;
}

.seller-page .seller-nav-link {
	display: inline-flex;
	align-items: center;
	justify-content: center;
	flex: 0 0 auto;
	padding: 10px 0;
	border: 0;
	border-radius: 0;
	background: transparent;
	color: #14213d;
	font-size: 16px;
	font-weight: 600;
	line-height: 1.4;
	text-decoration: none;
	white-space: nowrap;
	transition: color .2s ease;
}

.seller-page .seller-nav-link:hover, .seller-page .seller-nav-link:focus-visible,
	.seller-page .seller-nav-link.active {
	background: transparent;
	color: #1463f3;
	text-decoration: none;
}

/* =========================
   PAGE TITLE
========================= */
.listings-page-header {
	display: flex;
	align-items: flex-start;
	justify-content: space-between;
	flex-wrap: wrap;
	gap: 20px;
	margin-bottom: 24px;
}

.listings-page-header h1 {
	margin: 0 0 8px;
	color: #172541;
	font-size: clamp(25px, 3vw, 32px);
	font-weight: 750;
	letter-spacing: -.7px;
}

.listings-page-header p {
	margin: 0;
	color: #748198;
	font-size: 15px;
	line-height: 1.6;
}

.listings-header-actions {
	display: flex;
	flex-wrap: wrap;
	gap: 10px;
}

.listings-page .btn {
	display: inline-flex;
	align-items: center;
	justify-content: center;
	min-height: 42px;
	padding: 10px 16px;
	border: 1px solid transparent;
	border-radius: 9px;
	font-size: 14px;
	font-weight: 650;
	line-height: 1.4;
	text-align: center;
	text-decoration: none;
	cursor: pointer;
	transition: background .2s ease, color .2s ease, border-color .2s ease,
		transform .2s ease, box-shadow .2s ease;
}

.listings-page .btn:hover {
	transform: translateY(-1px);
	text-decoration: none;
}

.listings-page .btn:focus-visible {
	outline: 3px solid rgba(65, 116, 224, .25);
	outline-offset: 2px;
}

.listings-page .btn-primary {
	background: #285bd4;
	color: #fff;
	box-shadow: 0 3px 8px rgba(40, 91, 212, .15);
}

.listings-page .btn-primary:hover {
	background: #1f4dbb;
	color: #fff;
}

.listings-page .btn-secondary {
	background: #fff;
	color: #42516b;
	border-color: #d9e1ed;
}

.listings-page .btn-secondary:hover {
	background: #f0f4fa;
	border-color: #c6d2e3;
	color: #25324b;
}

.listings-page .alert {
	margin-bottom: 20px;
	padding: 14px 16px;
	border: 1px solid transparent;
	border-radius: 10px;
	font-size: 14px;
	line-height: 1.5;
}

.listings-page .alert-success {
	background: #ecfdf3;
	border-color: #b8ebca;
	color: #187443;
}

.listings-page .alert-danger {
	background: #fff1f0;
	border-color: #ffd0cc;
	color: #b42318;
}

.listings-card {
	margin-bottom: 22px;
	padding: 24px;
	background: #fff;
	border: 1px solid #e5eaf2;
	border-radius: 15px;
	box-shadow: 0 4px 16px rgba(28, 45, 78, .035);
}

.listings-card h2 {
	margin: 0 0 10px;
	color: #1c2a45;
	font-size: 20px;
	font-weight: 700;
}

.listings-card p {
	margin: 0;
	color: #748198;
	font-size: 14px;
	line-height: 1.6;
}

.shop-filter {
	display: flex;
	align-items: flex-end;
	flex-wrap: wrap;
	gap: 14px;
}

.shop-filter-field {
	display: flex;
	flex: 1 1 280px;
	flex-direction: column;
	gap: 8px;
	min-width: 0;
}

.listings-page label {
	color: #34435d;
	font-size: 13px;
	font-weight: 650;
}

.listings-page input, .listings-page select {
	width: 100%;
	min-height: 42px;
	padding: 10px 12px;
	border: 1px solid #d7dfeb;
	border-radius: 8px;
	outline: none;
	background: #fff;
	color: #25324b;
	font: inherit;
	font-size: 14px;
	box-sizing: border-box;
}

.listings-page input:focus, .listings-page select:focus {
	border-color: #5785ed;
	box-shadow: 0 0 0 3px rgba(65, 116, 224, .12);
}

.listings-table-card {
	padding: 0;
	overflow: hidden;
}

.listings-table-heading {
	display: flex;
	align-items: center;
	justify-content: space-between;
	gap: 12px;
	padding: 22px 24px;
	border-bottom: 1px solid #e9edf4;
}

.listings-table-heading h2 {
	margin: 0;
}

.listings-count {
	color: #7b879b;
	font-size: 13px;
	white-space: nowrap;
}

.table-responsive {
	width: 100%;
	overflow-x: auto;
	-webkit-overflow-scrolling: touch;
}

.listings-page .data-table {
	width: 100%;
	min-width: 980px;
	border-collapse: collapse;
	background: #fff;
}

.listings-page .data-table th {
	padding: 14px 16px;
	background: #f8faff;
	color: #687790;
	font-size: 12px;
	font-weight: 750;
	text-align: left;
	text-transform: uppercase;
	letter-spacing: .35px;
	white-space: nowrap;
	border-bottom: 1px solid #e8edf5;
}

.listings-page .data-table td {
	padding: 17px 16px;
	border-bottom: 1px solid #edf0f5;
	color: #35435b;
	font-size: 14px;
	vertical-align: top;
}

.listings-page .data-table tbody tr:last-child td {
	border-bottom: none;
}

.listings-page .data-table tbody tr:hover {
	background: #fbfcff;
}

.listing-title {
	display: block;
	max-width: 270px;
	margin-bottom: 6px;
	color: #1d2b46;
	font-weight: 700;
	line-height: 1.5;
	overflow-wrap: anywhere;
}

.listings-page .muted {
	margin-top: 4px;
	color: #8994a7;
	font-size: 12px;
	line-height: 1.5;
}

.listing-price {
	color: #1c55bd;
	font-weight: 750;
	white-space: nowrap;
}

.listing-quantity {
	color: #34435d;
	font-weight: 650;
}

.listing-status, .listing-condition {
	display: inline-flex;
	align-items: center;
	padding: 5px 9px;
	border-radius: 20px;
	background: #f0f3f8;
	color: #596780;
	font-size: 12px;
	font-weight: 700;
	white-space: nowrap;
}

.listing-status-active {
	background: #e7f8ee;
	color: #187443;
}

.listing-status-hidden {
	background: #fff3dc;
	color: #9a5a00;
}

.listing-actions {
	display: flex;
	flex-direction: column;
	align-items: stretch;
	gap: 9px;
	min-width: 125px;
}

.listing-actions details {
	width: 100%;
}

.listing-actions summary {
	display: flex;
	align-items: center;
	justify-content: center;
	min-height: 36px;
	padding: 8px 12px;
	border: 1px solid #d9e1ed;
	border-radius: 8px;
	background: #fff;
	color: #42516b;
	font-size: 13px;
	font-weight: 650;
	cursor: pointer;
	list-style: none;
}

.listing-actions summary::-webkit-details-marker {
	display: none;
}

.listing-actions summary:hover {
	background: #f3f6fb;
}

.listing-edit-form {
	display: flex;
	flex-direction: column;
	gap: 9px;
	width: min(280px, 75vw);
	margin-top: 12px;
	padding: 16px;
	border: 1px solid #e1e7f0;
	border-radius: 10px;
	background: #fbfcff;
}

.listing-edit-form label {
	margin-top: 3px;
}

.listing-edit-form .btn {
	margin-top: 5px;
}

.listing-actions>form {
	margin: 0;
}

.listing-actions>form .btn {
	width: 100%;
	min-height: 36px;
	padding: 8px 12px;
	font-size: 13px;
}

.listings-empty {
	padding: 42px 24px;
	text-align: center;
}

.listings-empty-icon {
	display: flex;
	align-items: center;
	justify-content: center;
	width: 56px;
	height: 56px;
	margin: 0 auto 16px;
	border-radius: 16px;
	background: #edf3ff;
	color: #285bd4;
	font-size: 25px;
}

.listings-empty h2 {
	margin-bottom: 8px;
}

.listings-empty p {
	margin-bottom: 20px;
}

@media ( max-width : 1000px) {
	.seller-page .seller-header-inner {
		min-height: auto;
		flex-wrap: wrap;
		gap: 16px 24px;
		padding: 20px 24px;
	}
	.seller-page .seller-nav {
		flex: 1 1 100%;
		justify-content: flex-start;
		flex-wrap: wrap;
		gap: 10px 24px;
	}
}

@media ( max-width : 600px) {
	.seller-container {
		width: calc(100% - 24px);
	}
	.seller-page .seller-header-inner {
		padding: 16px;
		gap: 14px;
	}
	.seller-page .seller-header-brand {
		gap: 12px;
	}
	.seller-page .seller-subtitle {
		padding-left: 12px;
		font-size: 13px;
	}
	.seller-page .seller-nav {
		gap: 8px 18px;
	}
	.seller-page .seller-nav-link {
		font-size: 13px;
	}
	.listings-page-header {
		flex-direction: column;
	}
	.listings-header-actions {
		width: 100%;
	}
	.listings-header-actions .btn {
		flex: 1 1 auto;
	}
	.listings-card {
		padding: 17px;
	}
	.listings-table-card {
		padding: 0;
	}
	.listings-table-heading {
		align-items: flex-start;
		flex-direction: column;
		padding: 17px;
	}
	.shop-filter {
		align-items: stretch;
		flex-direction: column;
	}
	.shop-filter-field {
		width: 100%;
		flex-basis: auto;
	}
	.shop-filter>.btn {
		width: 100%;
	}
}

@media ( max-width : 380px) {
	.seller-page .seller-header-brand {
		align-items: flex-start;
		flex-direction: column;
		gap: 8px;
	}
	.seller-page .seller-subtitle {
		padding: 0;
		border-left: none;
	}
	.seller-page .seller-nav-link {
		padding: 8px;
		font-size: 11px;
	}
}
</style>
</head>

<body>
	<div class="seller-page listings-page">

		<!-- HEADER FULL WIDTH -->
		<header class="seller-header">
			<div class="seller-header-inner">
				<div class="seller-header-brand">
					<a class="keeper-brand" href="${pageContext.request.contextPath}/">
						<span class="keeper-brand-icon">K</span> <span
						class="keeper-brand-text">Keeper</span>
					</a> <span class="seller-subtitle">Kênh người bán</span>
				</div>

				<nav class="seller-nav" aria-label="Điều hướng khu vực người bán">
					<a class="seller-nav-link"
						href="${pageContext.request.contextPath}/seller/dashboard">
						Tổng quan </a> <a class="seller-nav-link"
						href="${pageContext.request.contextPath}/seller/shops"> Cửa
						hàng </a> <a class="seller-nav-link active"
						href="${pageContext.request.contextPath}/seller/listings"
						aria-current="page"> Sách đăng bán </a> <a class="seller-nav-link"
						href="${pageContext.request.contextPath}/seller/orders"> Đơn
						hàng </a>
				</nav>
			</div>
		</header>

		<!-- PAGE CONTENT -->
		<div class="seller-container">
			<main>
				<section class="listings-page-header">
					<div>
						<h1>Quản lý sản phẩm</h1>
						<p>Quản lý giá bán, tình trạng sách và tồn kho.</p>
					</div>

					<div class="listings-header-actions">
						<a class="btn btn-secondary"
							href="${pageContext.request.contextPath}/seller/shops"> Quay
							lại shop </a> <a class="btn btn-primary"
							href="${pageContext.request.contextPath}/seller/listings/new?shopId=${selectedShopId}">
							Đăng bán sách </a>
					</div>
				</section>

				<c:if test="${not empty successMessage}">
					<div class="alert alert-success" role="status">
						<c:out value="${successMessage}" />
					</div>
				</c:if>

				<c:if test="${not empty errorMessage}">
					<div class="alert alert-danger" role="alert">
						<c:out value="${errorMessage}" />
					</div>
				</c:if>

				<section class="listings-card">
					<form class="shop-filter" method="get"
						action="${pageContext.request.contextPath}/seller/listings">

						<div class="shop-filter-field">
							<label for="shopId">Chọn shop</label> <select id="shopId"
								name="shopId" required>
								<c:forEach var="shop" items="${shops}">
									<option value="${shop.shopId}"
										${shop.shopId eq selectedShopId ? 'selected' : ''}
										<c:if test="${shop.approvalStatus ne 'APPROVED' or not shop.isActive}">
            disabled
        </c:if>>
										<c:out value="${shop.shopName}" />
									</option>
								</c:forEach>
							</select>
						</div>

						<button class="btn btn-primary" type="submit">Xem sản
							phẩm</button>
					</form>
				</section>

				<c:choose>
					<c:when test="${empty listings}">
						<section class="listings-card listings-empty">
							<div class="listings-empty-icon" aria-hidden="true">📚</div>

							<h2>Chưa có sản phẩm</h2>

							<p>Hãy đăng bán một cuốn sách có sẵn trong danh mục hệ thống.
							</p>

							<a class="btn btn-primary"
								href="${pageContext.request.contextPath}/seller/listings/new?shopId=${selectedShopId}">
								Đăng bán sách </a>
						</section>
					</c:when>

					<c:otherwise>
						<section class="listings-card listings-table-card">
							<div class="listings-table-heading">
								<h2>Danh sách sản phẩm</h2>
								<span class="listings-count"> Sản phẩm trong shop </span>
							</div>

							<div class="table-responsive">
								<table class="data-table">
									<thead>
										<tr>
											<th scope="col">Sách</th>
											<th scope="col">Tình trạng</th>
											<th scope="col">Giá bán</th>
											<th scope="col">Tồn kho</th>
											<th scope="col">Đã bán</th>
											<th scope="col">Trạng thái</th>
											<th scope="col">Thao tác</th>
										</tr>
									</thead>

									<tbody>
										<c:forEach var="item" items="${listings}">
											<tr>
												<td><strong class="listing-title"> <c:out
															value="${item.title}" />
												</strong>

													<div class="muted">
														ISBN:
														<c:out value="${item.isbn}" />
													</div>

													<div class="muted">
														Listing #
														<c:out value="${item.listingId}" />
													</div></td>

												<td><span class="listing-condition"> <c:out
															value="${item.conditionCode}" />
												</span></td>

												<td><span class="listing-price"> <fmt:formatNumber
															value="${item.unitPrice}" type="number"
															maxFractionDigits="0" groupingUsed="true" /> ₫
												</span></td>

												<td><span class="listing-quantity"> <c:out
															value="${item.stockQuantity}" />
												</span></td>

												<td><span class="listing-quantity"> <c:out
															value="${item.soldQuantity}" />
												</span></td>

												<td><c:choose>
														<c:when test="${item.listingStatus eq 'ACTIVE'}">
															<span class="listing-status listing-status-active">
																Đang hiển thị </span>
														</c:when>

														<c:when test="${item.listingStatus eq 'HIDDEN'}">
															<span class="listing-status listing-status-hidden">
																Đang ẩn </span>
														</c:when>

														<c:otherwise>
															<span class="listing-status"> <c:out
																	value="${item.listingStatus}" />
															</span>
														</c:otherwise>
													</c:choose></td>

												<td>
													<div class="listing-actions">
														<details>
															<summary>Chỉnh sửa</summary>

															<form class="listing-edit-form" method="post"
																action="${pageContext.request.contextPath}/seller/listings/update">

																<input type="hidden" name="${_csrf.parameterName}"
																	value="${_csrf.token}"> <input type="hidden"
																	name="shopId" value="${selectedShopId}"> <input
																	type="hidden" name="listingId"
																	value="${item.listingId}"> <label
																	for="listingTitle-${item.listingId}"> Tên hiển
																	thị </label> <input id="listingTitle-${item.listingId}"
																	name="listingTitle" maxlength="300"
																	value="<c:out value='${item.title}'/>"> <label
																	for="conditionCode-${item.listingId}"> Tình
																	trạng </label> <select id="conditionCode-${item.listingId}"
																	name="conditionCode" required>
																	<option value="LIKE_NEW"
																		${item.conditionCode eq 'LIKE_NEW' ? 'selected' : ''}>
																		Như mới</option>

																	<option value="VERY_GOOD"
																		${item.conditionCode eq 'VERY_GOOD' ? 'selected' : ''}>
																		Rất tốt</option>

																	<option value="GOOD"
																		${item.conditionCode eq 'GOOD' ? 'selected' : ''}>
																		Tốt</option>

																	<option value="ACCEPTABLE"
																		${item.conditionCode eq 'ACCEPTABLE' ? 'selected' : ''}>
																		Chấp nhận được</option>
																</select> <label for="unitPrice-${item.listingId}"> Giá
																	bán </label> <input id="unitPrice-${item.listingId}"
																	type="number" name="unitPrice" min="0" step="0.01"
																	required value="${item.unitPrice}"> <label
																	for="stockQuantity-${item.listingId}"> Tồn kho
																</label> <input id="stockQuantity-${item.listingId}"
																	type="number" name="stockQuantity" min="0" required
																	value="${item.stockQuantity}">

																<button class="btn btn-primary" type="submit">
																	Lưu thay đổi</button>
															</form>
														</details>

														<form method="post"
															action="${pageContext.request.contextPath}/seller/listings/status">

															<input type="hidden" name="${_csrf.parameterName}"
																value="${_csrf.token}"> <input type="hidden"
																name="shopId" value="${selectedShopId}"> <input
																type="hidden" name="listingId" value="${item.listingId}">

															<c:choose>
																<c:when test="${item.listingStatus eq 'ACTIVE'}">
																	<input type="hidden" name="status" value="HIDDEN">

																	<button class="btn btn-secondary" type="submit">
																		Ẩn sản phẩm</button>
																</c:when>

																<c:when
																	test="${item.listingStatus eq 'HIDDEN' and item.stockQuantity gt 0}">
																	<input type="hidden" name="status" value="ACTIVE">

																	<button class="btn btn-primary" type="submit">
																		Hiện sản phẩm</button>
																</c:when>
															</c:choose>
														</form>
													</div>
												</td>
											</tr>
										</c:forEach>
									</tbody>
								</table>
							</div>
						</section>
					</c:otherwise>
				</c:choose>
			</main>
		</div>
	</div>
</body>
</html>

