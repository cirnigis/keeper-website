
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Thay đổi email | Keeper</title>
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/style.css">

<style>
.security-page .header-actions {
	display: flex;
	align-items: center;
	justify-content: flex-end;
	gap: 12px;
}

.security-page .header-actions>a {
	display: none;
}

.security-page .header-actions form {
	margin: 0;
}

.security-page .security-heading {
	align-items: flex-end;
	gap: 24px;
}

.security-page .security-back {
	display: inline-flex;
	align-items: center;
	justify-content: center;
	flex-shrink: 0;
	padding: 14px 22px;
	border: 1px solid #2163f6;
	border-radius: 11px;
	background: #2163f6;
	color: #fff;
	font-size: 16px;
	font-weight: 700;
	line-height: 1.4;
	text-decoration: none;
	transition: background 0.2s ease, transform 0.2s ease;
}

.security-page .security-back:hover {
	background: #174fce;
	border-color: #174fce;
	color: #fff;
	transform: translateY(-1px);
}

@media ( max-width : 640px) {
	.security-page .security-heading {
		align-items: flex-start;
		flex-direction: column;
	}
	.security-page .security-back {
		width: 100%;
	}
}
</style>
</head>

<body class="security-page">

	<header class="site-header">
		<div class="header-inner">
			<a class="keeper-brand" href="${pageContext.request.contextPath}/">
				<span class="keeper-brand-icon">K</span> <span
				class="keeper-brand-text">eeper</span>
			</a>

			<nav class="main-nav" aria-label="Điều hướng chính">
				<a href="${pageContext.request.contextPath}/">Trang chủ</a> <a
					href="${pageContext.request.contextPath}/books">Sách</a> <a
					href="${pageContext.request.contextPath}/shops">Nhà sách</a>
			</nav>

			<div class="header-actions">
				<form action="${pageContext.request.contextPath}/auth/logout"
					method="post">
					<c:if test="${_csrf != null}">
						<input type="hidden" name="${_csrf.parameterName}"
							value="${_csrf.token}">
					</c:if>
					<button type="submit" class="logout-button">Đăng xuất</button>
				</form>
			</div>
		</div>
	</header>

	<main class="security-container">

		<section class="security-heading">
			<div>
				<span class="security-eyebrow">TÀI KHOẢN KEEPER</span>
				<h1>Thay đổi email</h1>
				<p>Cập nhật địa chỉ email đăng nhập và xác minh bằng mã OTP.</p>
			</div>

			<a class="security-back"
				href="${pageContext.request.contextPath}${dashboardPath}"> Quay
				lại Dashboard </a>
		</section>

		<c:if test="${not empty successMessage}">
			<div class="security-alert security-alert-success" role="status">
				<c:out value="${successMessage}" />
			</div>
		</c:if>

		<c:if test="${not empty errorMessage}">
			<div class="security-alert security-alert-error" role="alert">
				<c:out value="${errorMessage}" />
			</div>
		</c:if>

		<section class="security-card" id="email">

			<div class="security-card-heading">
				<div class="security-icon" aria-hidden="true">✉</div>
				<div>
					<h2>Địa chỉ email</h2>
					<p>Email được dùng để đăng nhập và nhận thông báo tài khoản.</p>
				</div>
			</div>

			<div class="current-email">
				<span>Email hiện tại</span> <strong><c:out
						value="${currentEmail}" /></strong>
			</div>

			<form
				action="${pageContext.request.contextPath}/account/change-email/email/send-otp"
				method="post">

				<c:if test="${_csrf != null}">
					<input type="hidden" name="${_csrf.parameterName}"
						value="${_csrf.token}">
				</c:if>

				<label for="newEmail">Email mới</label> <input type="email"
					id="newEmail" name="newEmail"
					value="${not empty newEmail ? newEmail : sessionScope.pendingEmailChange}"
					maxlength="254" autocomplete="email" required>

				<button type="submit">Gửi mã xác minh</button>
			</form>

			<c:if test="${not empty sessionScope.pendingEmailChange}">
				<div class="security-divider"></div>

				<h3>Xác minh email mới</h3>
				<p class="security-description">Nhập mã OTP gồm 6 chữ số đã được
					gửi đến email mới của bạn.</p>

				<form
					action="${pageContext.request.contextPath}/account/change-email/email/verify"
					method="post">

					<c:if test="${_csrf != null}">
						<input type="hidden" name="${_csrf.parameterName}"
							value="${_csrf.token}">
					</c:if>

					<label for="otp">Mã OTP</label> <input type="text" id="otp"
						name="otp" inputmode="numeric" pattern="[0-9]{6}" minlength="6"
						maxlength="6" autocomplete="one-time-code" required>

					<button type="submit">Xác minh và đổi email</button>
				</form>
			</c:if>

		</section>

		<p class="security-footnote">Không chia sẻ mã OTP với bất kỳ ai.</p>

	</main>

</body>
</html>
