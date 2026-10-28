<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width,initial-scale=1">
<title>Quản lý Manager | Keeper</title>
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
	margin: auto;
	padding: 28px 18px
}

.panel {
	background: white;
	border: 1px solid #e2e8f0;
	border-radius: 14px;
	padding: 20px;
	overflow-x: auto
}

table {
	width: 100%;
	border-collapse: collapse;
	min-width: 700px
}

th, td {
	text-align: left;
	padding: 12px;
	border-bottom: 1px solid #e2e8f0
}

th {
	background: #f8fafc;
	color: #475569
}

.btn {
	border: 0;
	border-radius: 8px;
	padding: 9px 12px;
	color: white;
	background: #1463f3;
	cursor: pointer
}

.danger {
	background: #b91c1c
}

.msg {
	padding: 12px;
	border-radius: 8px;
	margin: 12px 0;
	background: #dcfce7
}

.err {
	background: #fee2e2
}

.muted {
	color: #64748b;
	font-size: 13px
}
</style>
</head>
<body>
	<main class="wrap">
		<p>
			<a href="${pageContext.request.contextPath}/admin/dashboard">←
				Dashboard Admin</a>
		</p>
		<h1>Quản lý quyền Manager</h1>
		<p class="muted">Chỉ tài khoản ACTIVE có thể được cấp quyền. Thao
			tác chỉ thêm hoặc thu hồi role MANAGER; các role khác được giữ
			nguyên.</p>
		<c:if test="${not empty successMessage}">
			<div class="msg">${successMessage}</div>
		</c:if>
		<c:if test="${not empty errorMessage}">
			<div class="msg err">${errorMessage}</div>
		</c:if>
		<section class="panel">
			<table>
				<thead>
					<tr>
						<th>Họ tên</th>
						<th>Email</th>
						<th>Trạng thái tài khoản</th>
						<th>Quyền Manager</th>
						<th>Thao tác</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${users}" var="u">
						<tr>
							<td><c:out value="${u.fullName}" /></td>
							<td><c:out value="${u.email}" /></td>
							<td><c:out value="${u.accountStatus}" /></td>
							<td><c:choose>
									<c:when test="${u.isManager}">Đang có quyền</c:when>
									<c:otherwise>Chưa có</c:otherwise>
								</c:choose></td>
							<td><form method="post"
									action="${pageContext.request.contextPath}/admin/managers/role">
									<input type="hidden" name="${_csrf.parameterName}"
										value="${_csrf.token}"><input type="hidden"
										name="userId" value="${u.userId}">
									<c:choose>
										<c:when test="${u.isManager}">
											<button class="btn danger" name="action" value="REVOKE"
												onclick="return confirm('Thu hồi quyền Manager? Các phân công shop đang hoạt động cũng sẽ kết thúc.')">Thu
												hồi quyền</button>
										</c:when>
										<c:otherwise>
											<button class="btn" name="action" value="GRANT">Cấp
												quyền Manager</button>
										</c:otherwise>
									</c:choose>
								</form></td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</section>
	</main>
</body>
</html>
