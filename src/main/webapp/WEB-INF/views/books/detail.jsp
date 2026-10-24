```jsp
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>

<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">

<title><c:out value="${book.title}" /> | UsedBookMarket</title>

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

				<ul class="navbar-nav me-auto ms-lg-4">
					<li class="nav-item"><a class="nav-link"
						href="${pageContext.request.contextPath}/"> Home </a></li>

					<li class="nav-item"><a class="nav-link"
						href="${pageContext.request.contextPath}/books"> Browse Books
					</a></li>
				</ul>

				<c:choose>
					<c:when test="${pageContext.request.userPrincipal != null}">
						<a class="btn btn-outline-dark"
							href="${pageContext.request.contextPath}/buyer/cart"> <i
							class="bi bi-cart-fill me-1"></i> Cart
						</a>
					</c:when>

					<c:otherwise>
						<a class="btn btn-outline-dark"
							href="${pageContext.request.contextPath}/auth/login"> <i
							class="bi bi-person me-1"></i> Sign In
						</a>
					</c:otherwise>
				</c:choose>

			</div>
		</div>
	</nav>

	<!-- Book Details -->
	<section class="py-5">
		<div class="container px-4 px-lg-5">

			<!-- Breadcrumb -->
			<nav aria-label="breadcrumb" class="mb-4">
				<ol class="breadcrumb">
					<li class="breadcrumb-item"><a
						href="${pageContext.request.contextPath}/"> Home </a></li>

					<li class="breadcrumb-item"><a
						href="${pageContext.request.contextPath}/books"> Browse Books
					</a></li>

					<li class="breadcrumb-item active" aria-current="page">Book
						Details</li>
				</ol>
			</nav>

			<div class="row gx-5 gy-4">

				<!-- Book Cover -->
				<div class="col-md-5">
					<div class="detail-cover-container">

						<c:choose>
							<c:when test="${not empty book.coverUrl}">
								<img class="img-fluid rounded detail-cover"
									src="<c:out value='${book.coverUrl}'/>" alt="Book cover">
							</c:when>

							<c:otherwise>
								<img class="img-fluid rounded detail-cover"
									src="https://dummyimage.com/600x750/e9ecef/495057.jpg&text=Book+Cover"
									alt="Book cover placeholder">
							</c:otherwise>
						</c:choose>

					</div>
				</div>

				<!-- Book Information -->
				<div class="col-md-7">

					<span class="badge text-bg-secondary mb-2"> <c:out
							value="${book.conditionCode}" />
					</span>

					<h1 class="fw-bolder mb-3">
						<c:out value="${book.title}" />
					</h1>

					<p class="text-muted">
						<i class="bi bi-person me-1"></i>
						<c:choose>
							<c:when test="${not empty book.authorName}">
								<c:out value="${book.authorName}" />
							</c:when>
							<c:otherwise>
                                Unknown author
                            </c:otherwise>
						</c:choose>
					</p>

					<h2 class="text-primary fw-bold mb-4">
						<fmt:formatNumber value="${book.unitPrice}" type="number"
							groupingUsed="true" maxFractionDigits="0" />
						VND
					</h2>

					<!-- Book Metadata -->
					<div class="book-detail-meta mb-4">

						<p>
							<strong>Category:</strong>
							<c:out value="${book.categoryName}" />
						</p>

						<p>
							<strong>Publisher:</strong>
							<c:choose>
								<c:when test="${not empty book.publisherName}">
									<c:out value="${book.publisherName}" />
								</c:when>
								<c:otherwise>
                                    Not specified
                                </c:otherwise>
							</c:choose>
						</p>

						<p>
							<strong>ISBN:</strong>
							<c:choose>
								<c:when test="${not empty book.isbn}">
									<c:out value="${book.isbn}" />
								</c:when>
								<c:otherwise>
                                    Not specified
                                </c:otherwise>
							</c:choose>
						</p>

						<p>
							<strong>Edition:</strong>
							<c:choose>
								<c:when test="${not empty book.editionLabel}">
									<c:out value="${book.editionLabel}" />
								</c:when>
								<c:otherwise>
                                    Not specified
                                </c:otherwise>
							</c:choose>
						</p>

						<p>
							<strong>Language:</strong>
							<c:choose>
								<c:when test="${not empty book.languageCode}">
									<c:out value="${book.languageCode}" />
								</c:when>
								<c:otherwise>
                                    Not specified
                                </c:otherwise>
							</c:choose>
						</p>

						<p>
							<strong>Available quantity:</strong>
							<c:out value="${book.stockQuantity}" />
						</p>

					</div>

					<!-- Seller Information -->
					<div class="border rounded p-3 mb-4">

						<h5>
							<i class="bi bi-shop me-2"></i> Sold by
						</h5>

						<h6 class="fw-bold">
							<c:out value="${book.shopName}" />
						</h6>

						<c:if test="${not empty book.shopDescription}">
							<p class="text-muted mb-0">
								<c:out value="${book.shopDescription}" />
							</p>
						</c:if>

					</div>

					<!-- Purchase Actions -->
					<div class="d-flex gap-2 flex-wrap align-items-start">

						<a class="btn btn-outline-dark"
							href="${pageContext.request.contextPath}/books"> <i
							class="bi bi-arrow-left me-1"></i> Continue Browsing
						</a>

						<c:choose>

							<!-- Logged-in user -->
							<c:when test="${pageContext.request.userPrincipal != null}">

								<c:choose>
									<c:when test="${book.stockQuantity > 0}">

										<form method="post"
											action="${pageContext.request.contextPath}/buyer/cart/add"
											class="border rounded p-3 mt-2 mt-md-0">

											<input type="hidden" name="${_csrf.parameterName}"
												value="${_csrf.token}"> <input type="hidden"
												name="listingId" value="${book.listingId}">

											<div class="mb-3">
												<label for="quantity" class="form-label fw-semibold">
													Quantity </label> <input type="number" id="quantity"
													name="quantity" class="form-control" value="1" min="1"
													max="${book.stockQuantity}" required>
											</div>

											<button type="submit" class="btn btn-primary">
												<i class="bi bi-cart-plus me-1"></i> Add to Cart
											</button>

											<a href="${pageContext.request.contextPath}/buyer/cart"
												class="btn btn-outline-dark mt-2"> View Cart </a>

										</form>

									</c:when>

									<c:otherwise>
										<button type="button" class="btn btn-secondary" disabled>
											Out of Stock</button>
									</c:otherwise>
								</c:choose>

							</c:when>

							<!-- Guest user -->
							<c:otherwise>

								<a class="btn btn-primary"
									href="${pageContext.request.contextPath}/auth/login"> <i
									class="bi bi-cart-plus me-1"></i> Sign In to Purchase
								</a>

							</c:otherwise>

						</c:choose>

					</div>

				</div>
			</div>

			<hr class="my-5">

			<!-- Book Description -->
			<div class="row">
				<div class="col-lg-8">

					<h3 class="fw-bolder mb-3">Book Description</h3>

					<c:choose>
						<c:when test="${not empty book.description}">
							<p class="book-description">
								<c:out value="${book.description}" />
							</p>
						</c:when>

						<c:otherwise>
							<p class="text-muted">No description is available for this
								book.</p>
						</c:otherwise>
					</c:choose>

				</div>
			</div>

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
		src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js">
		
	</script>

	<script src="${pageContext.request.contextPath}/js/scripts.js">
		
	</script>

</body>
</html>
```
