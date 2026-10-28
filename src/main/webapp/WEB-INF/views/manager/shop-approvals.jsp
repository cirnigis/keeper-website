<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Duyệt shop | Keeper</title>
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/style.css">
<style>
.approval-page {
	min-height: 100vh;
	background: #f4f6fb;
	color: #172554;
	font-family: Arial, sans-serif
}

.approval-head {
	background: #fff;
	border-bottom: 1px solid #e2e8f0;
	padding: 18px 5%
}

.approval-head a {
	margin-right: 20px;
	color: #1746a2;
	text-decoration: none;
	font-weight: 600
}

.approval-main {
	max-width: 1200px;
	margin: auto;
	padding: 28px 20px
}

.approval-card {
	background: #fff;
	border: 1px solid #e2e8f0;
	border-radius: 14px;
	padding: 22px;
	margin: 18px 0
}

.approval-grid {
	display: grid;
	grid-template-columns: repeat(2, minmax(0, 1fr));
	gap: 12px
}

.approval-field {
	background: #f8fafc;
	padding: 10px;
	border-radius: 8px;
	overflow-wrap: anywhere
}

.approval-field strong {
	display: block;
	color: #64748b;
	font-size: 12px;
	margin-bottom: 4px
}

.approval-actions {
	display: grid;
	grid-template-columns: 1fr 2fr auto auto;
	gap: 10px;
	align-items: center;
	margin-top: 16px
}

.approval-actions input, .approval-actions select {
	min-width: 0;
	padding: 10px;
	border: 1px solid #cbd5e1;
	border-radius: 8px
}

.approval-btn {
	border: 0;
	border-radius: 8px;
	padding: 10px 14px;
	background: #145be8;
	color: #fff;
	font-weight: 700;
	cursor: pointer
}

.approval-btn.reject {
	background: #b42318
}

.approval-alert {
	padding: 12px 14px;
	border-radius: 8px;
	margin: 12px 0;
	background: #e0f2fe
}

.approval-alert.error {
	background: #fee2e2
}

@media ( max-width :700px) {
	.approval-grid {
		grid-template-columns: 1fr
	}
	.approval-actions {
		grid-template-columns: 1fr
	}
	.approval-head a {
		display: inline-block;
		margin: 5px
	}
}
</style>
</head>
<body class="approval-page">
	<header class="approval-head">
		<a href="${pageContext.request.contextPath}/">Keeper</a><a
			href="${pageContext.request.contextPath}/manager/dashboard">Tổng
			quan</a><a
			href="${pageContext.request.contextPath}/manager/shop-approvals">Duyệt
			shop</a>
		<form style="display: inline"
			action="${pageContext.request.contextPath}/auth/logout" method="post">
			<input type="hidden" name="${_csrf.parameterName}"
				value="${_csrf.token}">
			<button class="approval-btn" type="submit">Đăng xuất</button>
		</form>
	</header>
	<main class="approval-main">
		<p>KEEPER / KHU VỰC QUẢN LÝ</p>
		<h1>Yêu cầu mở shop</h1>
		<p>Manager chỉ xử lý shop được phân công; Admin có thể xử lý tất
			cả yêu cầu đang chờ.</p>
		<c:if test="${not empty successMessage}">
			<div class="approval-alert">
				<c:out value="${successMessage}" />
			</div>
		</c:if>
		<c:if test="${not empty errorMessage}">
			<div class="approval-alert error">
				<c:out value="${errorMessage}" />
			</div>
		</c:if>
		<c:choose>
			<c:when test="${not empty pendingShops}">
				<c:forEach items="${pendingShops}" var="shop">
					<section class="approval-card">
						<h2>
							<c:out value="${shop.shopName}" />
							<small>(#<c:out value="${shop.shopId}" />)
							</small>
						</h2>
						<div class="approval-grid">
							<div class="approval-field">
								<strong>Chủ shop</strong>
								<c:out value="${shop.ownerName}" />
								—
								<c:out value="${shop.ownerEmail}" />
							</div>
							<div class="approval-field">
								<strong>CCCD</strong>
								<c:out value="${shop.cccd}" default="Chưa cập nhật" />
							</div>
							<div class="approval-field">
								<strong>Loại chủ shop</strong>
								<c:out value="${shop.ownerType}" />
							</div>
							<div class="approval-field">
								<strong>Liên hệ</strong>
								<c:out value="${shop.contactPhone}" default="Chưa có SĐT" />
								·
								<c:out value="${shop.contactEmail}" default="Chưa có email" />
							</div>
							<div class="approval-field">
								<strong>Địa chỉ</strong>
								<c:out value="${shop.addressLine}" />
								,
								<c:out value="${shop.district}" />
								,
								<c:out value="${shop.province}" />
							</div>
							<div class="approval-field">
								<strong>Mô tả</strong>
								<c:out value="${shop.description}" default="Không có mô tả" />
							</div>
						</div>
						<form class="approval-actions"
							action="${pageContext.request.contextPath}/manager/shop-approvals/review"
							method="post">
							<input type="hidden" name="${_csrf.parameterName}"
								value="${_csrf.token}"><input type="hidden"
								name="shopId" value="${shop.shopId}"><input name="note"
								maxlength="1000" required
								placeholder="Ghi chú xét duyệt (bắt buộc)"><span
								style="color: #64748b; font-size: 13px">Chọn kết quả:</span>
							<button class="approval-btn" type="submit" name="decision"
								value="APPROVED">Duyệt shop</button>
							<button class="approval-btn reject" type="submit" name="decision"
								value="REJECTED">Từ chối</button>
						</form>
					</section>
				</c:forEach>
			</c:when>
			<c:otherwise>
				<section class="approval-card">Hiện không có yêu cầu mở
					shop đang chờ trong phạm vi được phân công.</section>
			</c:otherwise>
		</c:choose>
	</main>
</body>
</html>
