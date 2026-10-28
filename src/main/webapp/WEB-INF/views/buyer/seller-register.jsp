
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>

<!DOCTYPE html>
<html lang="vi">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Đăng ký trở thành người bán - Keeper</title>

<style>
* {
	box-sizing: border-box;
}

body {
	margin: 0;
	font-family: Arial, Helvetica, sans-serif;
	background: #f5f7fb;
	color: #263238;
}

.page-container {
	width: min(900px, calc(100% - 32px));
	margin: 36px auto;
}

.form-card {
	background: #fff;
	padding: 32px;
	border-radius: 14px;
	box-shadow: 0 4px 20px rgba(0, 0, 0, 0.07);
}

.page-title {
	margin: 0 0 8px;
	font-size: 28px;
	color: #173b57;
}

.page-description {
	margin: 0 0 26px;
	color: #64748b;
	line-height: 1.6;
}

.form-grid {
	display: grid;
	grid-template-columns: repeat(2, minmax(0, 1fr));
	gap: 20px;
}

.form-group {
	display: flex;
	flex-direction: column;
	min-width: 0;
}

.form-group.full-width {
	grid-column: 1/-1;
}

.form-group label {
	margin-bottom: 8px;
	font-size: 14px;
	font-weight: 600;
	color: #334155;
}

.required {
	color: #e53935;
	font-weight: 700;
	margin-left: 3px;
}

.form-control {
	width: 100%;
	min-height: 44px;
	padding: 11px 12px;
	border: 1px solid #cbd5e1;
	border-radius: 8px;
	background: #fff;
	color: #1e293b;
	font-size: 14px;
	outline: none;
	transition: border-color 0.2s, box-shadow 0.2s;
}

.form-control:focus {
	border-color: #2878b5;
	box-shadow: 0 0 0 3px rgba(40, 120, 181, 0.12);
}

textarea.form-control {
	min-height: 110px;
	resize: vertical;
}

.field-hint {
	margin-top: 6px;
	font-size: 12px;
	line-height: 1.5;
	color: #64748b;
}

.alert {
	margin-bottom: 20px;
	padding: 12px 14px;
	border-radius: 8px;
	font-size: 14px;
	line-height: 1.5;
}

.alert-error {
	color: #b42318;
	background: #fef3f2;
	border: 1px solid #fecdca;
}

.alert-success {
	color: #067647;
	background: #ecfdf3;
	border: 1px solid #abefc6;
}

.form-actions {
	display: flex;
	justify-content: flex-end;
	gap: 12px;
	margin-top: 28px;
	flex-wrap: wrap;
}

.btn {
	display: inline-flex;
	align-items: center;
	justify-content: center;
	min-height: 44px;
	padding: 11px 20px;
	border: none;
	border-radius: 8px;
	font-size: 14px;
	font-weight: 600;
	text-decoration: none;
	cursor: pointer;
}

.btn-primary {
	color: #fff;
	background: #1769aa;
}

.btn-primary:hover {
	background: #12558a;
}

.btn-secondary {
	color: #334155;
	background: #e2e8f0;
}

.btn-secondary:hover {
	background: #cbd5e1;
}

@media ( max-width : 640px) {
	.page-container {
		width: calc(100% - 20px);
		margin: 16px auto;
	}
	.form-card {
		padding: 20px 16px;
	}
	.page-title {
		font-size: 23px;
	}
	.form-grid {
		grid-template-columns: 1fr;
		gap: 16px;
	}
	.form-group.full-width {
		grid-column: auto;
	}
	.form-actions {
		flex-direction: column-reverse;
	}
	.btn {
		width: 100%;
	}
}
</style>
</head>

