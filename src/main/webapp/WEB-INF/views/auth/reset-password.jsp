<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Đặt lại mật khẩu | Keeper</title>

<link
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
	rel="stylesheet">
<link href="${pageContext.request.contextPath}/css/style.css"
	rel="stylesheet">
</head>
<body class="auth-page">
	<div class="container py-5">
		<div class="card shadow-sm mx-auto" style="max-width: 480px;">
			<div class="card-body p-4 p-md-5">

				<a class="keeper-brand mb-4"
					href="${pageContext.request.contextPath}/"
					aria-label="Keeper - Trang chủ"> <span
					class="keeper-brand-icon">K</span> <span class="keeper-brand-text">eeper</span>
				</a>

				<h1 class="h3 mb-2">Đặt lại mật khẩu</h1>
				<p class="text-muted mb-4">Nhập mã xác minh gồm 6 chữ số đã được
					gửi đến email của bạn và tạo mật khẩu mới.</p>

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

				<form
					action="${pageContext.request.contextPath}/auth/reset-password"
					method="post">

					<input type="hidden" name="${_csrf.parameterName}"
						value="${_csrf.token}">

					<div class="mb-3">
						<label for="email" class="form-label">Địa chỉ email</label> <input
							type="email" id="email" name="email" class="form-control"
							maxlength="254" value="<c:out value='${resetEmail}' />"
							autocomplete="email" required>
					</div>

					<div class="mb-3">
						<label for="otp" class="form-label">Mã xác minh</label> <input
							type="text" id="otp" name="otp" class="form-control"
							inputmode="numeric" pattern="[0-9]{6}" minlength="6"
							maxlength="6" autocomplete="one-time-code" required>
						<div class="form-text">Mã xác minh sẽ hết hạn sau 5 phút.</div>
					</div>

					<div class="mb-3">
						<label for="newPassword" class="form-label"> Mật khẩu mới
						</label> <input type="password" id="newPassword" name="newPassword"
							class="form-control" minlength="8" autocomplete="new-password"
							required>
					</div>

					<div class="mb-4">
						<label for="confirmPassword" class="form-label"> Xác nhận
							mật khẩu mới </label> <input type="password" id="confirmPassword"
							name="confirmPassword" class="form-control" minlength="8"
							autocomplete="new-password" required>
					</div>

					<button type="submit" class="btn btn-primary w-100">Đặt
						lại mật khẩu</button>
				</form>

				<div class="text-center mt-4">
					<a href="${pageContext.request.contextPath}/auth/forgot-password">
						Yêu cầu mã mới </a> <span class="mx-2">|</span> <a
						href="${pageContext.request.contextPath}/auth/login"> Đăng
						nhập </a>
				</div>
			</div>
		</div>
	</div>
</body>
</html>