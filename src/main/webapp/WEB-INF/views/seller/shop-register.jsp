
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Đăng ký shop | Keeper</title>
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
	<div class="seller-page">
		<header class="seller-header">
			<div>
				<a class="seller-brand" href="${pageContext.request.contextPath}/">Keeper</a>
				<p class="seller-subtitle">Khu vực người bán</p>
			</div>
			<nav class="seller-nav">
				<a class="seller-nav-link"
					href="${pageContext.request.contextPath}/seller/shops">Shop của
					tôi</a> <a class="seller-nav-link"
					href="${pageContext.request.contextPath}/seller/listings">Sản
					phẩm</a> <a class="seller-nav-link"
					href="${pageContext.request.contextPath}/seller/orders">Đơn
					hàng</a>
			</nav>
		</header>

		<main class="seller-main seller-main-narrow">
			<div class="seller-page-heading">
				<div>
					<p class="seller-eyebrow">SELLER CENTER</p>
					<h1>Đăng ký shop mới</h1>
					<p class="seller-muted">Điền thông tin để gửi shop đến quản trị
						viên xét duyệt.</p>
				</div>
			</div>

			<c:if test="${not empty errorMessage}">
				<div class="seller-alert seller-alert-error">
					<c:out value="${errorMessage}" />
				</div>
			</c:if>

			<section class="seller-panel">
				<form method="post"
					action="${pageContext.request.contextPath}/seller/shops/create"
					class="seller-form">
					<input type="hidden" name="${_csrf.parameterName}"
						value="${_csrf.token}">

					<div class="seller-form-grid">
						<div class="seller-field seller-field-full">
							<label for="shopName">Tên shop *</label> <input id="shopName"
								name="shopName" type="text" maxlength="160" required>
						</div>

						<div class="seller-field seller-field-full">
							<label for="shopSlug">Đường dẫn shop *</label> <input
								id="shopSlug" name="shopSlug" type="text" maxlength="180"
								pattern="[a-z0-9]+(-[a-z0-9]+)*" placeholder="goc-sach-cu"
								required> <small>Chỉ sử dụng chữ thường không
								dấu, chữ số và dấu gạch ngang.</small>
						</div>

						<div class="seller-field">
							<label for="ownerType">Loại hình *</label> <select id="ownerType"
								name="ownerType" required>
								<option value="INDIVIDUAL">Cá nhân</option>
								<option value="BUSINESS">Doanh nghiệp</option>
							</select>
						</div>

						<div class="seller-field">
							<label for="contactPhone">Số điện thoại</label> <input
								id="contactPhone" name="contactPhone" type="tel" maxlength="30">
						</div>

						<div class="seller-field seller-field-full">
							<label for="contactEmail">Email liên hệ</label> <input
								id="contactEmail" name="contactEmail" type="email"
								maxlength="254">
						</div>

						<div class="seller-field seller-field-full">
							<label for="description">Mô tả shop</label>
							<textarea id="description" name="description" maxlength="2000"
								rows="4"></textarea>
						</div>

						<div class="seller-field">
							<label for="province">Tỉnh/Thành phố *</label> <input
								id="province" name="province" type="text" maxlength="100"
								required>
						</div>

						<div class="seller-field">
							<label for="district">Quận/Huyện *</label> <input id="district"
								name="district" type="text" maxlength="100" required>
						</div>

						<div class="seller-field seller-field-full">
							<label for="addressLine">Địa chỉ cụ thể *</label> <input
								id="addressLine" name="addressLine" type="text" maxlength="300"
								required>
						</div>
					</div>

					<div class="seller-form-note">Shop mới sẽ ở trạng thái chờ
						duyệt. Bạn chỉ có thể đăng bán khi shop được duyệt và đang hoạt
						động.</div>

					<div class="seller-form-actions">
						<a class="seller-button seller-button-outline"
							href="${pageContext.request.contextPath}/seller/shops">Quay
							lại</a>
						<button class="seller-button seller-button-primary" type="submit">Gửi
							đăng ký</button>
					</div>
				</form>
			</section>
		</main>
	</div>

	<script>
    const shopNameInput = document.getElementById("shopName");
    const shopSlugInput = document.getElementById("shopSlug");
    let slugEdited = false;

    shopSlugInput.addEventListener("input", () => {
        slugEdited = true;
    });

    shopNameInput.addEventListener("input", () => {
        if (slugEdited) return;
        shopSlugInput.value = shopNameInput.value
            .normalize("NFD")
            .replace(/[\u0300-\u036f]/g, "")
            .replace(/đ/g, "d")
            .replace(/Đ/g, "d")
            .toLowerCase()
            .replace(/[^a-z0-9]+/g, "-")
            .replace(/^-+|-+$/g, "");
    });
</script>
</body>
</html>
