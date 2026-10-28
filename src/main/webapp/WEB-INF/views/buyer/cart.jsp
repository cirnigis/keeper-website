<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>

<!DOCTYPE html>
<html lang="vi">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Giỏ hàng | Keeper</title>

<link
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
	rel="stylesheet">
<link href="${pageContext.request.contextPath}/css/styles.css"
	rel="stylesheet">
<link href="${pageContext.request.contextPath}/css/style.css"
	rel="stylesheet">

<style>
.cart-page {
	min-height: 65vh;
	padding: 48px 0;
}

.keeper-brand {
	display: inline-flex;
	align-items: center;
	gap: 9px;
	text-decoration: none;
	color: #14213d;
	font-size: 1.45rem;
	font-weight: 800;
	letter-spacing: -0.7px;
}

.keeper-brand-icon {
	display: inline-flex;
	align-items: center;
	justify-content: center;
	width: 38px;
	height: 38px;
	border-radius: 11px;
	background: #1463f3;
	color: #fff;
	font-size: 1.35rem;
	font-weight: 800;
	box-shadow: 0 4px 10px rgba(20, 99, 243, 0.2);
}

.keeper-brand-text {
	margin-left: -8px;
}

.cart-cover {
	width: 76px;
	height: 100px;
	object-fit: contain;
	background: #f8f9fa;
	border-radius: 6px;
}

.cart-table td {
	vertical-align: middle;
}

.cart-total {
	font-size: 1.4rem;
	font-weight: 700;
	color: #14213d;
}

.cart-empty {
	padding: 60px 20px;
	text-align: center;
	background: #fff;
	border-radius: 12px;
}

.keeper-btn-primary {
	background: #1463f3;
	border-color: #1463f3;
	color: #fff;
}

.keeper-btn-primary:hover {
	background: #0f50c4;
	border-color: #0f50c4;
	color: #fff;
}

@media ( max-width : 576px) {
	.cart-page {
		padding: 28px 0;
	}
	.cart-cover {
		width: 58px;
		height: 78px;
	}
	.cart-total {
		font-size: 1.15rem;
	}
}
</style>
</head>

