<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Quản lý cửa hàng | Keeper</title>
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/style.css">
</head>
<body class="seller-page">

	<nav class="seller-navbar">
		<div class="seller-navbar-inner">
			<a class="keeper-brand" href="${pageContext.request.contextPath}/">
				<span class="keeper-brand-icon">K</span> <span
				class="keeper-brand-text">eeper</span>
			</a>

			<div class="seller-nav-links">
				<a href="${pageContext.request.contextPath}/seller/shops">Cửa
					hàng</a> <a href="${pageContext.request.contextPath}/seller/listings">Sách
					đăng bán</a> <a href="${pageContext.request.contextPath}/seller/orders">Đơn
					hàng</a> <a href="${pageContext.request.contextPath}/">Trang chủ</a>
				<form action="${pageContext.request.contextPath}/auth/logout"
					method="post">
					<input type="hidden" name="${_csrf.parameterName}"
						value="${_csrf.token}">
					<button type="submit" class="btn btn-sm btn-outline-light">
						Đăng xuất</button>
				</form>
			</div>
		</div>
	</nav>

	<main class="seller-container">
		<c:if test="${param.status == 'shop-inactive'}">
			<div class="seller-alert seller-alert-error" role="status">Tài
				khoản có vai trò Người bán nhưng hiện chưa có shop nào vừa được
				duyệt vừa đang hoạt động. Bạn có thể theo dõi trạng thái shop bên
				dưới.</div>
		</c:if>
		<c:if test="${param.status == 'buyer-role-unavailable'}">
			<div class="seller-alert seller-alert-error" role="status">Tài
				khoản này chưa có vai trò Người mua. Bạn đang được chuyển đến khu
				vực shop theo vai trò hiện có.</div>
		</c:if>
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

		<header class="seller-heading">
			<h1>Cửa hàng của tôi</h1>
			<p>Quản lý thông tin cửa hàng và theo dõi trạng thái xét duyệt
				trên Keeper.</p>
		</header>

		<section class="seller-panel">
			<div
				class="d-flex flex-wrap justify-content-between align-items-center gap-3 mb-4">
				<div>
					<h2 class="mb-1">Danh sách cửa hàng</h2>
					<p class="text-muted mb-0">Các cửa hàng thuộc tài khoản của
						bạn.</p>
				</div>
				<a class="keeper-btn-primary"
					href="${pageContext.request.contextPath}/seller/shop-register">
					Đăng ký cửa hàng </a>
			</div>

			<c:choose>
				<c:when test="${not empty shops}">
					<div class="seller-table-wrapper">
						<table class="seller-table">
							<thead>
								<tr>
									<th>Cửa hàng</th>
									<th>Đường dẫn</th>
									<th>Loại cửa hàng</th>
									<th>Trạng thái duyệt</th>
									<th>Hoạt động</th>
									<th>Thao tác</th>
								</tr>
							</thead>
							<tbody>
								<c:forEach var="shop" items="${shops}">
									<tr>
										<td><strong> <c:out value="${shop.shopName}" />
										</strong></td>
										<td><c:out value="${shop.shopSlug}" /></td>
										<td><c:choose>
												<c:when test="${shop.ownerType eq 'BUSINESS'}">
                                            Doanh nghiệp
                                        </c:when>
												<c:otherwise>
                                            Cá nhân
                                        </c:otherwise>
											</c:choose></td>
										<td><c:choose>
												<c:when test="${shop.approvalStatus eq 'APPROVED'}">
													<span class="seller-status seller-status-approved">Đã
														duyệt</span>
												</c:when>
												<c:when test="${shop.approvalStatus eq 'PENDING'}">
													<span class="seller-status seller-status-pending">Chờ
														duyệt</span>
												</c:when>
												<c:when test="${shop.approvalStatus eq 'REJECTED'}">
													<span class="seller-status seller-status-rejected">Bị
														từ chối</span>
												</c:when>
												<c:when test="${shop.approvalStatus eq 'SUSPENDED'}">
													<span class="seller-status seller-status-suspended">Đình
														chỉ</span>
												</c:when>
												<c:when test="${shop.approvalStatus eq 'CLOSED'}">
													<span class="seller-status seller-status-hidden">Đã
														đóng</span>
												</c:when>
												<c:otherwise>
													<span class="seller-status">Không xác định</span>
												</c:otherwise>
											</c:choose></td>
										<td><c:choose>
												<c:when test="${shop.isActive}">
													<span class="seller-status seller-status-active">Đang
														hoạt động</span>
												</c:when>
												<c:otherwise>
													<span class="seller-status seller-status-hidden">Ngừng
														hoạt động</span>
												</c:otherwise>
											</c:choose></td>
										<td>
											<div class="seller-table-actions">
												<c:if
													test="${shop.approvalStatus eq 'APPROVED' and shop.isActive}">
													<a class="btn btn-sm btn-outline-primary"
														href="${pageContext.request.contextPath}/seller/listings?shopId=${shop.shopId}">
														Quản lý sách </a>
													<a class="btn btn-sm btn-outline-secondary"
														href="${pageContext.request.contextPath}/seller/orders?shopId=${shop.shopId}">
														Đơn hàng </a>
												</c:if>
											</div>
										</td>
									</tr>
								</c:forEach>
							</tbody>
						</table>
					</div>
				</c:when>
				<c:otherwise>
					<div class="seller-empty">
						<h3>Bạn chưa có cửa hàng</h3>
						<p>Đăng ký cửa hàng để bắt đầu đăng bán sách trên Keeper.</p>
						<a class="keeper-btn-primary"
							href="${pageContext.request.contextPath}/seller/shop-register">
							Đăng ký cửa hàng ngay </a>
					</div>
				</c:otherwise>
			</c:choose>
		</section>
	</main>

</body>
</html>