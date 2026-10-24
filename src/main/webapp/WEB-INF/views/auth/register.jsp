<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>

<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">

<title>Create Account | Keeper</title>

<link
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
	rel="stylesheet">

<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/style.css">
</head>

<body class="auth-page">

	<div class="auth-card">

		<!-- Keeper Brand -->
		<a class="brand-name keeper-logo"
			href="${pageContext.request.contextPath}/"
			aria-label="Keeper Homepage"> <span class="keeper-k">K</span><span
			class="keeper-rest">eeper</span>
		</a>

		<h1>Create your account</h1>

		<p class="text-muted">Join our community of book lovers.</p>

		<!-- Error Message -->
		<c:if test="${not empty errorMessage}">
			<div class="alert alert-danger" role="alert">
				<c:out value="${errorMessage}" />
			</div>
		</c:if>

		<!-- Success Message -->
		<c:if test="${not empty successMessage}">
			<div class="alert alert-success" role="alert">
				<c:out value="${successMessage}" />
			</div>
		</c:if>

		<!-- Registration Form -->
		<form action="${pageContext.request.contextPath}/auth/register"
			method="post">

			<input type="hidden" name="${_csrf.parameterName}"
				value="${_csrf.token}">

			<div class="mb-3">
				<label for="fullName" class="form-label"> Full name </label> <input
					type="text" class="form-control" id="fullName" name="fullName"
					maxlength="150"
					value="<c:out value='${registerRequest.fullName}'/>"
					autocomplete="name" placeholder="Enter your full name" required>
			</div>

			<div class="mb-3">
				<label for="email" class="form-label"> Email address </label> <input
					type="email" class="form-control" id="email" name="email"
					maxlength="254" value="<c:out value='${registerRequest.email}'/>"
					autocomplete="email" placeholder="you@example.com" required>
			</div>

			<div class="mb-3">
				<label for="password" class="form-label"> Password </label> <input
					type="password" class="form-control" id="password" name="password"
					minlength="8" autocomplete="new-password"
					placeholder="At least 8 characters" required>
			</div>

			<div class="mb-4">
				<label for="confirmPassword" class="form-label"> Confirm
					password </label> <input type="password" class="form-control"
					id="confirmPassword" name="confirmPassword" minlength="8"
					autocomplete="new-password" placeholder="Re-enter your password"
					required>
			</div>

			<button type="submit" class="btn btn-primary w-100">Create
				Account</button>

		</form>

		<!-- Authentication Footer -->
		<p class="auth-footer">
			Already have an account? <a
				href="${pageContext.request.contextPath}/auth/login"> Sign in </a>
		</p>

		<a class="back-home" href="${pageContext.request.contextPath}/">
			Back to homepage </a>

	</div>

</body>
</html>

