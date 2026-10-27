<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html lang="vi">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">

<title>Đăng ký thành công | Keeper</title>

<link
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
	rel="stylesheet">

<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/style.css">
</head>

<body>

	<main class="registration-success-page">
		<div class="registration-success-card">

			<!-- Logo Keeper -->
			<a class="keeper-brand" href="${pageContext.request.contextPath}/"
				aria-label="Keeper - Trang chủ"> <span class="keeper-brand-icon">K</span>
				<span class="keeper-brand-text">eeper</span>
			</a>

			<!-- Biểu tượng thành công -->
			<div class="success-icon" aria-hidden="true">&#10003;</div>

			<h1>Đăng ký thành công!</h1>

			<p>Email của bạn đã được xác minh thành công. Bây giờ bạn có thể
				đăng nhập vào tài khoản Keeper.</p>

			<p class="redirect-note">
				Tự động chuyển đến trang đăng nhập sau <strong id="countdown"
					aria-live="polite">5</strong> giây...
			</p>

			<a href="${pageContext.request.contextPath}/auth/login"
				class="btn btn-primary"> Đăng nhập ngay </a>

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

