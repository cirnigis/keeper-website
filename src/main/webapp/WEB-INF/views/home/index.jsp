<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>

<!DOCTYPE html>
<html lang="vi">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">

<title>Keeper | Khám phá những cuốn sách hay</title>

<link rel="icon" type="image/x-icon"
	href="${pageContext.request.contextPath}/assets/favicon.ico">

<link
	href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css"
	rel="stylesheet">

<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/styles.css">

<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/style.css">
</head>

<body>

	<nav class="navbar navbar-expand-lg navbar-light bg-light">
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

				<ul class="navbar-nav me-auto mb-2 mb-lg-0 ms-lg-4">
					<li class="nav-item"><a class="nav-link active"
						aria-current="page" href="${pageContext.request.contextPath}/">
							Trang chủ </a></li>

					<li class="nav-item"><a class="nav-link"
						href="${pageContext.request.contextPath}/books"> Khám phá sách
					</a></li>

					<li class="nav-item"><a class="nav-link"
						href="${pageContext.request.contextPath}/shops"> Cửa hàng </a></li>
				</ul>

				<div class="d-flex align-items-center gap-2 flex-wrap">

					<a class="btn btn-outline-dark"
						href="${pageContext.request.contextPath}/buyer/cart"> <i
						class="bi bi-cart-fill me-1"></i> Giỏ hàng
					</a>

					<c:choose>
						<c:when test="${pageContext.request.userPrincipal != null}">

							<c:choose>
								<c:when test="${userRole == 'ROLE_ADMIN'}">
									<a class="btn btn-primary"
										href="${pageContext.request.contextPath}/admin/profile">
										Tài khoản của tôi </a>
								</c:when>

								<c:when test="${userRole == 'ROLE_MANAGER'}">
									<a class="btn btn-primary"
										href="${pageContext.request.contextPath}/manager/profile">
										Tài khoản của tôi </a>
								</c:when>

								<c:when test="${userRole == 'ROLE_SELLER'}">
									<a class="btn btn-primary"
										href="${pageContext.request.contextPath}/seller/profile">
										Tài khoản của tôi </a>
								</c:when>

								<c:when test="${userRole == 'ROLE_SHIPPER'}">
									<a class="btn btn-primary"
										href="${pageContext.request.contextPath}/shipper/profile">
										Tài khoản của tôi </a>
								</c:when>

								<c:otherwise>
									<a class="btn btn-primary"
										href="${pageContext.request.contextPath}/buyer/profile">
										Tài khoản của tôi </a>
								</c:otherwise>
							</c:choose>

							<form action="${pageContext.request.contextPath}/auth/logout"
								method="post" class="d-inline">

								<input type="hidden" name="${_csrf.parameterName}"
									value="${_csrf.token}">

								<button class="btn btn-outline-secondary" type="submit">
									Đăng xuất</button>
							</form>

						</c:when>

						<c:otherwise>
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

	<header class="bg-dark py-5">
		<div class="container px-4 px-lg-5 my-5">
			<div class="text-center text-white">

				<h1 class="display-4 fw-bolder">Mỗi cuốn sách đều xứng đáng có
					thêm một câu chuyện</h1>

				<p class="lead fw-normal text-white-50 mb-4">Khám phá những cuốn
					sách đã qua sử dụng và tìm một câu chuyện mới để đồng hành cùng
					bạn.</p>

				<form action="${pageContext.request.contextPath}/books" method="get"
					class="d-flex justify-content-center">

					<div class="input-group home-search">
						<input class="form-control" type="search" name="keyword"
							placeholder="Tìm theo tên sách hoặc tác giả..."
							aria-label="Tìm kiếm sách">

						<button class="btn btn-primary" type="submit">
							<i class="bi bi-search me-1"></i> Tìm kiếm
						</button>
					</div>
				</form>

			</div>
		</div>
	</header>

	<section class="py-5">
		<div class="container px-4 px-lg-5 mt-2">

			<div
				class="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-3">
				<div>
					<h2 class="fw-bolder mb-1">Khám phá sách</h2>
					<p class="text-muted mb-0">Tìm những cuốn sách hay từ các cửa
						hàng độc lập.</p>
				</div>

				<a class="btn btn-outline-dark"
					href="${pageContext.request.contextPath}/books"> Xem tất cả
					sách </a>
			</div>

			<c:choose>
				<c:when test="${not empty books}">

					<div
						class="row gx-4 gx-lg-5 row-cols-1 row-cols-sm-2 row-cols-md-3 row-cols-xl-4 justify-content-center">

						<c:forEach var="book" items="${books}" end="7">

							<div class="col mb-5">
								<div class="card h-100 book-card">

									<c:choose>
										<c:when test="${not empty book.coverUrl}">
											<img class="card-img-top"
												src="<c:out value='${book.coverUrl}'/>" alt="Ảnh bìa sách">
										</c:when>

										<c:otherwise>
											<img class="card-img-top"
												src="https://dummyimage.com/450x300/e9ecef/495057.jpg&text=Bia+Sach"
												alt="Chưa có ảnh bìa sách">
										</c:otherwise>
									</c:choose>

									<div class="card-body p-4">
										<div class="text-center">

											<h5 class="fw-bolder">
												<c:out value="${book.title}" />
											</h5>

											<p class="small text-muted mb-2">
												<c:choose>
													<c:when test="${not empty book.authorName}">
														<c:out value="${book.authorName}" />
													</c:when>
													<c:otherwise>
                                                        Chưa có thông tin tác giả
                                                    </c:otherwise>
												</c:choose>
											</p>

											<span class="badge text-bg-secondary"> <c:choose>
													<c:when test="${book.conditionCode == 'NEW'}">
                                                        Mới
                                                    </c:when>
													<c:when test="${book.conditionCode == 'LIKE_NEW'}">
                                                        Như mới
                                                    </c:when>
													<c:when test="${book.conditionCode == 'VERY_GOOD'}">
                                                        Rất tốt
                                                    </c:when>
													<c:when test="${book.conditionCode == 'GOOD'}">
                                                        Tốt
                                                    </c:when>
													<c:when test="${book.conditionCode == 'ACCEPTABLE'}">
                                                        Chấp nhận được
                                                    </c:when>
													<c:otherwise>
														<c:out value="${book.conditionCode}" />
													</c:otherwise>
												</c:choose>
											</span>

											<p class="fw-bold text-primary mt-3 mb-1">
												<fmt:formatNumber value="${book.unitPrice}" type="number"
													groupingUsed="true" maxFractionDigits="0" />
												VNĐ
											</p>

											<p class="small text-muted mb-0">
												<i class="bi bi-shop me-1"></i>
												<c:choose>
													<c:when test="${not empty book.shopName}">
														<c:out value="${book.shopName}" />
													</c:when>
													<c:otherwise>
                                                        Chưa có thông tin cửa hàng
                                                    </c:otherwise>
												</c:choose>
											</p>

										</div>
									</div>

									<div class="card-footer p-4 pt-0 border-top-0 bg-transparent">
										<div class="text-center">
											<a class="btn btn-outline-dark mt-auto"
												href="${pageContext.request.contextPath}/books/${book.listingId}">
												Xem chi tiết </a>
										</div>
									</div>

								</div>
							</div>

						</c:forEach>
					</div>

				</c:when>

				<c:otherwise>
					<div class="empty-state text-center py-5">
						<i class="bi bi-book display-3 text-muted"></i>

						<h3 class="mt-3">Chưa có sách nào</h3>

						<p class="text-muted">Hiện chưa có sách nào được đăng bán. Vui
							lòng quay lại sau.</p>

						<a class="btn btn-primary"
							href="${pageContext.request.contextPath}/books"> Khám phá tất
							cả sách </a>
					</div>
				</c:otherwise>
			</c:choose>

		</div>
	</section>

	<section class="py-5 bg-light">
		<div class="container px-4 px-lg-5 text-center">

			<h2 class="fw-bolder">Bạn có những cuốn sách không còn sử dụng?
			</h2>

			<p class="text-muted mb-4">Mở cửa hàng của riêng bạn và giúp
				những cuốn sách tìm được người đọc tiếp theo.</p>

			<a class="btn btn-dark"
				href="${pageContext.request.contextPath}/buyer/seller-register">
				Đăng ký trở thành người bán </a>

		</div>
	</section>

	<footer class="py-5 bg-dark">
		<div class="container">
			<p class="m-0 text-center text-white">Bản quyền &copy; Keeper
				2026</p>
		</div>
	</footer>

	<script
		src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
	<script src="${pageContext.request.contextPath}/js/scripts.js"></script>

</body>
</html>

