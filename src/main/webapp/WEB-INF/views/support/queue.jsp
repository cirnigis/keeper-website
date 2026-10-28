<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Quản lý khiếu nại - Keeper</title>
<link rel="stylesheet" href="/css/style.css">
</head>
<body>
	<main class="container"
		style="max-width: 1200px; margin: 32px auto; padding: 0 16px">
		<h1>
			<c:choose>
				<c:when test="${isAdmin}">Khiếu nại chuyển cấp</c:when>
				<c:otherwise>Hàng đợi khiếu nại</c:otherwise>
			</c:choose>
		</h1>
		<c:if test="${not empty successMessage}">
			<p role="status">
				<c:out value="${successMessage}" />
			</p>
		</c:if>
		<c:if test="${not empty errorMessage}">
			<p role="alert">
				<c:out value="${errorMessage}" />
			</p>
		</c:if>
		<c:choose>
			<c:when test="${empty tickets}">
				<p>Không có khiếu nại phù hợp.</p>
			</c:when>
			<c:otherwise>
				<c:forEach items="${tickets}" var="t">
					<article class="card" style="padding: 18px; margin: 16px 0">
						<h2>
							<c:out value="${t.ticketCode}" />
							—
							<c:out value="${t.subject}" />
						</h2>
						<p>
							Người gửi:
							<c:out value="${t.creatorName}" />
							| Đơn hàng:
							<c:out value="${t.orderId}" />
							| Shop:
							<c:out value="${t.shopId}" />
						</p>
						<p>
							Trạng thái: <strong><c:out value="${t.ticketStatus}" /></strong>
							| Ưu tiên:
							<c:out value="${t.priority}" />
						</p>
						<p>
							<c:out value="${t.description}" />
						</p>
						<section>
							<h3>Lịch sử trao đổi</h3>
							<c:forEach items="${t.messages}" var="m">
								<p>
									<b><c:out value="${m.senderName}" /></b> (
									<c:out value="${m.createdAt}" />
									):
									<c:out value="${m.messageText}" />
									<c:if test="${m.internalNote}">
										<em>[Ghi chú nội bộ]</em>
									</c:if>
								</p>
							</c:forEach>
						</section>
						<form method="post"
							action="${isAdmin ? '/admin/support/status' : '/manager/support/status'}">
							<input type="hidden" name="${_csrf.parameterName}"
								value="${_csrf.token}" /><input type="hidden" name="ticketId"
								value="${t.ticketId}" /><label>Trạng thái<select
								name="status"><option value="IN_PROGRESS">Đang
										xử lý</option>
									<option value="ESCALATED">Chuyển cấp Admin</option>
									<option value="RESOLVED">Đã giải quyết</option>
									<option value="CLOSED">Đóng</option></select></label>
							<button type="submit">Cập nhật</button>
						</form>
						<form method="post"
							action="${isAdmin ? '/admin/support/reply' : '/manager/support/reply'}"
							style="margin-top: 12px">
							<input type="hidden" name="${_csrf.parameterName}"
								value="${_csrf.token}" /><input type="hidden" name="ticketId"
								value="${t.ticketId}" /><label>Phản hồi<textarea
									name="message" required maxlength="4000" rows="3"></textarea></label>
							<c:if test="${isAdmin}">
								<label><input type="checkbox" name="internal"
									value="true"> Ghi chú nội bộ</label>
							</c:if>
							<button type="submit">Gửi phản hồi</button>
						</form>
					</article>
				</c:forEach>
			</c:otherwise>
		</c:choose>
	</main>
</body>
</html>
