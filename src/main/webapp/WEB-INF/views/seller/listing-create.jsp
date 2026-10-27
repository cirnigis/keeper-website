
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Đăng bán sách</title>
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
	<main class="container">
		<section class="page-header">
			<h1>Đăng bán sách</h1>
			<p>Chọn sách đã có trong hệ thống và nhập thông tin bán hàng.</p>
		</section>

		<c:if test="${not empty errorMessage}">
			<div class="alert alert-danger">
				<c:out value="${errorMessage}" />
			</div>
		</c:if>

		<section class="card">
			<form method="get"
				action="${pageContext.request.contextPath}/seller/listings/new">
				<input type="hidden" name="shopId" value="${selectedShopId}">
				<label for="keyword">Tìm sách theo tên hoặc ISBN</label> <input
					id="keyword" name="keyword" maxlength="100"
					value="<c:out value='${keyword}'/>">
				<button class="btn btn-secondary" type="submit">Tìm kiếm</button>
			</form>
		</section>

		<c:choose>
			<c:when test="${empty books}">
				<section class="card">
					<p>Không tìm thấy sách phù hợp. Hãy thử từ khóa khác.</p>
				</section>
			</c:when>
			<c:otherwise>
				<div class="card-grid">
					<c:forEach var="book" items="${books}">
						<section class="card">
							<h2>
								<c:out value="${book.title}" />
							</h2>
							<p>
								ISBN:
								<c:out value="${book.isbn}" />
							</p>
							<p>
								Danh mục:
								<c:out value="${book.categoryName}" />
							</p>
							<p>
								Năm xuất bản:
								<c:out value="${book.publicationYear}" />
							</p>

							<form method="post"
								action="${pageContext.request.contextPath}/seller/listings/create">
								<input type="hidden" name="${_csrf.parameterName}"
									value="${_csrf.token}"> <input type="hidden"
									name="shopId" value="${selectedShopId}"> <input
									type="hidden" name="bookId" value="${book.bookId}"> <label>Tên
									hiển thị riêng (không bắt buộc)</label> <input name="listingTitle"
									maxlength="300" placeholder="Mặc định dùng tên sách"> <label>Tình
									trạng sách</label> <select name="conditionCode" required>
									<option value="LIKE_NEW">Như mới</option>
									<option value="VERY_GOOD">Rất tốt</option>
									<option value="GOOD" selected>Tốt</option>
									<option value="ACCEPTABLE">Chấp nhận được</option>
								</select> <label>Giá bán (VNĐ)</label> <input type="number"
									name="unitPrice" min="0" step="0.01" required> <label>Số
									lượng tồn kho</label> <input type="number" name="stockQuantity" min="0"
									value="1" required>

								<button class="btn btn-primary" type="submit">Đăng bán
									sách này</button>
							</form>
						</section>
					</c:forEach>
				</div>
			</c:otherwise>
		</c:choose>

		<a class="btn btn-secondary"
			href="${pageContext.request.contextPath}/seller/listings?shopId=${selectedShopId}">Quay
			lại sản phẩm</a>
	</main>
</body>
</html>
