
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>

<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Browse Books | UsedBookMarket</title>

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
				<ul class="navbar-nav me-auto ms-lg-4">
					<li class="nav-item"><a class="nav-link"
						href="${pageContext.request.contextPath}/">Home</a></li>
					<li class="nav-item"><a class="nav-link active"
						href="${pageContext.request.contextPath}/books">Browse Books</a></li>
				</ul>

				<div class="d-flex gap-2">
					<a class="btn btn-outline-dark"
						href="${pageContext.request.contextPath}/buyer/cart"> <i
						class="bi-cart-fill me-1"></i> Cart
					</a> <a class="btn btn-outline-primary"
						href="${pageContext.request.contextPath}/auth/login"> Sign In
					</a>
				</div>
			</div>
		</div>
	</nav>

	<header class="bg-dark py-5">
		<div class="container px-4 px-lg-5">
			<div class="text-center text-white">
				<h1 class="display-5 fw-bolder">Browse Books</h1>
				<p class="lead text-white-50">Discover your next favorite read.
				</p>

				<form action="${pageContext.request.contextPath}/books" method="get"
					class="d-flex justify-content-center">
					<div class="input-group home-search">
						<input class="form-control" type="search" name="keyword"
							value="<c:out value='${keyword}'/>"
							placeholder="Search by title or author..."
							aria-label="Search books">
						<button class="btn btn-primary" type="submit">
							<i class="bi-search me-1"></i> Search
						</button>
					</div>
				</form>
			</div>
		</div>
	</header>

	<section class="py-5">
		<div class="container px-4 px-lg-5">
			<div class="d-flex justify-content-between align-items-center mb-4">
				<h2 class="fw-bolder mb-0">Available Books</h2>
				<span class="text-muted"> <c:out value="${books.size()}" />
					result(s)
				</span>
			</div>

			<c:choose>
				<c:when test="${not empty books}">
					<div
						class="row gx-4 gx-lg-5 row-cols-1 row-cols-sm-2
                            row-cols-md-3 row-cols-xl-4">

						<c:forEach var="book" items="${books}">
							<div class="col mb-5">
								<div class="card h-100 book-card">

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
												Sold by
												<c:out value="${book.shopName}" />
											</p>
										</div>
									</div>

									<div class="card-footer p-4 pt-0 border-top-0 bg-transparent">
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
						<h3 class="mt-3">No books found</h3>
						<p class="text-muted">Try another search keyword or check back
							later.</p>
						<a class="btn btn-primary"
							href="${pageContext.request.contextPath}/books"> Browse All
							Books </a>
					</div>
				</c:otherwise>
			</c:choose>
		</div>
	</section>

	<footer class="py-5 bg-dark">
		<div class="container">
			<p class="m-0 text-center text-white">Copyright &copy;
				UsedBookMarket 2026</p>
		</div>
	</footer>

	<script
		src="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/js/bootstrap.bundle.min.js"></script>
	<script src="${pageContext.request.contextPath}/js/scripts.js"></script>
</body>
</html>
