
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>

<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Shopping Cart - Used Book Marketplace</title>

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
}

.cart-empty {
	padding: 60px 20px;
	text-align: center;
	background: #fff;
	border-radius: 12px;
}
</style>
</head>

<body>
	<nav class="navbar navbar-expand-lg navbar-light bg-light">
		<div class="container px-4 px-lg-5">
			<a class="navbar-brand fw-bold"
				href="${pageContext.request.contextPath}/"> Used Book
				Marketplace </a>

			<div class="d-flex gap-2">
				<a class="btn btn-outline-dark"
					href="${pageContext.request.contextPath}/books"> Browse Books </a>

				<a class="btn btn-dark"
					href="${pageContext.request.contextPath}/buyer/cart"> Cart </a>
			</div>
		</div>
	</nav>

	<main class="cart-page">
		<div class="container px-4 px-lg-5">

			<div class="d-flex justify-content-between align-items-center mb-4">
				<h1 class="h2 mb-0">Shopping Cart</h1>

				<a class="btn btn-outline-secondary"
					href="${pageContext.request.contextPath}/books"> Continue
					Shopping </a>
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
						<h2 class="h4 mb-3">Your cart is empty</h2>
						<p class="text-muted">Discover your next favorite book.</p>

						<a class="btn btn-dark"
							href="${pageContext.request.contextPath}/books"> Browse Books
						</a>
					</div>
				</c:when>

				<c:otherwise>
					<div class="table-responsive bg-white rounded shadow-sm">
						<table class="table cart-table align-middle mb-0">
							<thead class="table-light">
								<tr>
									<th scope="col">Book</th>
									<th scope="col">Shop</th>
									<th scope="col">Condition</th>
									<th scope="col">Unit Price</th>
									<th scope="col">Quantity</th>
									<th scope="col">Subtotal</th>
									<th scope="col">Action</th>
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
															src="<c:out value='${item.coverUrl}'/>" alt="Book cover">
													</c:when>
													<c:otherwise>
														<div
															class="cart-cover d-flex
                                                            align-items-center
                                                            justify-content-center">
															<span class="text-muted small"> No image </span>
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
												type="number" groupingUsed="true" /> VND</td>

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
													aria-label="Quantity">

												<button type="submit" class="btn btn-sm btn-outline-primary">
													Update</button>
											</form> <small class="text-muted"> Available: <c:out
													value="${item.stockQuantity}" />
										</small>
										</td>

										<td class="fw-semibold"><fmt:formatNumber
												value="${item.lineTotal}" type="number" groupingUsed="true" />
											VND</td>

										<td>
											<form method="post"
												action="${pageContext.request.contextPath}/buyer/cart/remove"
												onsubmit="return confirm('Remove this book from your cart?');">

												<input type="hidden" name="${_csrf.parameterName}"
													value="${_csrf.token}"> <input type="hidden"
													name="cartItemId" value="${item.cartItemId}">

												<button type="submit" class="btn btn-sm btn-outline-danger">
													Remove</button>
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
									<div class="d-flex justify-content-between mb-3">
										<span>Subtotal</span> <span class="cart-total"> <fmt:formatNumber
												value="${cartTotal}" type="number" groupingUsed="true" />
											VND
										</span>
									</div>

									<p class="text-muted small">Shipping fees and discounts
										will be calculated during checkout.</p>

									<button type="button" class="btn btn-dark w-100" disabled
										title="Checkout will be implemented in the next step">
										Proceed to Checkout</button>
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
			<p class="m-0 text-center text-white">Copyright &copy; Used Book
				Marketplace</p>
		</div>
	</footer>

	<script
		src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
