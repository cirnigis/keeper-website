<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>

<!DOCTYPE html>
<html lang="vi">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">

<title><c:out value="${pageTitle}" /> | Keeper</title>

<link
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
	rel="stylesheet">

<link href="${pageContext.request.contextPath}/css/style.css"
	rel="stylesheet">

<style>
body {
	background: #f5f7fb;
	color: #212529;
}

.dashboard-navbar {
	background: #212529;
}

.dashboard-header {
	background: linear-gradient(135deg, #343a40, #495057);
	color: #fff;
	border-radius: 16px;
	padding: 30px;
}

.dashboard-card {
	height: 100%;
	border: 1px solid #e9ecef;
	border-radius: 14px;
	transition: transform .15s ease, box-shadow .15s ease;
}

.dashboard-card:hover {
	transform: translateY(-3px);
	box-shadow: 0 8px 24px rgba(0, 0, 0, .08);
}

.dashboard-card .card-body {
	padding: 24px;
}

.dashboard-card h2 {
	font-size: 1.15rem;
	font-weight: 700;
}

.dashboard-footer {
	color: #6c757d;
	font-size: .9rem;
}
</style>
</head>

<body>

	<nav class="navbar navbar-expand-lg navbar-dark dashboard-navbar">
		<div class="container">

			<a class="keeper-brand" href="${pageContext.request.contextPath}/"
				aria-label="Keeper - Trang chủ"> <span class="keeper-brand-icon">K</span>
				<span class="keeper-brand-text">eeper</span>
			</a>

			<button class="navbar-toggler" type="button"
				data-bs-toggle="collapse" data-bs-target="#dashboardNavbar"
				aria-controls="dashboardNavbar" aria-expanded="false"
				aria-label="Mở hoặc thu gọn thanh điều hướng">
				<span class="navbar-toggler-icon"></span>
			</button>

			<div class="collapse navbar-collapse" id="dashboardNavbar">

				<ul class="navbar-nav me-auto">
					<li class="nav-item"><a class="nav-link"
						href="${pageContext.request.contextPath}/books"> Khám phá sách
					</a></li>

					<c:if test="${roleName eq 'BUYER'}">
						<li class="nav-item"><a class="nav-link"
							href="${pageContext.request.contextPath}/buyer/cart"> Giỏ
								hàng </a></li>

						<li class="nav-item"><a class="nav-link"
							href="${pageContext.request.contextPath}/buyer/addresses">
								Địa chỉ giao hàng </a></li>
					</c:if>
				</ul>

				<div
					class="d-flex flex-column flex-lg-row align-items-lg-center gap-3 mt-3 mt-lg-0">

					<span class="text-light small"> Vai trò: <strong><c:out
								value="${roleName}" /></strong>
					</span> <a class="btn btn-outline-light btn-sm"
						href="${pageContext.request.contextPath}/buyer/change-password">
						Đổi mật khẩu </a>

					<form method="post"
						action="${pageContext.request.contextPath}/auth/logout"
						class="m-0">
						<input type="hidden" name="${_csrf.parameterName}"
							value="${_csrf.token}">

						<button type="submit" class="btn btn-warning btn-sm">
							Đăng xuất</button>
					</form>

				</div>
			</div>
		</div>
	</nav>

	<main class="container py-4 py-lg-5">

		<section class="dashboard-header mb-4 mb-lg-5">
			<div
				class="d-flex flex-column flex-md-row
                        align-items-md-center justify-content-between gap-3">

				<div>
					<p class="text-uppercase small mb-2 opacity-75">Không gian tài
						khoản</p>

					<h1 class="h2 fw-bold mb-2">
						<c:out value="${pageTitle}" />
					</h1>

					<p class="mb-0 opacity-75">
						<c:out value="${pageDescription}" />
					</p>
				</div>

				<a href="${pageContext.request.contextPath}/" class="btn btn-light">
					Về trang chủ </a>
			</div>
		</section>

		<div
			class="d-flex flex-column flex-sm-row
                    justify-content-between align-items-sm-center gap-2 mb-3">

			<h2 class="h4 fw-bold mb-0">Khu vực quản lý</h2>

			<span class="text-secondary small"> Chọn chức năng bạn muốn
				tiếp tục. </span>
		</div>

		<div class="row g-4">
			<c:forEach var="card" items="${cards}">
				<div class="col-12 col-md-6 col-xl-4">
					<div class="card dashboard-card">
						<div class="card-body d-flex flex-column">

							<div
								class="d-flex justify-content-between
                                        align-items-start gap-2 mb-3">

								<h2 class="mb-0">
									<c:out value="${card.title}" />
								</h2>

								<c:choose>
									<c:when test="${card.status eq 'Available'}">
										<span class="badge text-bg-success"> Khả dụng </span>
									</c:when>

									<c:otherwise>
										<span class="badge text-bg-secondary"> Sắp ra mắt </span>
									</c:otherwise>
								</c:choose>

							</div>

							<p class="text-secondary flex-grow-1">
								<c:out value="${card.description}" />
							</p>

							<c:choose>
								<c:when test="${not empty card.url}">
									<a class="btn btn-dark mt-2"
										href="${pageContext.request.contextPath}${card.url}"> Mở
										chức năng </a>
								</c:when>

								<c:otherwise>
									<button type="button" class="btn btn-outline-secondary mt-2"
										disabled>Chưa khả dụng</button>
								</c:otherwise>
							</c:choose>

						</div>
					</div>
				</div>
			</c:forEach>
		</div>

		<footer class="dashboard-footer text-center mt-5 pt-3"> Bản
			quyền &copy; Keeper 2026 </footer>

	</main>

	<script
		src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js">
		
	</script>

</body>
</html>