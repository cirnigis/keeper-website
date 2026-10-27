<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>

<!DOCTYPE html>
<html lang="vi">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">

<title>Quên mật khẩu | Keeper</title>

<link
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
	rel="stylesheet">

<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/style.css">
</head>

<body class="auth-page">

	<div class="container py-5">
		<div class="card shadow-sm mx-auto" style="max-width: 480px;">
			<div class="card-body p-4 p-md-5">

				<!-- Logo Keeper -->
				<div class="text-center mb-4">
					<a class="keeper-brand" href="${pageContext.request.contextPath}/"
						aria-label="Keeper - Trang chủ"> <span
						class="keeper-brand-icon">K</span> <span class="keeper-brand-text">eeper</span>
					</a>
				</div>

				<h1 class="h3 mb-2">Quên mật khẩu?</h1>

				<p class="text-muted mb-4">Nhập địa chỉ email đã đăng ký để nhận
					mã đặt lại mật khẩu.</p>

				<!-- Thông báo lỗi -->
				<c:if test="${not empty errorMessage}">
					<div class="alert alert-danger" role="alert">
						<c:out value="${errorMessage}" />
					</div>
				</c:if>

				<!-- Biểu mẫu yêu cầu đặt lại mật khẩu -->
				<form
					action="${pageContext.request.contextPath}/auth/forgot-password"
					method="post">

					<input type="hidden" name="${_csrf.parameterName}"
						value="${_csrf.token}">

					<div class="mb-3">
						<label for="email" class="form-label"> Địa chỉ email </label> <input
							type="email" id="email" name="email" class="form-control"
							maxlength="254" autocomplete="email" placeholder="vidu@email.com"
							required>
					</div>

					<button type="submit" class="btn btn-primary w-100">Gửi mã
						đặt lại mật khẩu</button>
				</form>

				<div class="text-center mt-4">
					<a href="${pageContext.request.contextPath}/auth/login"> Quay
						lại đăng nhập </a>
				</div>

			</div>
		</div>
	</div>

</body>
</html>

