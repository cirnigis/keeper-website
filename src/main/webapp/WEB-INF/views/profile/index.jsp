
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>

<!DOCTYPE html>
<html lang="vi">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Hồ sơ cá nhân | Keeper</title>

<link
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
	rel="stylesheet">
<link
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css"
	rel="stylesheet">

<style>
:root {
	--primary: #5b4bdb;
	--primary-dark: #4637bd;
	--background: #f5f6fb;
	--text: #25263a;
	--muted: #777b91;
	--border: #e8e9f2;
}

* {
	box-sizing: border-box;
}

body {
	margin: 0;
	background: var(--background);
	color: var(--text);
	font-family: "Segoe UI", Arial, sans-serif;
}

a {
	text-decoration: none;
}

.topbar {
	background: #fff;
	border-bottom: 1px solid var(--border);
	padding: 15px 0;
}

.brand {
	color: var(--primary);
	font-size: 25px;
	font-weight: 800;
}

.brand:hover {
	color: var(--primary-dark);
}

.topbar-link {
	color: #53566d;
	font-weight: 600;
	margin-left: 22px;
}

.topbar-link:hover {
	color: var(--primary);
}

.page-container {
	max-width: 1160px;
	margin: 36px auto;
	padding: 0 18px;
}

.page-heading {
	margin-bottom: 25px;
}

.page-heading h1 {
	font-size: 29px;
	font-weight: 800;
	margin-bottom: 8px;
}

.page-heading p {
	color: var(--muted);
	margin: 0;
}

.profile-layout {
	display: grid;
	grid-template-columns: 285px minmax(0, 1fr);
	gap: 24px;
	align-items: start;
}

.panel {
	background: #fff;
	border: 1px solid var(--border);
	border-radius: 18px;
	box-shadow: 0 8px 28px rgba(32, 35, 70, .045);
}

.sidebar {
	padding: 24px;
}

.avatar-wrap {
	text-align: center;
	padding-bottom: 22px;
	border-bottom: 1px solid var(--border);
}

.avatar {
	width: 112px;
	height: 112px;
	border-radius: 50%;
	object-fit: cover;
	border: 4px solid #efedff;
	background: #f0efff;
}

.avatar-placeholder {
	width: 112px;
	height: 112px;
	border-radius: 50%;
	margin: 0 auto;
	display: flex;
	align-items: center;
	justify-content: center;
	color: var(--primary);
	background: #efedff;
	font-size: 42px;
}

.profile-name {
	margin-top: 15px;
	font-weight: 800;
	font-size: 18px;
	overflow-wrap: anywhere;
}

.profile-email {
	color: var(--muted);
	font-size: 13px;
	overflow-wrap: anywhere;
	margin-top: 5px;
}

.role-pill {
	display: inline-block;
	margin-top: 12px;
	padding: 6px 12px;
	background: #efedff;
	color: var(--primary);
	border-radius: 30px;
	font-size: 12px;
	font-weight: 700;
}

.side-menu {
	margin-top: 20px;
	display: flex;
	flex-direction: column;
	gap: 7px;
}

.side-menu a {
	display: flex;
	align-items: center;
	gap: 12px;
	padding: 12px 13px;
	color: #555970;
	border-radius: 10px;
	font-size: 14px;
	font-weight: 600;
	transition: .2s;
}

.side-menu a i {
	width: 18px;
	text-align: center;
}

.side-menu a:hover, .side-menu a.active {
	color: var(--primary);
	background: #f0efff;
}

.main-panel {
	padding: 30px;
}

.section-title {
	font-size: 21px;
	font-weight: 800;
	margin-bottom: 7px;
}

.section-description {
	color: var(--muted);
	font-size: 14px;
	margin-bottom: 25px;
}

.form-label {
	font-size: 14px;
	font-weight: 700;
	margin-bottom: 8px;
}

.form-control {
	min-height: 46px;
	border-radius: 10px;
	border-color: #dedfec;
	padding: 11px 13px;
}

.form-control:focus {
	border-color: var(--primary);
	box-shadow: 0 0 0 .2rem rgba(91, 75, 219, .12);
}

.form-control[readonly] {
	background: #f7f7fb;
	color: #686b80;
}

.form-text {
	font-size: 12px;
	color: var(--muted);
}

.btn-primary-custom {
	background: var(--primary);
	border: 1px solid var(--primary);
	color: #fff;
	padding: 12px 22px;
	border-radius: 10px;
	font-weight: 700;
}

.btn-primary-custom:hover {
	background: var(--primary-dark);
	border-color: var(--primary-dark);
	color: #fff;
}

.btn-light-custom {
	background: #fff;
	color: #555970;
	border: 1px solid var(--border);
	padding: 12px 18px;
	border-radius: 10px;
	font-weight: 700;
}

.btn-light-custom:hover {
	background: #f7f7fb;
}

.quick-links {
	display: grid;
	grid-template-columns: repeat(2, minmax(0, 1fr));
	gap: 13px;
	margin-top: 28px;
}

