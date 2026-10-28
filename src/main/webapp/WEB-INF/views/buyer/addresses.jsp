
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>

<!DOCTYPE html>
<html lang="vi">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Địa chỉ giao hàng | Keeper</title>

<link
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
	rel="stylesheet">
<link href="${pageContext.request.contextPath}/css/styles.css"
	rel="stylesheet">
<link href="${pageContext.request.contextPath}/css/style.css"
	rel="stylesheet">

<style>
.keeper-brand {
	display: inline-flex;
	align-items: center;
	gap: 0;
	color: #14213d;
	text-decoration: none;
	font-size: 25px;
	font-weight: 800;
	letter-spacing: -1px;
}

.keeper-brand:hover {
	color: #1463f3;
}

.keeper-brand-icon {
	display: inline-flex;
	align-items: center;
	justify-content: center;
	width: 35px;
	height: 35px;
	margin-right: 2px;
	border-radius: 10px;
	background: #1463f3;
	color: #fff;
	font-size: 21px;
	letter-spacing: 0;
}

.keeper-brand-text {
	color: #14213d;
}

.keeper-brand:hover .keeper-brand-text {
	color: #1463f3;
}

.address-page-title {
	color: #14213d;
	font-weight: 700;
}

.address-card {
	border-radius: 14px;
	overflow: hidden;
}

.address-card .card-header {
	border-bottom: 1px solid #edf0f4;
}

.address-action-form {
	margin: 0;
}

@media ( max-width : 767.98px) {
	.address-navbar-links {
		flex-wrap: wrap;
		justify-content: flex-end;
		gap: 8px !important;
	}
}
</style>
</head>

