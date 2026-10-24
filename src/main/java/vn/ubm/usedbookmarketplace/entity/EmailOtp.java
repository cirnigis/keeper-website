package vn.ubm.usedbookmarketplace.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "EmailOTPs", schema = "dbo")
public class EmailOtp {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "EmailOTPId")
	private Long emailOtpId;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "UserId", referencedColumnName = "UserId", nullable = false)
	private AppUser user;

	@Column(name = "Email", nullable = false, length = 254)
	private String email;

	@Column(name = "Purpose", nullable = false, length = 25)
	private String purpose;

	@Column(name = "OTPHash", nullable = false, length = 255)
	private String otpHash;

	@Column(name = "ExpiresAt", nullable = false)
	private LocalDateTime expiresAt;

	@Column(name = "ConsumedAt")
	private LocalDateTime consumedAt;

	@Column(name = "FailedAttempts", nullable = false)
	private Integer failedAttempts = 0;

	@Column(name = "CreatedAt", insertable = false, updatable = false)
	private LocalDateTime createdAt;

	public EmailOtp() {
	}

	public Long getEmailOtpId() {
		return emailOtpId;
	}

	public AppUser getUser() {
		return user;
	}

	public void setUser(AppUser user) {
		this.user = user;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPurpose() {
		return purpose;
	}

	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}

	public String getOtpHash() {
		return otpHash;
	}

	public void setOtpHash(String otpHash) {
		this.otpHash = otpHash;
	}

	public LocalDateTime getExpiresAt() {
		return expiresAt;
	}

	public void setExpiresAt(LocalDateTime expiresAt) {
		this.expiresAt = expiresAt;
	}

	public LocalDateTime getConsumedAt() {
		return consumedAt;
	}

	public void setConsumedAt(LocalDateTime consumedAt) {
		this.consumedAt = consumedAt;
	}

	public Integer getFailedAttempts() {
		return failedAttempts;
	}

	public void setFailedAttempts(Integer failedAttempts) {
		this.failedAttempts = failedAttempts;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
}