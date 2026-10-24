package vn.ubm.usedbookmarketplace.service;

import java.security.SecureRandom;
import java.util.Locale;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PasswordRecoveryService {

	private static final String RESET_PURPOSE = "RESET_PASSWORD";
	private static final int OTP_EXPIRY_MINUTES = 5;
	private static final int MAX_FAILED_ATTEMPTS = 5;

	private final JdbcTemplate jdbcTemplate;
	private final JavaMailSender mailSender;
	private final PasswordEncoder passwordEncoder;
	private final SecureRandom secureRandom = new SecureRandom();

	public PasswordRecoveryService(JdbcTemplate jdbcTemplate, JavaMailSender mailSender,
			PasswordEncoder passwordEncoder) {
		this.jdbcTemplate = jdbcTemplate;
		this.mailSender = mailSender;
		this.passwordEncoder = passwordEncoder;
	}

	public void requestPasswordReset(String inputEmail) {
		String email = normalizeEmail(inputEmail);

		var users = jdbcTemplate.query("""
				SELECT UserId, Email, FullName
				FROM dbo.Users
				WHERE LOWER(Email) = LOWER(?)
				  AND AccountStatus = 'ACTIVE'
				  AND EmailVerifiedAt IS NOT NULL
				""",
				(rs, rowNum) -> new UserMailInfo(rs.getLong("UserId"), rs.getString("Email"), rs.getString("FullName")),
				email);

		// Do not reveal whether the email belongs to an account.
		if (users.isEmpty()) {
			return;
		}

		UserMailInfo user = users.get(0);
		String otp = String.format("%06d", secureRandom.nextInt(1_000_000));
		String otpHash = passwordEncoder.encode(otp);

		jdbcTemplate.update("""
				UPDATE dbo.EmailOTPs
				SET ConsumedAt = SYSUTCDATETIME()
				WHERE Email = ?
				  AND Purpose = ?
				  AND ConsumedAt IS NULL
				""", user.email(), RESET_PURPOSE);

		jdbcTemplate.update("""
				INSERT INTO dbo.EmailOTPs
				    (UserId, Email, Purpose, OTPHash, ExpiresAt,
				     FailedAttempts, CreatedAt)
				VALUES
				    (?, ?, ?, ?, DATEADD(MINUTE, ?, SYSUTCDATETIME()),
				     0, SYSUTCDATETIME())
				""", user.userId(), user.email(), RESET_PURPOSE, otpHash, OTP_EXPIRY_MINUTES);

		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(user.email());
		message.setSubject("Used Book Marketplace - Password Reset Code");
		message.setText("Hello " + user.fullName() + ",\n\n" + "Your password reset code is: " + otp + "\n\n"
				+ "This code expires in " + OTP_EXPIRY_MINUTES + " minutes and can only be used once.\n"
				+ "If you did not request a password reset, you can ignore this email.");

		mailSender.send(message);
	}

	@Transactional
	public boolean resetPassword(String inputEmail, String otp, String newPassword) {

		String email = normalizeEmail(inputEmail);

		if (otp == null || !otp.matches("\\d{6}")) {
			return false;
		}

		var otpRows = jdbcTemplate.query("""
				SELECT TOP 1 EmailOTPId, OTPHash, FailedAttempts
				FROM dbo.EmailOTPs
				WHERE LOWER(Email) = LOWER(?)
				  AND Purpose = ?
				  AND ConsumedAt IS NULL
				  AND ExpiresAt > SYSUTCDATETIME()
				ORDER BY CreatedAt DESC, EmailOTPId DESC
				""", (rs, rowNum) -> new OtpInfo(rs.getLong("EmailOTPId"), rs.getString("OTPHash"),
				rs.getInt("FailedAttempts")), email, RESET_PURPOSE);

		if (otpRows.isEmpty()) {
			return false;
		}

		OtpInfo otpInfo = otpRows.get(0);

		if (otpInfo.failedAttempts() >= MAX_FAILED_ATTEMPTS) {
			jdbcTemplate.update("""
					UPDATE dbo.EmailOTPs
					SET ConsumedAt = SYSUTCDATETIME()
					WHERE EmailOTPId = ?
					""", otpInfo.id());
			return false;
		}

		if (!passwordEncoder.matches(otp, otpInfo.otpHash())) {
			jdbcTemplate.update("""
					UPDATE dbo.EmailOTPs
					SET FailedAttempts = FailedAttempts + 1,
					    ConsumedAt = CASE
					        WHEN FailedAttempts + 1 >= ?
					        THEN SYSUTCDATETIME()
					        ELSE ConsumedAt
					    END
					WHERE EmailOTPId = ?
					  AND ConsumedAt IS NULL
					""", MAX_FAILED_ATTEMPTS, otpInfo.id());
			return false;
		}

		int updatedUsers = jdbcTemplate.update("""
				UPDATE dbo.Users
				SET PasswordHash = ?,
				    UpdatedAt = SYSUTCDATETIME()
				WHERE LOWER(Email) = LOWER(?)
				  AND AccountStatus = 'ACTIVE'
				  AND EmailVerifiedAt IS NOT NULL
				""", passwordEncoder.encode(newPassword), email);

		if (updatedUsers != 1) {
			return false;
		}

		jdbcTemplate.update("""
				UPDATE dbo.EmailOTPs
				SET ConsumedAt = SYSUTCDATETIME()
				WHERE Email = ?
				  AND Purpose = ?
				  AND ConsumedAt IS NULL
				""", email, RESET_PURPOSE);

		return true;
	}

	public void changePassword(String email, String currentPassword, String newPassword) {

		var rows = jdbcTemplate.query("""
				SELECT PasswordHash
				FROM dbo.Users
				WHERE LOWER(Email) = LOWER(?)
				  AND AccountStatus = 'ACTIVE'
				""", (rs, rowNum) -> rs.getString("PasswordHash"), normalizeEmail(email));

		if (rows.isEmpty() || !passwordEncoder.matches(currentPassword, rows.get(0))) {
			throw new IllegalArgumentException("The current password is incorrect.");
		}

		jdbcTemplate.update("""
				UPDATE dbo.Users
				SET PasswordHash = ?,
				    UpdatedAt = SYSUTCDATETIME()
				WHERE LOWER(Email) = LOWER(?)
				  AND AccountStatus = 'ACTIVE'
				""", passwordEncoder.encode(newPassword), normalizeEmail(email));
	}

	private String normalizeEmail(String email) {
		if (email == null || email.isBlank()) {
			throw new IllegalArgumentException("Email is required.");
		}
		return email.trim().toLowerCase(Locale.ROOT);
	}

	private record UserMailInfo(long userId, String email, String fullName) {
	}

	private record OtpInfo(long id, String otpHash, int failedAttempts) {
	}
}
