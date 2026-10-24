
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Registration Successful - Keeper</title>

<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
	<main class="registration-success-page">
		<div class="registration-success-card">
			<div class="success-icon">&#10003;</div>

			<h1>Registration Successful!</h1>

			<p>Your email has been verified successfully. You can now log in
				to your Keeper account.</p>

			<p class="redirect-note">
				Redirecting to Login in <strong id="countdown">5</strong> seconds...
			</p>

			<a href="${pageContext.request.contextPath}/auth/login"
				class="btn btn-primary"> Go to Login Now </a>
		</div>
	</main>

	<script>
        let seconds = 5;
        const countdown = document.getElementById("countdown");
        const loginUrl =
            "${pageContext.request.contextPath}/auth/login";

        const timer = setInterval(() => {
            seconds--;
            countdown.textContent = seconds;

            if (seconds <= 0) {
                clearInterval(timer);
                window.location.href = loginUrl;
            }
        }, 1000);
    </script>
</body>
</html>
