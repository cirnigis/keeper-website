<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>

<!DOCTYPE html>
<html lang="vi">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">

<title>Đăng nhập | Keeper</title>

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

		<h1>Chào mừng bạn trở lại!</h1>
		<p class="text-muted">Đăng nhập để tiếp tục sử dụng tài khoản của
			bạn.</p>

		<c:if test="${param.roleError == 'seller'}">
			<div class="alert alert-warning" role="alert">Tài khoản chưa có
				vai trò Người bán. Hãy đăng nhập bằng vai trò Người mua để đăng ký
				mở shop.</div>
		</c:if>
		<c:if test="${param.roleError == 'buyer'}">
			<div class="alert alert-warning" role="alert">Tài khoản chưa
				được cấp vai trò Người mua. Vui lòng chọn vai trò phù hợp với tài
				khoản.</div>
		</c:if>

		<!-- Thông báo đăng nhập thất bại -->
		<c:if test="${param.error != null}">
			<div class="alert alert-danger" role="alert">Email hoặc mật
				khẩu không chính xác, hoặc tài khoản của bạn chưa được kích hoạt.</div>
		</c:if>

		<!-- Thông báo đăng xuất thành công -->
		<c:if test="${param.logout != null}">
			<div class="alert alert-success" role="alert">Bạn đã đăng xuất
				thành công.</div>
		</c:if>

		<!-- Thông báo thành công -->
		<c:if test="${not empty message}">
			<div class="alert alert-success" role="alert">
				<c:out value="${message}" />
			</div>
		</c:if>

		<!-- Thông báo lỗi -->
		<c:if test="${not empty error}">
			<div class="alert alert-danger" role="alert">
				<c:out value="${error}" />
			</div>
		</c:if>

		<!-- Biểu mẫu đăng nhập -->
		<form action="${pageContext.request.contextPath}/auth/login"
			method="post">

			<input type="hidden" name="${_csrf.parameterName}"
				value="${_csrf.token}">

			<div class="mb-3">
				<label class="form-label d-block">Bạn muốn tiếp tục với vai
					trò</label>
				<div class="d-flex gap-3 flex-wrap">
					<div class="form-check">
						<input class="form-check-input" type="radio" name="loginRole"
							id="loginRoleBuyer" value="BUYER" checked> <label
							class="form-check-label" for="loginRoleBuyer">Người mua</label>
					</div>
					<div class="form-check">
						<input class="form-check-input" type="radio" name="loginRole"
							id="loginRoleSeller" value="SELLER"> <label
							class="form-check-label" for="loginRoleSeller">Người bán</label>
					</div>
				</div>
			</div>

			<div class="mb-3">
				<label for="email" class="form-label">Địa chỉ email</label> <input
					type="email" class="form-control" id="email" name="email"
					autocomplete="username" placeholder="vidu@email.com" required>
			</div>

			<div class="mb-3">
				<label for="password" class="form-label">Mật khẩu</label> <input
					type="password" class="form-control" id="password" name="password"
					autocomplete="current-password" placeholder="Nhập mật khẩu của bạn"
					required>
			</div>

			<div class="forgot-password-container">
				<a href="${pageContext.request.contextPath}/auth/forgot-password">
					Quên mật khẩu? </a>
			</div>

			<button type="submit" class="btn btn-primary w-100">Đăng
				nhập</button>
		</form>

		<p class="auth-footer">
			Bạn chưa có tài khoản? <a
				href="${pageContext.request.contextPath}/auth/register"> Đăng ký
				ngay </a>
		</p>

		<a class="back-home" href="${pageContext.request.contextPath}/"> <i
			class="bi bi-arrow-left"></i> Quay lại trang chủ
		</a>

	</div>

</body>
</html>