.quick-link {
	display: flex;
	align-items: center;
	gap: 13px;
	padding: 16px;
	border: 1px solid var(--border);
	border-radius: 12px;
	color: var(--text);
	transition: .2s;
}

.quick-link:hover {
	border-color: #c9c3ff;
	background: #faf9ff;
	color: var(--primary);
}

.quick-link-icon {
	flex-shrink: 0;
	width: 42px;
	height: 42px;
	display: flex;
	align-items: center;
	justify-content: center;
	background: #efedff;
	color: var(--primary);
	border-radius: 10px;
	font-size: 17px;
}

.quick-link-title {
	font-weight: 750;
	font-size: 14px;
	margin-bottom: 4px;
}

.quick-link-description {
	font-size: 12px;
	color: var(--muted);
	line-height: 1.5;
}

.footer {
	padding: 24px 12px;
	color: var(--muted);
	text-align: center;
	font-size: 13px;
}

@media ( max-width : 900px) {
	.profile-layout {
		grid-template-columns: 1fr;
	}
	.sidebar {
		padding: 20px;
	}
	.side-menu {
		display: grid;
		grid-template-columns: repeat(2, minmax(0, 1fr));
	}
}

@media ( max-width : 576px) {
	.page-container {
		margin-top: 24px;
		padding: 0 12px;
	}
	.main-panel {
		padding: 21px 17px;
	}
	.page-heading h1 {
		font-size: 25px;
	}
	.quick-links, .side-menu {
		grid-template-columns: 1fr;
	}
	.topbar-link {
		margin-left: 10px;
		font-size: 13px;
	}
}
</style>
</head>

