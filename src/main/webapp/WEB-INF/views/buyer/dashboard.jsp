<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Dashboard của tôi | Keeper</title>
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

	<header class="site-header">
		<div class="header-inner">
			<a class="keeper-brand" href="${pageContext.request.contextPath}/"
				aria-label="Keeper - Trang chủ"> <span class="keeper-brand-icon">K</span>
				<span class="keeper-brand-text">eeper</span>
			</a>

			<nav class="main-nav" aria-label="Điều hướng chính">
				<a href="${pageContext.request.contextPath}/">Trang chủ</a> <a
					href="${pageContext.request.contextPath}/books">Sách</a> <a
					href="${pageContext.request.contextPath}/shops">Nhà sách</a>
			</nav>

			<div class="header-actions">
				<a href="${pageContext.request.contextPath}/buyer/dashboard"
					aria-current="page">Tài khoản của tôi</a>

				<form action="${pageContext.request.contextPath}/auth/logout"
					method="post">
					<c:if test="${_csrf != null}">
						<input type="hidden" name="${_csrf.parameterName}"
							value="${_csrf.token}">
					</c:if>
					<button type="submit" class="logout-button">

						<svg viewBox="0 0 24 24" aria-hidden="true">
    <path
								d="M14 7l-5 5 5 5M9 12h12M12 3h7a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2h-7"
								fill="none" stroke="currentColor" stroke-width="2"
								stroke-linecap="round" stroke-linejoin="round" />
</svg>

						<span>Đăng xuất</span>
					</button>
				</form>
			</div>
		</div>
	</header>

	<main class="dashboard-container">
		<section class="dashboard-heading">
			<h1>Dashboard của tôi</h1>
			<p>Quản lý thông tin cá nhân, bảo mật tài khoản và hoạt động mua
				sách của bạn.</p>
		</section>

		<section class="dashboard-grid" aria-label="Các chức năng tài khoản">

			<article class="dashboard-card">
				<div class="dashboard-card-icon" aria-hidden="true">
					<svg viewBox="0 0 24 24">
                    <circle cx="12" cy="8" r="4" />
                    <path d="M4 21v-2a8 8 0 0 1 16 0v2z" />
                </svg>
				</div>
				<h2>Thông tin cá nhân</h2>
				<p>Xem và cập nhật họ tên, số điện thoại và ảnh đại diện của
					bạn.</p>
				<a class="dashboard-card-link"
					href="${pageContext.request.contextPath}/buyer/profile"> <span>Cập
						nhật thông tin</span> <span aria-hidden="true">→</span>
				</a>
			</article>

			<article class="dashboard-card">
				<div class="dashboard-card-icon" aria-hidden="true">
					<svg viewBox="0 0 24 24">
                    <rect x="4" y="10" width="16" height="11" rx="2" />
                    <path d="M8 10V7a4 4 0 0 1 8 0v3" />
                    <circle cx="12" cy="15" r="1" />
                    <path d="M12 16v2" />
                </svg>
				</div>
				<h2>Bảo mật tài khoản</h2>
				<p>Chọn chức năng bạn muốn thực hiện để bảo vệ tài khoản.</p>

				<div class="dashboard-security-options">
					<a class="dashboard-security-option"
						href="${pageContext.request.contextPath}/buyer/security"> <span
						class="dashboard-security-option-icon" aria-hidden="true">
							<svg viewBox="0 0 24 24">
                            <rect x="3" y="5" width="18" height="14"
									rx="2" />
                            <path d="m4 7 8 6 8-6" />
                        </svg>
					</span> <span class="dashboard-security-option-content"> <strong>Thay
								đổi email</strong> <small>Xác minh email mới bằng mã OTP</small>
					</span> <span class="dashboard-security-arrow" aria-hidden="true">→</span>
					</a> <a class="dashboard-security-option"
						href="${pageContext.request.contextPath}/buyer/change-password">
						<span class="dashboard-security-option-icon" aria-hidden="true">
							<svg viewBox="0 0 24 24">
                            <rect x="4" y="10" width="16" height="11"
									rx="2" />
                            <path d="M8 10V7a4 4 0 0 1 8 0v3" />
                        </svg>
					</span> <span class="dashboard-security-option-content"> <strong>Thay
								đổi mật khẩu</strong> <small>Cập nhật mật khẩu đăng nhập</small>
					</span> <span class="dashboard-security-arrow" aria-hidden="true">→</span>
					</a>
				</div>
			</article>

			<article class="dashboard-card">
				<div class="dashboard-card-icon" aria-hidden="true">
					<svg viewBox="0 0 24 24">
                    <path d="M6 3h12v18l-6-4-6 4z" />
                    <path d="M9 8h6M9 11h6" />
                </svg>
				</div>
				<h2>Đơn hàng của tôi</h2>
				<p>Theo dõi trạng thái đơn hàng và xem lại lịch sử mua sách.</p>
				<a class="dashboard-card-link"
					href="${pageContext.request.contextPath}/buyer/orders"> <span>Xem
						đơn hàng</span> <span aria-hidden="true">→</span>
				</a>
			</article>

			<article class="dashboard-card">
				<div class="dashboard-card-icon" aria-hidden="true">
					<svg viewBox="0 0 24 24">
                    <path
							d="M3 3h2l2.2 11.5a2 2 0 0 0 2 1.5h8.6a2 2 0 0 0 2-1.6L21 7H6" />
                    <circle cx="10" cy="20" r="1.5" />
                    <circle cx="18" cy="20" r="1.5" />
                </svg>
				</div>
				<h2>Giỏ hàng</h2>
				<p>Xem lại sách đã chọn trước khi tiến hành đặt hàng.</p>
				<a class="dashboard-card-link"
					href="${pageContext.request.contextPath}/buyer/cart"> <span>Mở
						giỏ hàng</span> <span aria-hidden="true">→</span>
				</a>
			</article>

			<article class="dashboard-card">
				<div class="dashboard-card-icon" aria-hidden="true">
					<svg viewBox="0 0 24 24">
                    <path
							d="M20 10c0 5-8 11-8 11S4 15 4 10a8 8 0 1 1 16 0z" />
                    <circle cx="12" cy="10" r="2.5" />
                </svg>
				</div>
				<h2>Quản lý địa chỉ</h2>
				<p>Thêm, chỉnh sửa hoặc chọn địa chỉ giao hàng mặc định.</p>
				<a class="dashboard-card-link"
					href="${pageContext.request.contextPath}/buyer/addresses"> <span>Quản
						lý địa chỉ</span> <span aria-hidden="true">→</span>
				</a>
			</article>

		</section>
	</main>

	<style>
