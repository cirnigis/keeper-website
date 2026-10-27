

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>

<!DOCTYPE html>

<html lang="vi">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">

<title><c:out value="${book.title}" /> | Keeper</title>

<link
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
	rel="stylesheet">

<link
	href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css"
	rel="stylesheet">

<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/styles.css">

<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/style.css">

<style>
.detail-page {
	min-height: 60vh;
}

.detail-cover-container {
	display: flex;
	align-items: flex-start;
	justify-content: center;
	padding: 1rem;
	background: #f8fafc;
	border: 1px solid #e9ecef;
	border-radius: 1rem;
}

.detail-cover {
	width: 100%;
	max-height: 560px;
	object-fit: contain;
	border-radius: .5rem;
}

.detail-price {
	font-size: clamp(1.7rem, 3vw, 2.25rem);
}

.detail-meta-row {
	display: grid;
	grid-template-columns: minmax(130px, 180px) minmax(0, 1fr);
	gap: 1rem;
	padding: .75rem 0;
	border-bottom: 1px solid #edf0f4;
}

.detail-meta-row:last-child {
	border-bottom: 0;
}

.detail-meta-label {
	color: #6c757d;
}

.seller-panel {
	border: 1px solid #e5e7eb;
	border-radius: 1rem;
	padding: 1.25rem;
	background: #fff;
}

.detail-description {
	white-space: pre-line;
	overflow-wrap: anywhere;
	line-height: 1.8;
}

.detail-actions {
	display: flex;
	flex-wrap: wrap;
	align-items: flex-start;
	gap: .75rem;
}

@media ( max-width : 575.98px) {
	.detail-meta-row {
		grid-template-columns: 1fr;
		gap: .25rem;
	}
	.detail-actions>a, .detail-actions form {
		width: 100%;
	}
	.detail-actions .btn {
		width: 100%;
	}
}
</style>

</head>