<body>
	<nav class="navbar navbar-expand-lg navbar-light bg-light">
		<div class="container px-4 px-lg-5">
			<a class="keeper-brand" href="${pageContext.request.contextPath}/"
				aria-label="Keeper - Trang chủ"> <span class="keeper-brand-icon">K</span>
				<span class="keeper-brand-text">eeper</span>
			</a>

			<div class="d-flex gap-2">
				<a class="btn btn-outline-dark"
					href="${pageContext.request.contextPath}/books"> Xem sách </a> <a
					class="btn keeper-btn-primary"
					href="${pageContext.request.contextPath}/buyer/cart"> Giỏ hàng
				</a>
			</div>
		</div>
	</nav>

	<main class="cart-page">
		<div class="container px-4 px-lg-5">
			<div
				class="d-flex flex-wrap justify-content-between align-items-center gap-3 mb-4">
				<h1 class="h2 mb-0">Giỏ hàng của bạn</h1>

				<a class="btn btn-outline-secondary"
					href="${pageContext.request.contextPath}/books"> Tiếp tục mua
					sắm </a>
			</div>

			<c:if test="${not empty successMessage}">
				<div class="alert alert-success" role="alert">
					<c:out value="${successMessage}" />
				</div>
			</c:if>

			<c:if test="${not empty errorMessage}">
				<div class="alert alert-danger" role="alert">
					<c:out value="${errorMessage}" />
				</div>
			</c:if>

			<c:choose>
				<c:when test="${empty items}">
					<div class="cart-empty shadow-sm">
						<div class="mb-3">
							<span class="keeper-brand-icon"
								style="margin: 0 auto; width: 58px; height: 58px; font-size: 2rem;">K</span>
						</div>

						<h2 class="h4 mb-3">Giỏ hàng đang trống</h2>
						<p class="text-muted">Khám phá những cuốn sách hay và tìm cuốn
							sách yêu thích tiếp theo của bạn.</p>

						<a class="btn keeper-btn-primary"
							href="${pageContext.request.contextPath}/books"> Khám phá
							sách </a>
					</div>
				</c:when>

				<c:otherwise>
					<div class="table-responsive bg-white rounded shadow-sm">
						<table class="table cart-table align-middle mb-0">
							<thead class="table-light">
								<tr>
									<th scope="col">Sách</th>
									<th scope="col">Cửa hàng</th>
									<th scope="col">Tình trạng</th>
									<th scope="col">Đơn giá</th>
									<th scope="col">Số lượng</th>
									<th scope="col">Thành tiền</th>
									<th scope="col">Thao tác</th>
								</tr>
							</thead>

							<tbody>
								<c:forEach var="item" items="${items}">
									<tr>
										<td>
											<div class="d-flex align-items-center gap-3">
												<c:choose>
													<c:when test="${not empty item.coverUrl}">
														<img class="cart-cover"
															src="<c:out value='${item.coverUrl}'/>" alt="Bìa sách">
													</c:when>
													<c:otherwise>
														<div
															class="cart-cover d-flex align-items-center justify-content-center">
															<span class="text-muted small">Chưa có ảnh</span>
														</div>
													</c:otherwise>
												</c:choose>

												<div>
													<a class="fw-semibold text-dark text-decoration-none"
														href="${pageContext.request.contextPath}/books/${item.listingId}">
														<c:out value="${item.title}" />
													</a>
												</div>
											</div>
										</td>

										<td><c:out value="${item.shopName}" /></td>
										<td><c:out value="${item.conditionCode}" /></td>

										<td><fmt:formatNumber value="${item.unitPrice}"
												type="number" groupingUsed="true" /> ₫</td>

										<td>
											<form method="post"
												action="${pageContext.request.contextPath}/buyer/cart/update"
												class="d-flex align-items-center gap-2">

												<input type="hidden" name="${_csrf.parameterName}"
													value="${_csrf.token}"> <input type="hidden"
													name="cartItemId" value="${item.cartItemId}"> <input
													type="number" name="quantity" min="1"
													max="${item.stockQuantity}" value="${item.quantity}"
													class="form-control" style="width: 85px"
													aria-label="Số lượng">

												<button type="submit" class="btn btn-sm btn-outline-primary">
													Cập nhật</button>
											</form> <small class="text-muted"> Còn lại: <c:out
													value="${item.stockQuantity}" />
										</small>
										</td>

										<td class="fw-semibold"><fmt:formatNumber
												value="${item.lineTotal}" type="number" groupingUsed="true" />
											₫</td>

										<td>
											<form method="post"
												action="${pageContext.request.contextPath}/buyer/cart/remove"
												onsubmit="return confirm('Bạn có chắc muốn xóa sách này khỏi giỏ hàng không?');">

												<input type="hidden" name="${_csrf.parameterName}"
													value="${_csrf.token}"> <input type="hidden"
													name="cartItemId" value="${item.cartItemId}">

												<button type="submit" class="btn btn-sm btn-outline-danger">
													Xóa</button>
											</form>
										</td>
									</tr>
								</c:forEach>
							</tbody>
						</table>
					</div>

					<div class="row justify-content-end mt-4">
						<div class="col-md-5 col-lg-4">
							<div class="card shadow-sm">
								<div class="card-body">
									<div
										class="d-flex justify-content-between align-items-center gap-3 mb-3">
										<span>Tạm tính</span> <span class="cart-total text-end">
											<fmt:formatNumber value="${cartTotal}" type="number"
												groupingUsed="true" /> ₫
										</span>
									</div>

									<p class="text-muted small">Phí vận chuyển và ưu đãi (nếu
										có) sẽ được tính ở bước thanh toán.</p>

									<a class="btn keeper-btn-primary w-100"
										href="${pageContext.request.contextPath}/buyer/cart/checkout">
										Tiến hành thanh toán </a>
								</div>
							</div>
						</div>
					</div>
				</c:otherwise>
			</c:choose>
		</div>
	</main>

	<footer class="py-4 bg-dark">
		<div class="container">
			<p class="m-0 text-center text-white">© Keeper. Bảo lưu mọi
				quyền.</p>
		</div>
	</footer>

	<script
		src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>

