
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Quản lý sản phẩm</title>
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
	<main class="container">
		<section class="page-header">
			<h1>Quản lý sản phẩm</h1>
			<p>Quản lý giá bán, tình trạng sách và tồn kho.</p>
			<a class="btn btn-secondary"
				href="${pageContext.request.contextPath}/seller/shops">Quay lại
				shop</a> <a class="btn btn-primary"
				href="${pageContext.request.contextPath}/seller/listings/new?shopId=${selectedShopId}">Đăng
				bán sách</a>
		</section>

		<c:if test="${not empty successMessage}">
			<div class="alert alert-success">
				<c:out value="${successMessage}" />
			</div>
		</c:if>
		<c:if test="${not empty errorMessage}">
			<div class="alert alert-danger">
				<c:out value="${errorMessage}" />
			</div>
		</c:if>

		<section class="card">
			<form method="get"
				action="${pageContext.request.contextPath}/seller/listings">
				<label for="shopId">Chọn shop</label> <select id="shopId"
					name="shopId" required>
					<c:forEach var="shop" items="${shops}">
						<option value="${shop.shopId}"
							${shop.shopId eq selectedShopId ? 'selected' : ''}
							<c:if test="${shop.approvalStatus ne 'APPROVED' or not shop.isActive}">disabled</c:if>>
							<c:out value="${shop.shopName}" /> -
							<c:out value="${shop.approvalStatus}" />
						</option>
					</c:forEach>
				</select>
				<button class="btn btn-primary" type="submit">Xem sản phẩm</button>
			</form>
		</section>

		<c:choose>
			<c:when test="${empty listings}">
				<section class="card">
					<h2>Chưa có sản phẩm</h2>
					<p>Hãy đăng bán một cuốn sách có sẵn trong danh mục hệ thống.</p>
				</section>
			</c:when>
			<c:otherwise>
				<div class="table-responsive">
					<table class="data-table">
						<thead>
							<tr>
								<th>Sách</th>
								<th>Tình trạng</th>
								<th>Giá bán</th>
								<th>Tồn kho</th>
								<th>Đã bán</th>
								<th>Trạng thái</th>
								<th>Thao tác</th>
							</tr>
						</thead>
						<tbody>
							<c:forEach var="item" items="${listings}">
								<tr>
									<td><strong><c:out value="${item.title}" /></strong>
										<div class="muted">
											ISBN:
											<c:out value="${item.isbn}" />
										</div>
										<div class="muted">
											Listing #
											<c:out value="${item.listingId}" />
										</div></td>
									<td><c:out value="${item.conditionCode}" /></td>
									<td><c:out value="${item.unitPrice}" /> ₫</td>
									<td><c:out value="${item.stockQuantity}" /></td>
									<td><c:out value="${item.soldQuantity}" /></td>
									<td><c:out value="${item.listingStatus}" /></td>
									<td><details>
											<summary>Sửa</summary>
											<form method="post"
												action="${pageContext.request.contextPath}/seller/listings/update">
												<input type="hidden" name="${_csrf.parameterName}"
													value="${_csrf.token}"> <input type="hidden"
													name="shopId" value="${selectedShopId}"> <input
													type="hidden" name="listingId" value="${item.listingId}">

												<label>Tên hiển thị</label> <input name="listingTitle"
													maxlength="300" value="<c:out value='${item.title}'/>">

												<label>Tình trạng</label> <select name="conditionCode"
													required>
													<option value="LIKE_NEW"
														${item.conditionCode eq 'LIKE_NEW' ? 'selected' : ''}>Như
														mới</option>
													<option value="VERY_GOOD"
														${item.conditionCode eq 'VERY_GOOD' ? 'selected' : ''}>Rất
														tốt</option>
													<option value="GOOD"
														${item.conditionCode eq 'GOOD' ? 'selected' : ''}>Tốt</option>
													<option value="ACCEPTABLE"
														${item.conditionCode eq 'ACCEPTABLE' ? 'selected' : ''}>Chấp
														nhận được</option>
												</select> <label>Giá bán</label> <input type="number"
													name="unitPrice" min="0" step="0.01" required
													value="${item.unitPrice}"> <label>Tồn kho</label> <input
													type="number" name="stockQuantity" min="0" required
													value="${item.stockQuantity}">

												<button class="btn btn-primary" type="submit">Lưu</button>
											</form>
										</details>

										<form method="post"
											action="${pageContext.request.contextPath}/seller/listings/status">
											<input type="hidden" name="${_csrf.parameterName}"
												value="${_csrf.token}"> <input type="hidden"
												name="shopId" value="${selectedShopId}"> <input
												type="hidden" name="listingId" value="${item.listingId}">

											<c:choose>
												<c:when test="${item.listingStatus eq 'ACTIVE'}">
													<input type="hidden" name="status" value="HIDDEN">
													<button class="btn btn-secondary" type="submit">Ẩn</button>
												</c:when>
												<c:when
													test="${item.listingStatus eq 'HIDDEN' and item.stockQuantity gt 0}">
													<input type="hidden" name="status" value="ACTIVE">
													<button class="btn btn-primary" type="submit">Hiện</button>
												</c:when>
											</c:choose>
										</form></td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</div>
			</c:otherwise>
		</c:choose>
	</main>
</body>
</html>
