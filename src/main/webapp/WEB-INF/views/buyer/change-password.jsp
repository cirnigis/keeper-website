<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>

<!DOCTYPE html>
<html lang="vi">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">

<title>Đổi mật khẩu | Keeper</title>

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

				<h1 class="h3 mb-2">Đổi mật khẩu</h1>

				<p class="text-muted mb-4">Xác nhận mật khẩu hiện tại trước khi
					thiết lập mật khẩu mới.</p>

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
					action="${pageContext.request.contextPath}/buyer/change-password"
					method="post">

					<input type="hidden" name="${_csrf.parameterName}"
						value="${_csrf.token}">

					<div class="mb-3">
						<label for="currentPassword" class="form-label"> Mật khẩu
							hiện tại </label> <input type="password" id="currentPassword"
							name="currentPassword" class="form-control"
							autocomplete="current-password" required>
					</div>

					<div class="mb-3">
						<label for="newPassword" class="form-label"> Mật khẩu mới
						</label> <input type="password" id="newPassword" name="newPassword"
							class="form-control" minlength="8" autocomplete="new-password"
							required>
						<div class="form-text">Mật khẩu phải có ít nhất 8 ký tự.</div>
					</div>

					<div class="mb-4">
						<label for="confirmPassword" class="form-label"> Xác nhận
							mật khẩu mới </label> <input type="password" id="confirmPassword"
							name="confirmPassword" class="form-control" minlength="8"
							autocomplete="new-password" required>
					</div>

					<button type="submit" class="btn btn-primary w-100">Cập
						nhật mật khẩu</button>
				</form>

				<div class="text-center mt-4">
					<a href="${pageContext.request.contextPath}/"> Quay về trang
						chủ </a>
				</div>

			</div>
		</div>
	</div>
</body>
</html>