
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>

<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">

<title>UsedBookMarket | Discover Your Next Book</title>

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

	<!-- Navigation -->
	<nav class="navbar navbar-expand-lg navbar-light bg-light">
		<div class="container px-4 px-lg-5">

			<a class="navbar-brand fw-bold"
				href="${pageContext.request.contextPath}/"> UsedBook<span
				class="text-primary">Market</span>
			</a>

			<button class="navbar-toggler" type="button"
				data-bs-toggle="collapse" data-bs-target="#mainNavbar"
				aria-controls="mainNavbar" aria-expanded="false"
				aria-label="Toggle navigation">
				<span class="navbar-toggler-icon"></span>
			</button>

			<div class="collapse navbar-collapse" id="mainNavbar">

				<ul class="navbar-nav me-auto mb-2 mb-lg-0 ms-lg-4">
					<li class="nav-item"><a class="nav-link active"
						aria-current="page" href="${pageContext.request.contextPath}/">
							Home </a></li>

					<li class="nav-item"><a class="nav-link"
						href="${pageContext.request.contextPath}/books"> Browse Books
					</a></li>

					<li class="nav-item"><a class="nav-link"
						href="${pageContext.request.contextPath}/shops"> Shops </a></li>
				</ul>

				<div class="d-flex align-items-center gap-2 flex-wrap">

					<a class="btn btn-outline-dark"
						href="${pageContext.request.contextPath}/buyer/cart"> <i
						class="bi bi-cart-fill me-1"></i> Cart
					</a>

					<c:choose>
						<c:when test="${pageContext.request.userPrincipal != null}">
							<a class="btn btn-primary"
								href="${pageContext.request.contextPath}/buyer/profile"> My
								Account </a>

							<form action="${pageContext.request.contextPath}/auth/logout"
								method="post" class="d-inline">

								<input type="hidden" name="${_csrf.parameterName}"
									value="${_csrf.token}">

								<button class="btn btn-outline-secondary" type="submit">
									Sign Out</button>
							</form>
						</c:when>

						<c:otherwise>
							<a class="btn btn-outline-primary"
								href="${pageContext.request.contextPath}/auth/login"> Sign
								In </a>

							<a class="btn btn-primary"
								href="${pageContext.request.contextPath}/auth/register">
								Register </a>
						</c:otherwise>
					</c:choose>

				</div>
			</div>
		</div>
	</nav>

	<!-- Hero -->
	<header class="bg-dark py-5">
		<div class="container px-4 px-lg-5 my-5">

			<div class="text-center text-white">
				<h1 class="display-4 fw-bolder">Every Book Deserves Another
					Story</h1>

				<p class="lead fw-normal text-white-50 mb-4">Discover pre-loved
					books and give your next favorite story a new home.</p>

				<form action="${pageContext.request.contextPath}/books" method="get"
					class="d-flex justify-content-center">

					<div class="input-group home-search">
						<input class="form-control" type="search" name="keyword"
							placeholder="Search by book title or author..."
							aria-label="Search books">

						<button class="btn btn-primary" type="submit">
							<i class="bi bi-search me-1"></i> Search
						</button>
					</div>
				</form>
			</div>

		</div>
	</header>

	<!-- Book Catalog -->
	<section class="py-5">
		<div class="container px-4 px-lg-5 mt-2">

			<div
				class="d-flex justify-content-between
                    align-items-center mb-4 flex-wrap gap-3">

				<div>
					<h2 class="fw-bolder mb-1">Discover Books</h2>
					<p class="text-muted mb-0">Find great reads from independent
						sellers.</p>
				</div>

				<a class="btn btn-outline-dark"
					href="${pageContext.request.contextPath}/books"> View All Books
				</a>
			</div>

			<c:choose>
				<c:when test="${not empty books}">

					<div
						class="row gx-4 gx-lg-5 row-cols-1
                            row-cols-sm-2 row-cols-md-3 row-cols-xl-4
                            justify-content-center">

						<c:forEach var="book" items="${books}" end="7">

							<div class="col mb-5">
								<div class="card h-100 book-card">

									<!-- Book Cover -->
									<c:choose>
										<c:when test="${not empty book.coverUrl}">
											<img class="card-img-top"
												src="<c:out value='${book.coverUrl}'/>" alt="Book cover">
										</c:when>

										<c:otherwise>
											<img class="card-img-top"
												src="https://dummyimage.com/450x300/e9ecef/495057.jpg&text=Book+Cover"
												alt="Book cover placeholder">
										</c:otherwise>
									</c:choose>

									<!-- Book Information -->
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
                                                    Unknown author
                                                </c:otherwise>
												</c:choose>
											</p>

											<span class="badge text-bg-secondary"> <c:out
													value="${book.conditionCode}" />
											</span>

											<p class="fw-bold text-primary mt-3 mb-1">
												<fmt:formatNumber value="${book.unitPrice}" type="number"
													groupingUsed="true" maxFractionDigits="0" />
												VND
											</p>

											<p class="small text-muted mb-0">
												<i class="bi bi-shop me-1"></i>
												<c:out value="${book.shopName}" />
											</p>

										</div>
									</div>

									<!-- View Details -->
									<div
										class="card-footer p-4 pt-0
                                            border-top-0 bg-transparent">

										<div class="text-center">
											<a class="btn btn-outline-dark mt-auto"
												href="${pageContext.request.contextPath}/books/${book.listingId}">
												View Details </a>
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

						<h3 class="mt-3">No books available yet</h3>

						<p class="text-muted">There are currently no books available
							for sale. Please check back later.</p>

						<a class="btn btn-primary"
							href="${pageContext.request.contextPath}/books"> Browse All
							Books </a>
					</div>

				</c:otherwise>
			</c:choose>

		</div>
	</section>

	<!-- Seller Call to Action -->
	<section class="py-5 bg-light">
		<div class="container px-4 px-lg-5 text-center">

			<h2 class="fw-bolder">Have Books You No Longer Need?</h2>

			<p class="text-muted mb-4">Open your shop and help your books
				find their next readers.</p>

			<a class="btn btn-dark"
				href="${pageContext.request.contextPath}/auth/register"> Join as
				a Seller </a>

		</div>
	</section>

	<!-- Footer -->
	<footer class="py-5 bg-dark">
		<div class="container">
			<p class="m-0 text-center text-white">Copyright &copy;
				UsedBookMarket 2026</p>
		</div>
	</footer>

	<script
		src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

	<script src="${pageContext.request.contextPath}/js/scripts.js"></script>

</body>
</html>