<body>
	<c:set var="contextPath" value="${pageContext.request.contextPath}" />

	<header class="topbar">
		<div
			class="container d-flex justify-content-between align-items-center">
			<a class="brand" href="${contextPath}/"> <i
				class="fa-solid fa-book-open"></i> Keeper
			</a>

			<nav class="d-flex align-items-center">
				<c:if test="${not empty dashboardPath}">
					<a class="topbar-link" href="${contextPath}${dashboardPath}"> <i
						class="fa-solid fa-house"></i> <span class="d-none d-sm-inline">Trang
							chủ</span>
					</a>
				</c:if>

				<a class="topbar-link" href="${contextPath}/account/change-password">
					<i class="fa-solid fa-shield-halved"></i> <span
					class="d-none d-sm-inline">Bảo mật</span>
				</a>
			</nav>
		</div>
	</header>

	<main class="page-container">
		<div class="page-heading">
			<h1>Hồ sơ cá nhân</h1>
			<p>Quản lý thông tin cá nhân và cài đặt tài khoản Keeper của bạn.</p>
		</div>

		<c:if test="${not empty successMessage}">
			<div class="alert alert-success alert-dismissible fade show"
				role="alert">
				<i class="fa-solid fa-circle-check me-2"></i>
				<c:out value="${successMessage}" />
				<button type="button" class="btn-close" data-bs-dismiss="alert"
					aria-label="Đóng"></button>
			</div>
		</c:if>

		<c:if test="${not empty errorMessage}">
			<div class="alert alert-danger alert-dismissible fade show"
				role="alert">
				<i class="fa-solid fa-circle-exclamation me-2"></i>
				<c:out value="${errorMessage}" />
				<button type="button" class="btn-close" data-bs-dismiss="alert"
					aria-label="Đóng"></button>
			</div>
		</c:if>

		<div class="profile-layout">
			<aside class="panel sidebar">
				<div class="avatar-wrap">
					<c:choose>
						<c:when test="${not empty profileUser.avatar}">
							<img class="avatar" src="${contextPath}/${profileUser.avatar}"
								alt="Ảnh đại diện">
						</c:when>
						<c:otherwise>
							<div class="avatar-placeholder">
								<i class="fa-solid fa-user"></i>
							</div>
						</c:otherwise>
					</c:choose>

					<div class="profile-name">
						<c:out value="${profileUser.fullName}" />
					</div>

					<div class="profile-email">
						<c:out value="${profileUser.email}" />
					</div>

					<c:choose>
						<c:when test="${profilePath.startsWith('/buyer/')}">
							<span class="role-pill">Người mua</span>
						</c:when>
						<c:when test="${profilePath.startsWith('/seller/')}">
							<span class="role-pill">Người bán</span>
						</c:when>
						<c:when test="${profilePath.startsWith('/manager/')}">
							<span class="role-pill">Quản lý</span>
						</c:when>
						<c:when test="${profilePath.startsWith('/admin/')}">
							<span class="role-pill">Quản trị viên</span>
						</c:when>
						<c:when test="${profilePath.startsWith('/shipper/')}">
							<span class="role-pill">Nhân viên giao hàng</span>
						</c:when>
						<c:otherwise>
							<span class="role-pill">Thành viên Keeper</span>
						</c:otherwise>
					</c:choose>
				</div>

				<nav class="side-menu">
					<a class="active" href="${contextPath}${profilePath}"> <i
						class="fa-regular fa-user"></i> Hồ sơ cá nhân
					</a> <a href="${contextPath}/account/change-email"> <i
						class="fa-regular fa-envelope"></i> Thay đổi email
					</a> <a href="${contextPath}/account/change-password"> <i
						class="fa-solid fa-lock"></i> Đổi mật khẩu
					</a>

					<c:choose>
						<c:when test="${profilePath.startsWith('/buyer/')}">
							<a href="${contextPath}/buyer/orders"> <i
								class="fa-solid fa-box"></i> Đơn hàng của tôi
							</a>
						</c:when>

						<c:when test="${profilePath.startsWith('/seller/')}">
							<a href="${contextPath}/seller/orders"> <i
								class="fa-solid fa-boxes-stacked"></i> Quản lý đơn hàng
							</a>
						</c:when>

						<c:when test="${profilePath.startsWith('/manager/')}">
							<a href="${contextPath}/manager"> <i
								class="fa-solid fa-chart-line"></i> Trang quản lý
							</a>
						</c:when>

						<c:when test="${profilePath.startsWith('/admin/')}">
							<a href="${contextPath}/admin"> <i
								class="fa-solid fa-screwdriver-wrench"></i> Quản trị hệ thống
							</a>
						</c:when>

						<c:when test="${profilePath.startsWith('/shipper/')}">
							<a href="${contextPath}/shipper/orders"> <i
								class="fa-solid fa-truck"></i> Đơn hàng được giao
							</a>
						</c:when>
					</c:choose>
				</nav>
			</aside>

			<section class="panel main-panel">
				<h2 class="section-title">Thông tin cá nhân</h2>
				<p class="section-description">Cập nhật thông tin liên hệ và ảnh
					đại diện của bạn.</p>

				<form action="${contextPath}${profilePath}" method="post"
					enctype="multipart/form-data">

					<c:if test="${_csrf != null}">
						<input type="hidden" name="${_csrf.parameterName}"
							value="${_csrf.token}">
					</c:if>

					<div class="row g-4">
						<div class="col-12">
							<label for="fullName" class="form-label"> Họ và tên <span
								class="text-danger">*</span>
							</label> <input type="text" id="fullName" name="fullName"
								class="form-control"
								value="<c:out value='${profileUser.fullName}' />"
								maxlength="100" autocomplete="name" required>
						</div>

						<div class="col-12">
							<label for="email" class="form-label">Email tài khoản</label> <input
								type="email" id="email" class="form-control"
								value="<c:out value='${profileUser.email}' />" readonly>
							<div class="form-text">Để thay đổi email, hãy chọn “Thay
								đổi email” và xác minh bằng OTP.</div>
						</div>

						<div class="col-12">
							<label for="phone" class="form-label">Số điện thoại</label> <input
								type="tel" id="phone" name="phone" class="form-control"
								value="<c:out value='${profileUser.phone}' />" maxlength="20"
								autocomplete="tel" placeholder="Nhập số điện thoại">
						</div>

						<div class="col-12">
							<label for="avatarFile" class="form-label">Ảnh đại diện</label> <input
								type="file" id="avatarFile" name="avatarFile"
								class="form-control" accept="image/png,image/jpeg">
							<div class="form-text">Chỉ chấp nhận ảnh PNG hoặc JPEG,
								dung lượng tối đa 5 MB. Để trống nếu không muốn thay đổi ảnh.</div>
						</div>
					</div>

					<div class="d-flex flex-wrap gap-3 mt-4">
						<button type="submit" class="btn btn-primary-custom">
							<i class="fa-solid fa-floppy-disk me-2"></i> Lưu thay đổi
						</button>

						<a href="${contextPath}${profilePath}"
							class="btn btn-light-custom"> Hủy </a>
					</div>
				</form>

				<hr class="my-4" style="border-color: var(--border);">

				<h2 class="section-title">Cài đặt tài khoản</h2>
				<p class="section-description">Quản lý email đăng nhập và bảo vệ
					tài khoản của bạn.</p>

				<div class="quick-links">
					<a class="quick-link" href="${contextPath}/account/change-email">
						<div class="quick-link-icon">
							<i class="fa-solid fa-envelope"></i>
						</div>
						<div>
							<div class="quick-link-title">Thay đổi email</div>
							<div class="quick-link-description">Xác minh email mới bằng
								mã OTP.</div>
						</div>
					</a> <a class="quick-link"
						href="${contextPath}/account/change-password">
						<div class="quick-link-icon">
							<i class="fa-solid fa-shield-halved"></i>
						</div>
						<div>
							<div class="quick-link-title">Đổi mật khẩu</div>
							<div class="quick-link-description">Cập nhật mật khẩu để
								bảo vệ tài khoản.</div>
						</div>
					</a>
				</div>
			</section>
		</div>
	</main>

	<footer class="footer"> © Keeper — Nền tảng mua bán sách. </footer>

	<script
		src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
