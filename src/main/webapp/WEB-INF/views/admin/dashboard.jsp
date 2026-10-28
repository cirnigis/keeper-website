<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Admin Dashboard | Keeper</title>
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/style.css" />
<style>
.kp-admin {
	min-height: 100vh;
	background: #f4f6fb;
	color: #172554;
	font-family: Arial, Helvetica, sans-serif
}

.kp-head {
	background: #fff;
	border-bottom: 1px solid #e2e8f0
}

.kp-head-inner {
	max-width: 1400px;
	margin: auto;
	padding: 20px 24px;
	display: flex;
	align-items: center;
	justify-content: space-between;
	gap: 20px
}

.kp-brand {
	display: flex;
	align-items: center;
	gap: 8px;
	text-decoration: none;
	font: 700 32px 'Trebuchet MS', Arial;
	color: #14213d
}

.kp-mark {
	display: inline-grid;
	place-items: center;
	width: 34px;
	height: 34px;
	border-radius: 9px;
	background: var(--keeper-blue, #1463f3);
	color: #fff
}

.kp-nav {
	display: flex;
	gap: 20px;
	align-items: center
}

.kp-nav a {
	color: #14213d;
	text-decoration: none;
	font-weight: 600
}

.kp-main {
	max-width: 1400px;
	margin: auto;
	padding: 32px 24px
}

.kp-muted {
	color: #64748b
}

.kp-grid {
	display: grid;
	grid-template-columns: repeat(4, minmax(0, 1fr));
	gap: 16px;
	margin: 24px 0
}

.kp-card, .kp-panel {
	background: #fff;
	border: 1px solid #e2e8f0;
	border-radius: 16px;
	padding: 22px;
	box-shadow: 0 2px 8px #0f172a06
}

.kp-label {
	color: #64748b;
	font-size: 14px;
	margin: 0 0 12px
}

.kp-value {
	font-size: 30px;
	font-weight: 750;
	margin: 0;
	color: #172554
}

.kp-panel {
	margin-top: 20px;
	overflow-x: auto
}

.kp-table {
	width: 100%;
	border-collapse: collapse;
	min-width: 620px
}

.kp-table th, .kp-table td {
	text-align: left;
	padding: 13px 12px;
	border-bottom: 1px solid #e2e8f0;
	font-size: 14px
}

.kp-table th {
	color: #64748b;
	background: #f8fafc
}

.kp-pill {
	display: inline-block;
	border-radius: 999px;
	padding: 5px 10px;
	background: #eff6ff;
	color: #1d4ed8;
	font-size: 12px;
	font-weight: 700
}

.kp-btn {
	border: 0;
	border-radius: 9px;
	padding: 10px 14px;
	background: #1463f3;
	color: #fff;
	font-weight: 700;
	cursor: pointer
}

@media ( max-width :850px) {
	.kp-grid {
		grid-template-columns: repeat(2, minmax(0, 1fr))
	}
	.kp-head-inner {
		align-items: flex-start;
		flex-direction: column
	}
}

@media ( max-width :520px) {
	.kp-grid {
		grid-template-columns: 1fr
	}
	.kp-main {
		padding: 24px 16px
	}
}
</style>
</head>
<body class="kp-admin">
	<header class="kp-head">
		<div class="kp-head-inner">
			<a class="kp-brand" href="${pageContext.request.contextPath}/"><span
				class="kp-mark">K</span><span>eeper</span></a>
			<nav class="kp-nav">
				<a href="${pageContext.request.contextPath}/admin/dashboard"
					aria-current="page">Tổng quan</a><a
					href="${pageContext.request.contextPath}/books">Sách</a>
				<form action="${pageContext.request.contextPath}/auth/logout"
					method="post">
					<c:if test="${_csrf != null}">
						<input type="hidden" name="${_csrf.parameterName}"
							value="${_csrf.token}" />
					</c:if>
					<button class="kp-btn" type="submit">Đăng xuất</button>
				</form>
				<a href="${pageContext.request.contextPath}/admin/managers">Quản
					lý Manager</a><a
					href="${pageContext.request.contextPath}/admin/manager-assignments">Phân
					công Manager</a><a
					href="${pageContext.request.contextPath}/admin/shops/manage">Quản
					lý cửa hàng</a>
			</nav>
		</div>
	</header>
	<main class="kp-main">
		<p class="kp-muted">KEEPER / QUẢN TRỊ HỆ THỐNG</p>
		<h1>Dashboard Admin</h1>
		<p class="kp-muted">Tổng quan tài khoản, cửa hàng và hoạt động
			marketplace.</p>
		<section class="kp-grid">
			<article class="kp-card">
				<p class="kp-label">Tổng tài khoản</p>
				<p class="kp-value">
					<c:out value="${summary.totalUsers}" default="0" />
				</p>
			</article>
			<article class="kp-card">
				<p class="kp-label">Manager</p>
				<p class="kp-value">
					<c:out value="${summary.totalManagers}" default="0" />
				</p>
			</article>
			<article class="kp-card">
				<p class="kp-label">Seller</p>
				<p class="kp-value">
					<c:out value="${summary.totalSellers}" default="0" />
				</p>
			</article>
			<article class="kp-card">
				<p class="kp-label">Cửa hàng chờ duyệt</p>
				<p class="kp-value">
					<c:out value="${summary.pendingShops}" default="0" />
				</p>
			</article>
			<article class="kp-card">
				<p class="kp-label">Tổng cửa hàng</p>
				<p class="kp-value">
					<c:out value="${summary.totalShops}" default="0" />
				</p>
			</article>
			<article class="kp-card">
				<p class="kp-label">Listing đang hoạt động</p>
				<p class="kp-value">
					<c:out value="${summary.activeListings}" default="0" />
				</p>
			</article>
			<article class="kp-card">
				<p class="kp-label">Tổng đơn hàng</p>
				<p class="kp-value">
					<c:out value="${summary.totalOrders}" default="0" />
				</p>
			</article>
		</section>
		<section class="kp-panel">
			<h2>Danh sách Manager</h2>
			<p class="kp-muted">Theo dõi trạng thái tài khoản và số cửa hàng
				đang được phân công.</p>
			<table class="kp-table">
				<thead>
					<tr>
						<th>Họ tên</th>
						<th>Email</th>
						<th>Trạng thái</th>
						<th>Cửa hàng được giao</th>
					</tr>
				</thead>
				<tbody>
					<c:choose>
						<c:when test="${not empty managers}">
							<c:forEach items="${managers}" var="manager">
								<tr>
									<td><c:out value="${manager.fullName}" /></td>
									<td><c:out value="${manager.email}" /></td>
									<td><span class="kp-pill"><c:out
												value="${manager.accountStatus}" /></span></td>
									<td><c:out value="${manager.assignedShops}" default="0" /></td>
								</tr>
							</c:forEach>
						</c:when>
						<c:otherwise>
							<tr>
								<td colspan="4">Chưa có tài khoản Manager.</td>
							</tr>
						</c:otherwise>
					</c:choose>
				</tbody>
			</table>
		</section>
		<section class="kp-panel">
			<h2>Hỗ trợ và phân cấp</h2>
			<p>
				<a href="${pageContext.request.contextPath}/admin/support">Xem
					khiếu nại chuyển cấp</a><br>
				<a href="${pageContext.request.contextPath}/admin/shops/manage">Quản
					lý cửa hàng (tạm khóa / mở lại / đóng vĩnh viễn)</a>
			</p>
		</section>
	</main>
</body>
</html>
