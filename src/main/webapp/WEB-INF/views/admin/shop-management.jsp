<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width,initial-scale=1">
<title>Quản lý cửa hàng | Keeper</title>
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/style.css">
<style>
body {
	font-family: Arial, sans-serif;
	background: #f4f6fb;
	color: #172554;
	margin: 0
}

.wrap {
	max-width: 1200px;
	margin: 32px auto;
	padding: 0 18px
}

.panel {
	background: white;
	border: 1px solid #e2e8f0;
	border-radius: 14px;
	padding: 20px;
	margin: 16px 0;
	overflow: auto
}

table {
	width: 100%;
	border-collapse: collapse;
	min-width: 850px
}

th, td {
	text-align: left;
	padding: 12px;
	border-bottom: 1px solid #e2e8f0;
	vertical-align: top
}

th {
	background: #f8fafc
}

.muted {
	color: #64748b
}

.field {
	padding: 8px;
	border: 1px solid #cbd5e1;
	border-radius: 7px;
	max-width: 100%;
	box-sizing: border-box
}

.btn {
	padding: 9px 12px;
	border: 0;
	border-radius: 7px;
	background: #1463f3;
	color: white;
	font-weight: bold;
	cursor: pointer
}

.alert {
	padding: 12px;
	border-radius: 8px;
	background: #eff6ff;
	margin: 12px 0
}

.danger {
	background: #b91c1c
}

.formrow {
	display: grid;
	gap: 7px;
	min-width: 220px
}
</style>
</head>
<body>
	<main class="wrap">
		<p class="muted">
			<a href="${pageContext.request.contextPath}/admin/dashboard">←
				Dashboard Admin</a>
		</p>
		<h1>Quản lý cửa hàng</h1>
		<p class="muted">Tạm khóa, mở lại hoặc đóng vĩnh viễn cửa hàng.
			Mỗi thao tác cần có lý do và được ghi vào lịch sử duyệt.</p>
		<c:if test="${not empty successMessage}">
			<div class="alert">
				<c:out value="${successMessage}" />
			</div>
		</c:if>
		<c:if test="${not empty errorMessage}">
			<div class="alert">
				<c:out value="${errorMessage}" />
			</div>
		</c:if>
		<section class="panel">
			<table>
				<thead>
					<tr>
						<th>ID</th>
						<th>Cửa hàng / chủ shop</th>
						<th>Trạng thái</th>
						<th>Thao tác quản trị</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${shops}" var="s">
						<tr>
							<td><c:out value="${s.shopId}" /></td>
							<td><strong><c:out value="${s.shopName}" /></strong><br>
							<c:out value="${s.ownerName}" /><br>
							<span class="muted"><c:out value="${s.ownerEmail}" /></span></td>
							<td><c:out value="${s.approvalStatus}" /> · <c:choose>
									<c:when test="${s.isActive}">Đang hoạt động</c:when>
									<c:otherwise>Không hoạt động</c:otherwise>
								</c:choose></td>
							<td><c:choose>
									<c:when test="${s.approvalStatus eq 'CLOSED'}">
										<span class="muted">Đã đóng vĩnh viễn</span>
									</c:when>
									<c:otherwise>
										<form method="post"
											action="${pageContext.request.contextPath}/admin/shops/manage"
											class="formrow">
											<input type="hidden" name="shopId" value="${s.shopId}">
											<c:if test="${_csrf != null}">
												<input type="hidden" name="${_csrf.parameterName}"
													value="${_csrf.token}">
											</c:if>
											<select class="field" name="action" required><option
													value="SUSPEND">Tạm khóa</option>
												<option value="REACTIVATE">Mở lại</option>
												<option value="CLOSE">Đóng vĩnh viễn</option></select>
											<textarea class="field" name="reason" rows="2" minlength="8"
												maxlength="1000" placeholder="Lý do (ít nhất 8 ký tự)"
												required></textarea>
											<button class="btn" type="submit"
												onclick="return confirm('Xác nhận thao tác với cửa hàng này?')">Xác
												nhận</button>
										</form>
									</c:otherwise>
								</c:choose></td>
						</tr>
					</c:forEach>
					<c:if test="${empty shops}">
						<tr>
							<td colspan="4">Chưa có cửa hàng.</td>
						</tr>
					</c:if>
				</tbody>
			</table>
		</section>
	</main>
</body>
</html>
