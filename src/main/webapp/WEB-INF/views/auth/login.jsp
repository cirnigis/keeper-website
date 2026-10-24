
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>

<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Sign In | UsedBook Marketplace</title>

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

		<h1>Welcome back</h1>
		<p class="text-muted">Sign in to continue to your account.</p>

		<c:if test="${param.error != null}">
			<div class="alert alert-danger" role="alert">Invalid email or
				password, or your account is not active.</div>
		</c:if>

		<c:if test="${param.logout != null}">
			<div class="alert alert-success" role="alert">You have been
				signed out successfully.</div>
		</c:if>

		<c:if test="${not empty message}">
			<div class="alert alert-success" role="alert">${message}</div>
		</c:if>

		<c:if test="${not empty error}">
			<div class="alert alert-danger" role="alert">${error}</div>
		</c:if>

		<form action="${pageContext.request.contextPath}/auth/login"
			method="post">

			<input type="hidden" name="${_csrf.parameterName}"
				value="${_csrf.token}">

			<div class="mb-3">
				<label for="email" class="form-label">Email address</label> <input
					type="email" class="form-control" id="email" name="email"
					autocomplete="username" placeholder="you@example.com" required>
			</div>

			<div class="mb-3">
				<label for="password" class="form-label">Password</label> <input
					type="password" class="form-control" id="password" name="password"
					autocomplete="current-password" placeholder="Enter your password"
					required>
			</div>

			<div class="forgot-password-container">
				<a href="${pageContext.request.contextPath}/auth/forgot-password">
					Forgot Password? </a>
			</div>

			<button type="submit" class="btn btn-primary w-100">Sign In
			</button>
		</form>

		<p class="auth-footer">
			Don't have an account? <a
				href="${pageContext.request.contextPath}/auth/register"> Create
				an account </a>
		</p>

		<a class="back-home" href="${pageContext.request.contextPath}/">
			Back to homepage </a>
	</div>



</body>
</html>
