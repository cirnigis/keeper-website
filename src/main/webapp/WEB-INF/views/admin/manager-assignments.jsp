<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Phân công Manager | Keeper</title>
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
	max-width: 1150px;
	margin: 32px auto;
	padding: 0 18px
}

.panel {
	background: #fff;
	border: 1px solid #e2e8f0;
	border-radius: 14px;
	padding: 20px;
	margin: 18px 0;
	overflow: auto
}

.row {
	display: flex;
	gap: 12px;
	flex-wrap: wrap;
	align-items: end
}

label {
	display: block;
	font-weight: 600;
	margin-bottom: 6px
}

select, input {
	padding: 10px;
	border: 1px solid #cbd5e1;
	border-radius: 8px;
	min-width: 220px
}

.btn {
	background: #1463f3;
	color: #fff;
	border: 0;
	padding: 10px 14px;
	border-radius: 8px;
	cursor: pointer
}

table {
	border-collapse: collapse;
	width: 100%;
	min-width: 650px
}

th, td {
	text-align: left;
	padding: 12px;
	border-bottom: 1px solid #e2e8f0
}

.muted {
	color: #64748b
}

.ok {
	color: #166534
}

.err {
	color: #b91c1c
}
</style>
</head>
<body>
	<main class="wrap">
		<p>
			<a href="${pageContext.request.contextPath}/admin/dashboard">←
				Dashboard Admin</a>
		</p>
		<h1>Phân công Manager phụ trách cửa hàng</h1>
		<p class="muted">Chỉ tài khoản Manager đang hoạt động và cửa hàng
			đã duyệt mới xuất hiện trong danh sách.</p>
		<c:if test="${not empty successMessage}">
			<p class="ok">
				<c:out value="${successMessage}" />
			</p>
		</c:if>
		<c:if test="${not empty errorMessage}">
			<p class="err">
				<c:out value="${errorMessage}" />
			</p>
		</c:if>
		<section class="panel">
			<h2>Tạo phân công</h2>
			<form method="post"
				action="${pageContext.request.contextPath}/admin/manager-assignments/assign">
				<c:if test="${_csrf != null}">
					<input type="hidden" name="${_csrf.parameterName}"
						value="${_csrf.token}">
				</c:if>
				<div class="row">
					<div>
						<label for="manager">Manager</label><select id="manager"
							name="managerUserId" required><option value="">--
								Chọn Manager --</option>
							<c:forEach items="${managers}" var="m">
								<option value="${m.userId}"><c:out
										value="${m.fullName}" /> —
									<c:out value="${m.email}" /></option>
							</c:forEach></select>
					</div>
					<div>
						<label for="shop">Cửa hàng</label><select id="shop" name="shopId"
							required><option value="">-- Chọn cửa hàng --</option>
							<c:forEach items="${shops}" var="s">
								<option value="${s.shopId}"><c:out
										value="${s.shopName}" /> (#
									<c:out value="${s.shopId}" />)
								</option>
							</c:forEach></select>
					</div>
					<div>
						<label for="note">Ghi chú (không bắt buộc)</label><input id="note"
							name="note" maxlength="500" placeholder="Phạm vi hoặc lưu ý">
					</div>
					<button class="btn" type="submit">Phân công</button>
				</div>
			</form>
		</section>
		<section class="panel">
			<h2>Phân công đang hoạt động</h2>
			<table>
				<thead>
					<tr>
						<th>Cửa hàng</th>
						<th>Manager</th>
						<th>Email</th>
						<th>Ngày phân công</th>
						<th>Ghi chú</th>
						<th>Thao tác</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${assignments}" var="a">
						<tr>
							<td><c:out value="${a.shopName}" /> (#<c:out
									value="${a.shopId}" />)</td>
							<td><c:out value="${a.managerName}" /></td>
							<td><c:out value="${a.managerEmail}" /></td>
							<td><c:out value="${a.assignedAt}" /></td>
							<td><c:out value="${a.note}" /></td>
							<td><form method="post"
									action="${pageContext.request.contextPath}/admin/manager-assignments/end"
									onsubmit="return confirm('Kết thúc phân công này?')">
									<c:if test="${_csrf != null}">
										<input type="hidden" name="${_csrf.parameterName}"
											value="${_csrf.token}">
									</c:if>
									<input type="hidden" name="shopId" value="${a.shopId}"><input
										type="hidden" name="managerUserId" value="${a.managerUserId}">
									<button class="btn" type="submit">Kết thúc</button>
								</form></td>
						</tr>
					</c:forEach>
					<c:if test="${empty assignments}">
						<tr>
							<td colspan="6">Chưa có phân công đang hoạt động.</td>
						</tr>
					</c:if>
				</tbody>
			</table>
		</section>
	</main>
</body>
</html>
