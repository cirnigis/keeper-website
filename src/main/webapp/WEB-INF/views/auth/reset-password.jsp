<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Reset Password - Used Book Marketplace</title>
<link
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
	rel="stylesheet">
<link href="${pageContext.request.contextPath}/css/style.css"
	rel="stylesheet">
</head>
<body class="auth-page">
	<div class="container py-5">
		<div class="card shadow-sm mx-auto" style="max-width: 480px;">
			<div class="card-body p-4 p-md-5">
				<h1 class="h3 mb-2">Reset Password</h1>
				<p class="text-muted mb-4">Enter the six-digit code sent to your
					email and choose a new password.</p>

				<%
				if (request.getAttribute("successMessage") != null) {
				%>
				<div class="alert alert-success">
					<%=org.springframework.web.util.HtmlUtils.htmlEscape(String.valueOf(request.getAttribute("successMessage")))%>
				</div>
				<%
				}
				%>

				<%
				if (request.getAttribute("errorMessage") != null) {
				%>
				<div class="alert alert-danger">
					<%=org.springframework.web.util.HtmlUtils.htmlEscape(String.valueOf(request.getAttribute("errorMessage")))%>
				</div>
				<%
				}
				%>

				<form
					action="${pageContext.request.contextPath}/auth/reset-password"
					method="post">
					<input type="hidden" name="${_csrf.parameterName}"
						value="${_csrf.token}">

					<div class="mb-3">
						<label for="email" class="form-label">Email address</label> <input
							type="email" id="email" name="email" class="form-control"
							maxlength="254"
							value="<%=org.springframework.web.util.HtmlUtils.htmlEscape(
		String.valueOf(request.getAttribute("resetEmail") == null ? "" : request.getAttribute("resetEmail")))%>"
							autocomplete="email" required>
					</div>

					<div class="mb-3">
						<label for="otp" class="form-label">Reset Code</label> <input
							type="text" id="otp" name="otp" class="form-control"
							inputmode="numeric" pattern="[0-9]{6}" minlength="6"
							maxlength="6" autocomplete="one-time-code" required>
						<div class="form-text">The code expires after 5 minutes.</div>
					</div>

					<div class="mb-3">
						<label for="newPassword" class="form-label">New Password</label> <input
							type="password" id="newPassword" name="newPassword"
							class="form-control" minlength="8" autocomplete="new-password"
							required>
					</div>

					<div class="mb-4">
						<label for="confirmPassword" class="form-label">Confirm
							New Password</label> <input type="password" id="confirmPassword"
							name="confirmPassword" class="form-control" minlength="8"
							autocomplete="new-password" required>
					</div>

					<button type="submit" class="btn btn-primary w-100">Reset
						Password</button>
				</form>

				<div class="text-center mt-4">
					<a href="${pageContext.request.contextPath}/auth/forgot-password">Request
						Another Code</a> <span class="mx-2">|</span> <a
						href="${pageContext.request.contextPath}/auth/login">Sign In</a>
				</div>
			</div>
		</div>
	</div>
</body>
</html>
