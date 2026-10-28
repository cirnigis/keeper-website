<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Khiếu nại - Keeper</title>
<link rel="stylesheet" href="/css/style.css">
</head>
<body>
	<main class="container"
		style="max-width: 1000px; margin: 32px auto; padding: 0 16px">
		<h1>Khiếu nại và hỗ trợ</h1>
		<c:if test="${not empty successMessage}">
			<p role="status" class="alert alert-success">
				<c:out value="${successMessage}" />
			</p>
		</c:if>
		<c:if test="${not empty errorMessage}">
			<p role="alert" class="alert alert-danger">
				<c:out value="${errorMessage}" />
			</p>
		</c:if>
		<section class="card" style="padding: 20px; margin: 20px 0">
			<h2>Tạo khiếu nại</h2>
			<form method="post" action="/buyer/support/create">
				<input type="hidden" name="${_csrf.parameterName}"
					value="${_csrf.token}" /><label>Mã đơn hàng (không bắt
					buộc)<input type="number" min="1" name="orderId">
				</label><label>Tiêu đề<input required minlength="4" maxlength="200"
					name="subject"></label><label>Mô tả<textarea required
						minlength="10" maxlength="4000" name="description" rows="5"></textarea></label>
				<button type="submit">Gửi khiếu nại</button>
			</form>
		</section>
		<section>
			<h2>Khiếu nại của tôi</h2>
			<c:choose>
				<c:when test="${empty tickets}">
					<p>Bạn chưa có khiếu nại nào.</p>
				</c:when>
				<c:otherwise>
					<table>
						<thead>
							<tr>
								<th>Mã</th>
								<th>Tiêu đề</th>
								<th>Đơn hàng</th>
								<th>Trạng thái</th>
								<th>Ngày tạo</th>
							</tr>
						</thead>
						<tbody>
							<c:forEach items="${tickets}" var="t">
								<tr>
									<td><c:out value="${t.ticketCode}" /></td>
									<td><c:out value="${t.subject}" /></td>
									<td><c:out value="${t.orderId}" /></td>
									<td><c:out value="${t.ticketStatus}" /></td>
									<td><c:out value="${t.createdAt}" /></td>
								</tr>
								<tr>
									<td colspan="5"><strong>Lịch sử trao đổi</strong>
									<c:forEach items="${t.messages}" var="m">
											<p>
												<b><c:out value="${m.senderName}" /></b> (
												<c:out value="${m.createdAt}" />
												):
												<c:out value="${m.messageText}" />
											</p>
										</c:forEach>
										<form method="post" action="/buyer/support/reply">
											<input type="hidden" name="${_csrf.parameterName}"
												value="${_csrf.token}" /><input type="hidden"
												name="ticketId" value="${t.ticketId}" /><label>Phản
												hồi<textarea name="message" required maxlength="4000"
													rows="2"></textarea>
											</label>
											<button type="submit">Gửi phản hồi</button>
										</form></td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</c:otherwise>
			</c:choose>
		</section>
		<p>
			<a href="/buyer/orders">Quay lại đơn hàng</a>
		</p>
	</main>
</body>
</html>
