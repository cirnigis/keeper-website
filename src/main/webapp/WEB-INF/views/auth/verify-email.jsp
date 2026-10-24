
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>

<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">

<title>Verify Email | Keeper</title>

<link
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
	rel="stylesheet">

<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/style.css">
</head>

<body class="auth-page">

	<div class="auth-card">

		<a class="brand-name keeper-logo"
			href="${pageContext.request.contextPath}/"
			aria-label="Keeper Homepage"> <span class="keeper-k">K</span><span
			class="keeper-rest">eeper</span>
		</a>

		<div class="verification-icon">✉</div>

		<h1>Verify your email</h1>

		<p class="text-muted">Enter the six-digit verification code sent
			to your email address.</p>

		<c:if test="${not empty errorMessage}">
			<div class="alert alert-danger" role="alert">
				<c:out value="${errorMessage}" />
			</div>
		</c:if>

		<c:if test="${not empty successMessage}">
			<div class="alert alert-success" role="alert">
				<c:out value="${successMessage}" />
			</div>
		</c:if>

		<form action="${pageContext.request.contextPath}/auth/verify-email"
			method="post">

			<input type="hidden" name="${_csrf.parameterName}"
				value="${_csrf.token}">

			<div class="mb-3">
				<label for="email" class="form-label">Email address</label> <input
					type="email" class="form-control" id="email" name="email"
					maxlength="254" value="<c:out value='${verifyRequest.email}'/>"
					placeholder="you@example.com" autocomplete="email" required>
			</div>

			<div class="mb-4">
				<label for="otp" class="form-label">Verification code</label> <input
					type="text" class="form-control otp-input" id="otp" name="otp"
					inputmode="numeric" pattern="[0-9]{6}" minlength="6" maxlength="6"
					placeholder="000000" autocomplete="one-time-code" required>
			</div>

			<button type="submit" class="btn btn-primary w-100">Verify
				Email</button>
		</form>

		<p class="auth-footer">
			Already verified? <a
				href="${pageContext.request.contextPath}/auth/login"> Sign in </a>
		</p>

	</div>

</body>
</html>
