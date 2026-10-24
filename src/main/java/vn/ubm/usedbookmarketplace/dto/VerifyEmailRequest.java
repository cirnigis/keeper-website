package vn.ubm.usedbookmarketplace.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class VerifyEmailRequest {

	@NotBlank(message = "Vui lòng nhập email.")
	@Email(message = "Email không hợp lệ.")
	private String email;

	@NotBlank(message = "Vui lòng nhập mã OTP.")
	@Pattern(regexp = "\\d{6}", message = "Mã OTP phải gồm 6 chữ số.")
	private String otp;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getOtp() {
		return otp;
	}

	public void setOtp(String otp) {
		this.otp = otp;
	}
}