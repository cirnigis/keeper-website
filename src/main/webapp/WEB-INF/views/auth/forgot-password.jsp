<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Forgot Password - Used Book Marketplace</title>
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
				<h1 class="h3 mb-2">Forgot Password</h1>
				<p class="text-muted mb-4">Enter your registered email address
					to receive a password reset code.</p>

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
					action="${pageContext.request.contextPath}/auth/forgot-password"
					method="post">
					<input type="hidden" name="${_csrf.parameterName}"
						value="${_csrf.token}">

					<div class="mb-3">
						<label for="email" class="form-label">Email address</label> <input
							type="email" id="email" name="email" class="form-control"
							maxlength="254" autocomplete="email" required>
					</div>

					<button type="submit" class="btn btn-primary w-100">Send
						Reset Code</button>
				</form>

				<div class="text-center mt-4">
					<a href="${pageContext.request.contextPath}/auth/login">Back to
						Sign In</a>
				</div>
			</div>
		</div>
	</div>
</body>
</html>