.logo {
	display: inline-flex;
	align-items: center;
	gap: 8px;
}

.logo svg {
	width: 25px;
	height: 25px;
	flex-shrink: 0;
}

.logout-button {
	display: inline-flex;
	align-items: center;
	justify-content: center;
	gap: 8px;
}

.logout-button svg {
	width: 18px;
	height: 18px;
	flex-shrink: 0;
}

.dashboard-card-icon, .dashboard-security-option-icon {
	display: flex;
	align-items: center;
	justify-content: center;
	flex-shrink: 0;
	color: #2563eb;
	background: #edf4ff;
}

.dashboard-card-icon {
	width: 56px;
	height: 56px;
	border-radius: 16px;
	margin-bottom: 24px;
}

.dashboard-card-icon svg {
	width: 28px;
	height: 28px;
	fill: none;
	stroke: currentColor;
	stroke-width: 1.8;
	stroke-linecap: round;
	stroke-linejoin: round;
}

.dashboard-security-option-icon {
	width: 44px;
	height: 44px;
	border-radius: 12px;
}

.dashboard-security-option-icon svg {
	width: 23px;
	height: 23px;
	fill: none;
	stroke: currentColor;
	stroke-width: 1.8;
	stroke-linecap: round;
	stroke-linejoin: round;
}

.dashboard-security-option-content {
	display: flex;
	flex: 1;
	flex-direction: column;
	gap: 5px;
	min-width: 0;
}

.dashboard-security-option-content small {
	line-height: 1.5;
}

.dashboard-security-arrow {
	margin-left: auto;
	font-size: 20px;
}

.dashboard-card-link {
	display: flex;
	align-items: center;
	justify-content: space-between;
	gap: 12px;
}
</style>

	<div style="max-width: 1100px; margin: 16px auto; padding: 0 16px">
		<a href="/buyer/support">Khiếu nại và hỗ trợ</a>
	</div>
</body>
</html>