<body>

	<nav
		class="navbar navbar-expand-lg navbar-light bg-white border-bottom">
		<div class="container">
			<a class="keeper-brand" href="${pageContext.request.contextPath}/"
				aria-label="Keeper - Trang chủ"> <span class="keeper-brand-icon">K</span>
				<span class="keeper-brand-text">eeper</span>
			</a>

			<div
				class="ms-auto d-flex align-items-center gap-3 address-navbar-links">
				<a class="nav-link" href="${pageContext.request.contextPath}/books">Tìm
					sách</a> <a class="nav-link"
					href="${pageContext.request.contextPath}/buyer/cart">Giỏ hàng</a> <a
					class="nav-link active"
					href="${pageContext.request.contextPath}/buyer/addresses">Địa
					chỉ</a>

				<form action="${pageContext.request.contextPath}/auth/logout"
					method="post" class="m-0">
					<input type="hidden" name="${_csrf.parameterName}"
						value="${_csrf.token}">
					<button type="submit" class="btn btn-outline-secondary btn-sm">Đăng
						xuất</button>
				</form>
			</div>
		</div>
	</nav>

	<main class="container py-5">
		<div class="row justify-content-center">
			<div class="col-lg-10">

				<div
					class="d-flex flex-wrap justify-content-between align-items-center mb-4 gap-3">
					<div>
						<h1 class="h3 address-page-title mb-1">Địa chỉ giao hàng</h1>
						<p class="text-muted mb-0">Quản lý địa chỉ sử dụng khi đặt mua
							sách trên Keeper.</p>
					</div>

					<a href="${pageContext.request.contextPath}/buyer/cart"
						class="btn btn-outline-secondary"> Quay lại giỏ hàng </a>
				</div>

				<c:if test="${not empty successMessage}">
					<div class="alert alert-success alert-dismissible fade show"
						role="alert">
						<c:out value="${successMessage}" />
						<button type="button" class="btn-close" data-bs-dismiss="alert"
							aria-label="Đóng"></button>
					</div>
				</c:if>

				<c:if test="${not empty errorMessage}">
					<div class="alert alert-danger alert-dismissible fade show"
						role="alert">
						<c:out value="${errorMessage}" />
						<button type="button" class="btn-close" data-bs-dismiss="alert"
							aria-label="Đóng"></button>
					</div>
				</c:if>

				<section class="card address-card border-0 shadow-sm mb-5">
					<div class="card-header bg-white py-3">
						<h2 class="h5 mb-0">Địa chỉ đã lưu</h2>
					</div>

					<div class="card-body p-4">
						<c:choose>
							<c:when test="${empty addresses}">
								<div class="text-center py-4">
									<h3 class="h6">Bạn chưa có địa chỉ giao hàng</h3>
									<p class="text-muted mb-0">Hãy thêm địa chỉ bên dưới để
										tiếp tục đặt hàng.</p>
								</div>
							</c:when>

							<c:otherwise>
								<c:forEach var="address" items="${addresses}">
									<div class="border rounded-3 p-3 mb-3">
										<div class="d-flex flex-wrap justify-content-between gap-3">
											<div class="flex-grow-1">
												<div class="fw-semibold">
													<c:out value="${address.recipientName}" />
													<span class="text-muted fw-normal"> | <c:out
															value="${address.phone}" />
													</span>
												</div>

												<div class="text-muted mt-2">
													<c:out value="${address.addressLine}" />
													,
													<c:out value="${address.ward}" />
													,
													<c:out value="${address.district}" />
													,
													<c:out value="${address.province}" />
												</div>

												<c:if test="${address['default']}">
													<span class="badge text-bg-success mt-2"> Địa chỉ
														mặc định </span>
												</c:if>
											</div>

											<div class="d-flex align-items-start gap-2">
												<c:if test="${not address['default']}">
													<form method="post"
														action="${pageContext.request.contextPath}/buyer/addresses/${address.addressId}/default"
														class="address-action-form">
														<input type="hidden" name="${_csrf.parameterName}"
															value="${_csrf.token}">
														<button type="submit"
															class="btn btn-outline-primary btn-sm">Đặt mặc
															định</button>
													</form>
												</c:if>

												<form method="post"
													action="${pageContext.request.contextPath}/buyer/addresses/${address.addressId}/delete"
													class="address-action-form"
													onsubmit="return confirm('Bạn có chắc muốn xóa địa chỉ này không?');">
													<input type="hidden" name="${_csrf.parameterName}"
														value="${_csrf.token}">
													<button type="submit" class="btn btn-outline-danger btn-sm">
														Xóa</button>
												</form>
											</div>
										</div>
									</div>
								</c:forEach>
							</c:otherwise>
						</c:choose>
					</div>
				</section>

				<section class="card address-card border-0 shadow-sm">
					<div class="card-header bg-white py-3">
						<h2 class="h5 mb-0">Thêm địa chỉ mới</h2>
					</div>

					<div class="card-body p-4">
						<form method="post"
							action="${pageContext.request.contextPath}/buyer/addresses/add">
							<input type="hidden" name="${_csrf.parameterName}"
								value="${_csrf.token}">

							<div class="row g-3">
								<div class="col-md-6">
									<label for="recipientName" class="form-label">Tên người
										nhận</label> <input id="recipientName" name="recipientName"
										class="form-control" maxlength="150" required>
								</div>

								<div class="col-md-6">
									<label for="phone" class="form-label">Số điện thoại</label> <input
										id="phone" name="phone" class="form-control" maxlength="20"
										required>
								</div>

								<div class="col-md-4">
									<label for="province" class="form-label">Tỉnh / Thành
										phố</label> <input id="province" name="province" class="form-control"
										maxlength="100" required>
								</div>

								<div class="col-md-4">
									<label for="district" class="form-label">Quận / Huyện</label> <input
										id="district" name="district" class="form-control"
										maxlength="100" required>
								</div>

								<div class="col-md-4">
									<label for="ward" class="form-label">Phường / Xã</label> <input
										id="ward" name="ward" class="form-control" maxlength="100"
										required>
								</div>

								<div class="col-12">
									<label for="addressLine" class="form-label">Địa chỉ cụ
										thể</label>
									<textarea id="addressLine" name="addressLine"
										class="form-control" rows="2" maxlength="300" required></textarea>
								</div>

								<div class="col-12">
									<button type="submit" class="btn btn-primary">Lưu địa
										chỉ</button>
								</div>
							</div>
						</form>
					</div>
				</section>

			</div>
		</div>
	</main>

	<footer class="border-top py-4 mt-5">
		<div class="container text-center text-muted small">Keeper · Kết
			nối bạn với những cuốn sách đáng đọc</div>
	</footer>

	<script
		src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