<body>
	<div class="page-container">
		<div class="form-card">

			<h1 class="page-title">Đăng ký mở shop</h1>
			<p class="page-description">Điền thông tin CCCD và cửa hàng để
				gửi yêu cầu trở thành người bán. Shop sẽ ở trạng thái chờ quản trị
				viên xét duyệt sau khi đăng ký.</p>

			<c:if test="${param.status == 'apply'}">
				<div class="alert alert-info" role="status">Tài khoản hiện
					chưa có vai trò Người bán. Bạn có thể gửi yêu cầu mở shop; vai trò
					Người bán chỉ được cấp sau khi shop được duyệt.</div>
			</c:if>

			<c:if test="${not empty errorMessage}">
				<div class="alert alert-error" role="alert">
					<c:out value="${errorMessage}" />
				</div>
			</c:if>

			<c:if test="${not empty successMessage}">
				<div class="alert alert-success" role="status">
					<c:out value="${successMessage}" />
				</div>
			</c:if>

			<form id="shopRegisterForm"
				action="${pageContext.request.contextPath}/buyer/seller-register"
				method="post" novalidate>

				<input type="hidden" name="${_csrf.parameterName}"
					value="${_csrf.token}">

				<div class="form-grid">

					<!-- CCCD -->
					<div class="form-group full-width">
						<label for="cccd"> Số CCCD<span class="required">*</span>
						</label> <input type="text" id="cccd" name="cccd" class="form-control"
							placeholder="Nhập số CCCD gồm 12 chữ số" minlength="12"
							maxlength="12" pattern="[0-9]{12}" inputmode="numeric"
							autocomplete="off" required> <small class="field-hint">
							CCCD phải gồm đúng 12 chữ số. CCCD được lưu vào tài khoản của
							bạn. </small>
					</div>

					<!-- Tên shop -->
					<div class="form-group">
						<label for="shopName"> Tên shop<span class="required">*</span>
						</label> <input type="text" id="shopName" name="shopName"
							class="form-control" maxlength="160" placeholder="Nhập tên shop"
							required>
					</div>

					<!-- Đường dẫn shop -->
					<div class="form-group">
						<label for="shopSlug"> Đường dẫn shop<span
							class="required">*</span>
						</label> <input type="text" id="shopSlug" name="shopSlug"
							class="form-control" maxlength="180"
							placeholder="Ví dụ: sach-cu-cua-toi"
							pattern="[a-z0-9]+(-[a-z0-9]+)*" required> <small
							class="field-hint"> Chỉ dùng chữ thường không dấu, số và
							dấu gạch ngang. </small>
					</div>

					<!-- Loại chủ shop -->
					<div class="form-group">
						<label for="ownerType"> Loại chủ shop<span
							class="required">*</span>
						</label> <select id="ownerType" name="ownerType" class="form-control"
							required>
							<option value="">-- Chọn loại chủ shop --</option>
							<option value="INDIVIDUAL">Cá nhân</option>
							<option value="BUSINESS">Doanh nghiệp</option>
						</select>
					</div>

					<!-- Số điện thoại -->
					<div class="form-group">
						<label for="contactPhone"> Số điện thoại<span
							class="required">*</span>
						</label> <input type="tel" id="contactPhone" name="contactPhone"
							class="form-control" maxlength="30" autocomplete="tel"
							placeholder="Nhập số điện thoại liên hệ" required>
					</div>

					<!-- Email liên hệ -->
					<div class="form-group full-width">
						<label for="contactEmail"> Email liên hệ<span
							class="required">*</span>
						</label> <input type="email" id="contactEmail" name="contactEmail"
							class="form-control" maxlength="254" autocomplete="email"
							placeholder="example@email.com" required>
					</div>

					<!-- Tỉnh/Thành phố -->
					<div class="form-group">
						<label for="province"> Tỉnh/Thành phố<span
							class="required">*</span>
						</label> <input type="text" id="province" name="province"
							class="form-control" maxlength="100"
							placeholder="Nhập tỉnh/thành phố" required>
					</div>

					<!-- Quận/Huyện -->
					<div class="form-group">
						<label for="district"> Quận/Huyện<span class="required">*</span>
						</label> <input type="text" id="district" name="district"
							class="form-control" maxlength="100"
							placeholder="Nhập quận/huyện" required>
					</div>

					<!-- Địa chỉ -->
					<div class="form-group full-width">
						<label for="addressLine"> Địa chỉ cụ thể<span
							class="required">*</span>
						</label> <input type="text" id="addressLine" name="addressLine"
							class="form-control" maxlength="300"
							autocomplete="street-address" placeholder="Số nhà, tên đường..."
							required>
					</div>

					<!-- Mô tả -->
					<div class="form-group full-width">
						<label for="description">Mô tả shop</label>
						<textarea id="description" name="description" class="form-control"
							maxlength="2000"
							placeholder="Giới thiệu về shop của bạn (không bắt buộc)"></textarea>
					</div>

				</div>

				<div class="form-actions">
					<a href="${pageContext.request.contextPath}/buyer/dashboard"
						class="btn btn-secondary"> Hủy </a>

					<button type="submit" class="btn btn-primary">Gửi yêu cầu
						đăng ký người bán</button>
				</div>
			</form>
		</div>
	</div>

	<script>
    (function () {
        const form = document.getElementById("shopRegisterForm");
        const cccdInput = document.getElementById("cccd");
        const shopNameInput = document.getElementById("shopName");
        const shopSlugInput = document.getElementById("shopSlug");

        // Chỉ cho phép nhập chữ số trong trường CCCD.
        cccdInput.addEventListener("input", function () {
            this.value = this.value.replace(/[^0-9]/g, "").slice(0, 12);
        });

        // Tự tạo slug từ tên shop nếu người dùng chưa chỉnh slug.
        let slugManuallyEdited = false;

        shopSlugInput.addEventListener("input", function () {
            slugManuallyEdited = true;
        });

        shopNameInput.addEventListener("input", function () {
            if (slugManuallyEdited) {
                return;
            }

            shopSlugInput.value = this.value
                .normalize("NFD")
                .replace(/[\u0300-\u036f]/g, "")
                .replace(/đ/g, "d")
                .replace(/Đ/g, "d")
                .toLowerCase()
                .trim()
                .replace(/[^a-z0-9]+/g, "-")
                .replace(/^-+|-+$/g, "");
        });

        form.addEventListener("submit", function (event) {
            const requiredFields = [
                { id: "cccd", label: "Số CCCD" },
                { id: "shopName", label: "Tên shop" },
                { id: "shopSlug", label: "Đường dẫn shop" },
                { id: "ownerType", label: "Loại chủ shop" },
                { id: "contactPhone", label: "Số điện thoại" },
                { id: "contactEmail", label: "Email liên hệ" },
                { id: "province", label: "Tỉnh/Thành phố" },
                { id: "district", label: "Quận/Huyện" },
                { id: "addressLine", label: "Địa chỉ cụ thể" }
            ];

            for (const field of requiredFields) {
                const input = document.getElementById(field.id);

                if (!input.value.trim()) {
                    event.preventDefault();
                    alert("Vui lòng nhập " + field.label + ".");
                    input.focus();
                    return;
                }
            }

            if (!/^[0-9]{12}$/.test(cccdInput.value.trim())) {
                event.preventDefault();
                alert("CCCD phải gồm đúng 12 chữ số.");
                cccdInput.focus();
                return;
            }

            if (!shopSlugInput.checkValidity()) {
                event.preventDefault();
                alert(
                    "Đường dẫn shop chỉ được chứa chữ thường không dấu, "
                    + "số và dấu gạch ngang; không được bắt đầu hoặc "
                    + "kết thúc bằng dấu gạch ngang."
                );
                shopSlugInput.focus();
                return;
            }

            const emailInput = document.getElementById("contactEmail");

            if (!emailInput.checkValidity()) {
                event.preventDefault();
                alert("Vui lòng nhập email liên hệ hợp lệ.");
                emailInput.focus();
                return;
            }
        });
    })();
</script>
</body>
</html>