<body>

	<!-- Thanh điều hướng -->
	<nav
		class="navbar navbar-expand-lg navbar-light bg-light border-bottom">
		<div class="container px-4 px-lg-5">

			<a class="keeper-brand" href="${pageContext.request.contextPath}/"
				aria-label="Keeper - Trang chủ"> <span class="keeper-brand-icon">K</span>
				<span class="keeper-brand-text">eeper</span>
			</a>

			<button class="navbar-toggler" type="button"
				data-bs-toggle="collapse" data-bs-target="#mainNavbar"
				aria-controls="mainNavbar" aria-expanded="false"
				aria-label="Mở hoặc đóng thanh điều hướng">
				<span class="navbar-toggler-icon"></span>
			</button>

			<div class="collapse navbar-collapse" id="mainNavbar">

				<ul class="navbar-nav me-auto ms-lg-4">
					<li class="nav-item"><a class="nav-link"
						href="${pageContext.request.contextPath}/"> Trang chủ </a></li>

					<li class="nav-item"><a class="nav-link active"
						href="${pageContext.request.contextPath}/books"> Khám phá sách
					</a></li>

					<li class="nav-item"><a class="nav-link"
						href="${pageContext.request.contextPath}/shops"> Cửa hàng </a></li>
				</ul>

				<div class="d-flex align-items-center gap-2 flex-wrap py-2 py-lg-0">

					<c:choose>
						<c:when test="${pageContext.request.userPrincipal != null}">
							<a class="btn btn-outline-dark"
								href="${pageContext.request.contextPath}/buyer/cart"> <i
								class="bi bi-cart-fill me-1"></i> Giỏ hàng
							</a>

							<a class="btn btn-primary"
								href="${pageContext.request.contextPath}/buyer/profile"> <i
								class="bi bi-person-circle me-1"></i> Tài khoản của tôi
							</a>

							<form action="${pageContext.request.contextPath}/auth/logout"
								method="post" class="d-inline">
								<input type="hidden" name="${_csrf.parameterName}"
									value="${_csrf.token}">

								<button class="btn btn-outline-secondary" type="submit">
									Đăng xuất</button>
							</form>
						</c:when>

						<c:otherwise>
							<a class="btn btn-outline-dark"
								href="${pageContext.request.contextPath}/auth/login"> <i
								class="bi bi-cart me-1"></i> Giỏ hàng
							</a>

							<a class="btn btn-outline-primary"
								href="${pageContext.request.contextPath}/auth/login"> Đăng
								nhập </a>

							<a class="btn btn-primary"
								href="${pageContext.request.contextPath}/auth/register">
								Đăng ký </a>
						</c:otherwise>
					</c:choose>

				</div>
			</div>
		</div>
	</nav>

	<main class="detail-page py-4 py-lg-5">
		<div class="container px-4 px-lg-5">

			<!-- Đường dẫn điều hướng -->
			<nav aria-label="Đường dẫn điều hướng" class="mb-4">
				<ol class="breadcrumb">
					<li class="breadcrumb-item"><a
						href="${pageContext.request.contextPath}/"> Trang chủ </a></li>

					<li class="breadcrumb-item"><a
						href="${pageContext.request.contextPath}/books"> Khám phá sách
					</a></li>

					<li class="breadcrumb-item active" aria-current="page">Chi
						tiết sách</li>
				</ol>
			</nav>

			<div class="row gx-lg-5 gy-4">

				<!-- Ảnh bìa -->
				<div class="col-12 col-md-5">
					<div class="detail-cover-container">
						<c:choose>
							<c:when test="${not empty book.coverUrl}">
								<img class="img-fluid detail-cover"
									src="<c:out value='${book.coverUrl}'/>"
									alt="Ảnh bìa <c:out value='${book.title}'/>">
							</c:when>

							<c:otherwise>
								<img class="img-fluid detail-cover"
									src="https://dummyimage.com/600x750/e9ecef/495057.jpg&text=Keeper"
									alt="Sách chưa có ảnh bìa">
							</c:otherwise>
						</c:choose>
					</div>
				</div>

				<!-- Thông tin listing -->
				<div class="col-12 col-md-7">

					<div class="mb-3">
						<span class="badge text-bg-secondary"> <c:choose>
								<c:when test="${book.conditionCode == 'NEW'}">Mới</c:when>
								<c:when test="${book.conditionCode == 'LIKE_NEW'}">Như mới</c:when>
								<c:when test="${book.conditionCode == 'VERY_GOOD'}">Rất tốt</c:when>
								<c:when test="${book.conditionCode == 'GOOD'}">Tốt</c:when>
								<c:when test="${book.conditionCode == 'ACCEPTABLE'}">Chấp nhận được</c:when>
								<c:otherwise>
									<c:out value="${book.conditionCode}" />
								</c:otherwise>
							</c:choose>
						</span>
					</div>

					<h1 class="fw-bolder mb-3">
						<c:out value="${book.title}" />
					</h1>

					<p class="text-muted mb-3">
						<i class="bi bi-person me-1"></i>
						<c:choose>
							<c:when test="${not empty book.authorName}">
								<c:out value="${book.authorName}" />
							</c:when>
							<c:otherwise>Chưa có thông tin tác giả</c:otherwise>
						</c:choose>
					</p>

					<div class="detail-price text-primary fw-bold mb-4">
						<fmt:formatNumber value="${book.unitPrice}" type="number"
							groupingUsed="true" maxFractionDigits="0" />
						VNĐ
					</div>

					<!-- Thông tin xuất bản -->
					<section class="mb-4">
						<h5 class="fw-bold mb-2">Thông tin sách</h5>

						<div class="detail-meta-row">
							<span class="detail-meta-label">Thể loại</span> <span> <c:choose>
									<c:when test="${not empty book.categoryName}">
										<c:out value="${book.categoryName}" />
									</c:when>
									<c:otherwise>Chưa phân loại</c:otherwise>
								</c:choose>
							</span>
						</div>

						<div class="detail-meta-row">
							<span class="detail-meta-label">Nhà xuất bản</span> <span>
								<c:choose>
									<c:when test="${not empty book.publisherName}">
										<c:out value="${book.publisherName}" />
									</c:when>
									<c:otherwise>Chưa có thông tin</c:otherwise>
								</c:choose>
							</span>
						</div>

						<div class="detail-meta-row">
							<span class="detail-meta-label">ISBN</span> <span> <c:choose>
									<c:when test="${not empty book.isbn}">
										<c:out value="${book.isbn}" />
									</c:when>
									<c:otherwise>Chưa có thông tin</c:otherwise>
								</c:choose>
							</span>
						</div>

						<div class="detail-meta-row">
							<span class="detail-meta-label">Phiên bản</span> <span> <c:choose>
									<c:when test="${not empty book.editionLabel}">
										<c:out value="${book.editionLabel}" />
									</c:when>
									<c:otherwise>Chưa có thông tin</c:otherwise>
								</c:choose>
							</span>
						</div>

						<div class="detail-meta-row">
							<span class="detail-meta-label">Ngôn ngữ</span> <span> <c:choose>
									<c:when test="${book.languageCode == 'vi'}">Tiếng Việt</c:when>
									<c:when test="${book.languageCode == 'en'}">Tiếng Anh</c:when>
									<c:when test="${book.languageCode == 'fr'}">Tiếng Pháp</c:when>
									<c:when test="${book.languageCode == 'zh'}">Tiếng Trung</c:when>
									<c:when test="${book.languageCode == 'ja'}">Tiếng Nhật</c:when>
									<c:when test="${not empty book.languageCode}">
										<c:out value="${book.languageCode}" />
									</c:when>
									<c:otherwise>Chưa có thông tin</c:otherwise>
								</c:choose>
							</span>
						</div>

						<div class="detail-meta-row">
							<span class="detail-meta-label">Số lượng còn lại</span> <span>
								<c:out value="${book.stockQuantity}" />
							</span>
						</div>
					</section>

					<!-- Thông tin cửa hàng -->
					<section class="seller-panel mb-4">
						<h5 class="fw-bold mb-3">
							<i class="bi bi-shop me-2"></i> Thông tin người bán
						</h5>

						<h6 class="fw-bold mb-2">
							<c:choose>
								<c:when test="${not empty book.shopName}">
									<c:out value="${book.shopName}" />
								</c:when>
								<c:otherwise>Chưa có thông tin cửa hàng</c:otherwise>
							</c:choose>
						</h6>

						<c:if test="${not empty book.shopDescription}">
							<p class="text-muted mb-0">
								<c:out value="${book.shopDescription}" />
							</p>
						</c:if>
					</section>

					<!-- Thao tác mua sách -->
					<div class="detail-actions">

						<a class="btn btn-outline-dark"
							href="${pageContext.request.contextPath}/books"> <i
							class="bi bi-arrow-left me-1"></i> Tiếp tục khám phá
						</a>

						<c:choose>
							<c:when test="${pageContext.request.userPrincipal != null}">
								<c:choose>
									<c:when test="${book.stockQuantity > 0}">
										<form method="post"
											action="${pageContext.request.contextPath}/buyer/cart/add"
											class="border rounded-3 p-3">

											<input type="hidden" name="${_csrf.parameterName}"
												value="${_csrf.token}"> <input type="hidden"
												name="listingId" value="${book.listingId}">

											<div class="mb-3">
												<label for="quantity" class="form-label fw-semibold">
													Số lượng </label> <input type="number" id="quantity"
													name="quantity" class="form-control" value="1" min="1"
													max="${book.stockQuantity}" required>
												<div class="form-text">Tối đa ${book.stockQuantity}
													cuốn cho listing này.</div>
											</div>

											<button type="submit" class="btn btn-primary">
												<i class="bi bi-cart-plus me-1"></i> Thêm vào giỏ hàng
											</button>
										</form>
									</c:when>

									<c:otherwise>
										<button type="button" class="btn btn-secondary" disabled>
											<i class="bi bi-x-circle me-1"></i> Hết hàng
										</button>
									</c:otherwise>
								</c:choose>
							</c:when>

							<c:otherwise>
								<a class="btn btn-primary"
									href="${pageContext.request.contextPath}/auth/login"> <i
									class="bi bi-person me-1"></i> Đăng nhập để mua sách
								</a>
							</c:otherwise>
						</c:choose>

					</div>

				</div>
			</div>

			<hr class="my-5">

			<!-- Mô tả sách -->
			<section class="row">
				<div class="col-12 col-lg-9">
					<h3 class="fw-bolder mb-3">Mô tả sách</h3>

					<c:choose>
						<c:when test="${not empty book.description}">
							<p class="detail-description">
								<c:out value="${book.description}" />
							</p>
						</c:when>

						<c:otherwise>
							<p class="text-muted">Hiện chưa có mô tả cho cuốn sách này.</p>
						</c:otherwise>
					</c:choose>
				</div>
			</section>

		</div>
	</main>

	<!-- Chân trang -->
	<footer class="py-4 bg-dark">
		<div class="container">
			<p class="m-0 text-center text-white">Bản quyền &copy; Keeper
				2026</p>
		</div>
	</footer>

	<script
		src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js">
		
	</script>

	<script src="${pageContext.request.contextPath}/js/scripts.js"></script>

</body>
</html>