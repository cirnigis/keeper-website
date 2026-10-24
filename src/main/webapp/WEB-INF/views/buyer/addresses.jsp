
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>

<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Shipping Addresses | Used Book Marketplace</title>

<link
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
	rel="stylesheet">
<link href="/css/styles.css" rel="stylesheet">
<link href="/css/style.css" rel="stylesheet">
</head>
<body>

	<nav
		class="navbar navbar-expand-lg navbar-light bg-white border-bottom">
		<div class="container">
			<a class="navbar-brand fw-bold" href="/">Used Book Marketplace</a>
			<div class="ms-auto d-flex gap-3">
				<a class="nav-link" href="/books">Browse Books</a> <a
					class="nav-link" href="/buyer/cart">Cart</a> <a
					class="nav-link active" href="/buyer/addresses">Addresses</a>
				<form action="/auth/logout" method="post" class="m-0">
					<input type="hidden" name="${_csrf.parameterName}"
						value="${_csrf.token}">
					<button type="submit" class="btn btn-outline-secondary btn-sm">
						Sign Out</button>
				</form>
			</div>
		</div>
	</nav>

	<main class="container py-5">
		<div class="row justify-content-center">
			<div class="col-lg-10">

				<div
					class="d-flex flex-wrap justify-content-between align-items-center mb-4">
					<div>
						<h1 class="h3 mb-1">Shipping Addresses</h1>
						<p class="text-muted mb-0">Manage the addresses used for your
							orders.</p>
					</div>
					<a href="/buyer/cart" class="btn btn-outline-secondary"> Back
						to Cart </a>
				</div>

				<c:if test="${not empty successMessage}">
					<div class="alert alert-success alert-dismissible fade show">
						<c:out value="${successMessage}" />
						<button type="button" class="btn-close" data-bs-dismiss="alert"></button>
					</div>
				</c:if>

				<c:if test="${not empty errorMessage}">
					<div class="alert alert-danger alert-dismissible fade show">
						<c:out value="${errorMessage}" />
						<button type="button" class="btn-close" data-bs-dismiss="alert"></button>
					</div>
				</c:if>

				<section class="card border-0 shadow-sm mb-5">
					<div class="card-header bg-white py-3">
						<h2 class="h5 mb-0">Saved Addresses</h2>
					</div>

					<div class="card-body">
						<c:choose>
							<c:when test="${empty addresses}">
								<div class="text-center py-4">
									<h3 class="h6">No shipping addresses yet</h3>
									<p class="text-muted mb-0">Add an address below to continue
										with checkout.</p>
								</div>
							</c:when>

							<c:otherwise>
								<c:forEach var="address" items="${addresses}">
									<div class="border rounded p-3 mb-3">
										<div class="d-flex flex-wrap justify-content-between gap-3">
											<div>
												<div class="fw-semibold">
													<c:out value="${address.recipientName}" />
													<span class="text-muted fw-normal"> | <c:out
															value="${address.phone}" />
													</span>
												</div>

												<div class="text-muted mt-2">
													<c:out value="${address.addressLine}" />
													,
													<c:out value="${address.ward}" />
													,
													<c:out value="${address.district}" />
													,
													<c:out value="${address.province}" />
												</div>

												<c:if test="${address.default}">
													<span class="badge text-bg-success mt-2"> Default
														Address </span>
												</c:if>
											</div>

											<div class="d-flex align-items-start gap-2">
												<c:if test="${not address.default}">
													<form method="post"
														action="/buyer/addresses/${address.addressId}/default">
														<input type="hidden" name="${_csrf.parameterName}"
															value="${_csrf.token}">
														<button type="submit"
															class="btn btn-outline-primary btn-sm">Set
															Default</button>
													</form>
												</c:if>

												<form method="post"
													action="/buyer/addresses/${address.addressId}/delete"
													onsubmit="return confirm('Remove this address?');">
													<input type="hidden" name="${_csrf.parameterName}"
														value="${_csrf.token}">
													<button type="submit" class="btn btn-outline-danger btn-sm">
														Remove</button>
												</form>
											</div>
										</div>
									</div>
								</c:forEach>
							</c:otherwise>
						</c:choose>
					</div>
				</section>

				<section class="card border-0 shadow-sm">
					<div class="card-header bg-white py-3">
						<h2 class="h5 mb-0">Add a New Address</h2>
					</div>

					<div class="card-body">
						<form method="post" action="/buyer/addresses/add">
							<input type="hidden" name="${_csrf.parameterName}"
								value="${_csrf.token}">

							<div class="row g-3">
								<div class="col-md-6">
									<label for="recipientName" class="form-label">
										Recipient Name </label> <input id="recipientName" name="recipientName"
										class="form-control" maxlength="150" required>
								</div>

								<div class="col-md-6">
									<label for="phone" class="form-label"> Phone Number </label> <input
										id="phone" name="phone" class="form-control" maxlength="20"
										required>
								</div>

								<div class="col-md-4">
									<label for="province" class="form-label"> Province /
										City </label> <input id="province" name="province"
										class="form-control" maxlength="100" required>
								</div>

								<div class="col-md-4">
									<label for="district" class="form-label"> District </label> <input
										id="district" name="district" class="form-control"
										maxlength="100" required>
								</div>

								<div class="col-md-4">
									<label for="ward" class="form-label"> Ward / Commune </label> <input
										id="ward" name="ward" class="form-control" maxlength="100"
										required>
								</div>

								<div class="col-12">
									<label for="addressLine" class="form-label"> Street
										Address </label>
									<textarea id="addressLine" name="addressLine"
										class="form-control" rows="2" maxlength="300" required></textarea>
								</div>

								<div class="col-12">
									<button type="submit" class="btn btn-primary">Save
										Address</button>
								</div>
							</div>
						</form>
					</div>
				</section>

			</div>
		</div>
	</main>

	<footer class="border-top py-4 mt-5">
		<div class="container text-center text-muted small">Used Book
			Marketplace</div>
	</footer>

	<script
		src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>