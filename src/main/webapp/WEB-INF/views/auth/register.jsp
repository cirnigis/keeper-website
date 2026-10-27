<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>

<!DOCTYPE html>
<html lang="vi">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">

<title>Đăng ký tài khoản | Keeper</title>

<link
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
	rel="stylesheet">

<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/style.css">
</head>

<body class="auth-page">

	<div class="auth-card">

		<!-- Logo Keeper -->
		<a class="keeper-brand" href="${pageContext.request.contextPath}/"
			aria-label="Keeper - Trang chủ"> <span class="keeper-brand-icon">K</span>
			<span class="keeper-brand-text">eeper</span>
		</a>

		<h1>Tạo tài khoản</h1>

		<p class="text-muted">Tham gia cộng đồng những người yêu sách cùng
			Keeper.</p>

		<!-- Thông báo lỗi -->
		<c:if test="${not empty errorMessage}">
			<div class="alert alert-danger" role="alert">
				<c:out value="${errorMessage}" />
			</div>
		</c:if>

		<!-- Thông báo thành công -->
		<c:if test="${not empty successMessage}">
			<div class="alert alert-success" role="alert">
				<c:out value="${successMessage}" />
			</div>
		</c:if>

		<!-- Biểu mẫu đăng ký -->
		<form action="${pageContext.request.contextPath}/auth/register"
			method="post">

			<input type="hidden" name="${_csrf.parameterName}"
				value="${_csrf.token}">

			<div class="mb-3">
				<label for="fullName" class="form-label"> Họ và tên </label> <input
					type="text" class="form-control" id="fullName" name="fullName"
					maxlength="150"
					value="<c:out value='${registerRequest.fullName}'/>"
					autocomplete="name" placeholder="Nhập họ và tên của bạn" required>
			</div>

			<div class="mb-3">
				<label for="email" class="form-label"> Địa chỉ email </label> <input
					type="email" class="form-control" id="email" name="email"
					maxlength="254" value="<c:out value='${registerRequest.email}'/>"
					autocomplete="email" placeholder="vidu@email.com" required>
			</div>

			<div class="mb-3">
				<label for="password" class="form-label"> Mật khẩu </label> <input
					type="password" class="form-control" id="password" name="password"
					minlength="8" autocomplete="new-password"
					placeholder="Mật khẩu có ít nhất 8 ký tự" required>
			</div>

			<div class="mb-4">
				<label for="confirmPassword" class="form-label"> Xác nhận
					mật khẩu </label> <input type="password" class="form-control"
					id="confirmPassword" name="confirmPassword" minlength="8"
					autocomplete="new-password" placeholder="Nhập lại mật khẩu của bạn"
					required>
			</div>

			<button type="submit" class="btn btn-primary w-100">Đăng ký
			</button>

		</form>

		<!-- Liên kết đăng nhập -->
		<p class="auth-footer">
			Bạn đã có tài khoản? <a
				href="${pageContext.request.contextPath}/auth/login"> Đăng nhập
			</a>
		</p>

		<a class="back-home" href="${pageContext.request.contextPath}/">
			Quay lại trang chủ </a>

	</div>

</body>
</html>

