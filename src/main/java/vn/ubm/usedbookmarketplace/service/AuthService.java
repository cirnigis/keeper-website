package vn.ubm.usedbookmarketplace.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import vn.ubm.usedbookmarketplace.dto.RegisterRequest;
import vn.ubm.usedbookmarketplace.dto.VerifyEmailRequest;
import vn.ubm.usedbookmarketplace.entity.AppUser;
import vn.ubm.usedbookmarketplace.entity.EmailOtp;
import vn.ubm.usedbookmarketplace.entity.Role;
import vn.ubm.usedbookmarketplace.repository.AppUserRepository;
import vn.ubm.usedbookmarketplace.repository.EmailOtpRepository;
import vn.ubm.usedbookmarketplace.repository.RoleRepository;

@Service
public class AuthService {

	private static final String REGISTER_PURPOSE = "REGISTER";
	private static final int OTP_LIFETIME_MINUTES = 5;
	private static final int MAX_FAILED_ATTEMPTS = 5;

	private final AppUserRepository userRepository;
	private final RoleRepository roleRepository;
	private final EmailOtpRepository otpRepository;
	private final PasswordEncoder passwordEncoder;
	private final JavaMailSender mailSender;
	private final SecureRandom secureRandom = new SecureRandom();

	@Value("${spring.mail.username}")
	private String senderEmail;

	public AuthService(AppUserRepository userRepository, RoleRepository roleRepository,
			EmailOtpRepository otpRepository, PasswordEncoder passwordEncoder, JavaMailSender mailSender) {
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.otpRepository = otpRepository;
		this.passwordEncoder = passwordEncoder;
		this.mailSender = mailSender;
	}

	@Transactional
	public void register(RegisterRequest request) {
		String email = normalizeEmail(request.getEmail());

		if (!request.getPassword().equals(request.getConfirmPassword())) {
			throw new IllegalArgumentException("Mật khẩu xác nhận không khớp.");
		}

		if (userRepository.existsByEmailIgnoreCase(email)) {
			throw new IllegalArgumentException(
					"Email này đã được đăng ký. Vui lòng đăng nhập hoặc sử dụng email khác.");
		}

		Role buyerRole = roleRepository.findByRoleCode("BUYER")
				.orElseThrow(() -> new IllegalStateException("Chưa có vai trò BUYER trong bảng Roles."));

		AppUser user = new AppUser();
		user.setEmail(email);
		user.setFullName(request.getFullName().trim());
		user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
		user.setAccountStatus("PENDING_EMAIL");
		user.getRoles().add(buyerRole);

		try {
			userRepository.saveAndFlush(user);
		} catch (DataIntegrityViolationException ex) {
			throw new IllegalArgumentException("Email này đã được đăng ký. Vui lòng sử dụng email khác.");
		}

		issueRegistrationOtp(user, email);
	}

	@Transactional
	public void verifyEmail(VerifyEmailRequest request) {
		String email = normalizeEmail(request.getEmail());

		AppUser user = userRepository.findByEmailIgnoreCase(email)
				.orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tài khoản cần xác minh."));

		if ("ACTIVE".equals(user.getAccountStatus())) {
			throw new IllegalArgumentException("Email này đã được xác minh.");
		}

		if (!"PENDING_EMAIL".equals(user.getAccountStatus())) {
			throw new IllegalArgumentException("Tài khoản hiện không thể xác minh email.");
		}

		EmailOtp otp = otpRepository.findByEmailIgnoreCaseAndPurposeOrderByCreatedAtDesc(email, REGISTER_PURPOSE)
				.stream().filter(item -> item.getConsumedAt() == null).findFirst()
				.orElseThrow(() -> new IllegalArgumentException("Không tìm thấy mã OTP đang có hiệu lực."));

		if (otp.getFailedAttempts() >= MAX_FAILED_ATTEMPTS) {
			throw new IllegalArgumentException("Mã OTP đã bị khóa do nhập sai quá nhiều lần. Vui lòng yêu cầu mã mới.");
		}

		if (!otp.getExpiresAt().isAfter(LocalDateTime.now())) {
			throw new IllegalArgumentException("Mã OTP đã hết hạn. Vui lòng yêu cầu mã mới.");
		}

		if (!passwordEncoder.matches(request.getOtp(), otp.getOtpHash())) {
			otp.setFailedAttempts(otp.getFailedAttempts() + 1);
			otpRepository.save(otp);
			throw new IllegalArgumentException("Mã OTP không chính xác.");
		}

		otp.setConsumedAt(LocalDateTime.now());
		otpRepository.save(otp);

		user.setAccountStatus("ACTIVE");
		user.setEmailVerifiedAt(LocalDateTime.now());
		userRepository.save(user);
	}

	private void issueRegistrationOtp(AppUser user, String email) {
		String otpCode = String.format(Locale.ROOT, "%06d", secureRandom.nextInt(1_000_000));

		EmailOtp otp = new EmailOtp();
		otp.setUser(user);
		otp.setEmail(email);
		otp.setPurpose(REGISTER_PURPOSE);
		otp.setOtpHash(passwordEncoder.encode(otpCode));
		otp.setExpiresAt(LocalDateTime.now().plusMinutes(OTP_LIFETIME_MINUTES));
		otp.setFailedAttempts(0);

		otpRepository.save(otp);

		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom(senderEmail);
		message.setTo(email);
		message.setSubject("Xác minh tài khoản - UsedBook Marketplace");
		message.setText("Xin chào " + user.getFullName() + ",\n\n" + "Mã OTP xác minh email của bạn là: " + otpCode
				+ "\n\n" + "Mã có hiệu lực trong " + OTP_LIFETIME_MINUTES
				+ " phút. Không chia sẻ mã này với người khác.\n\n" + "UsedBook Marketplace");

		mailSender.send(message);
	}

	private String normalizeEmail(String email) {
		return email.trim().toLowerCase(Locale.ROOT);
	}

	private static final String CHANGE_EMAIL_PURPOSE = "CHANGE_EMAIL";

	@Transactional
	public void sendEmailChangeOtp(String currentUserEmail, String newEmail) {
		String normalizedCurrentEmail = normalizeEmail(currentUserEmail);
		String normalizedNewEmail = normalizeEmail(newEmail);

		if (normalizedNewEmail.isBlank()) {
			throw new IllegalArgumentException("Vui lòng nhập email mới.");
		}

		if (normalizedCurrentEmail.equals(normalizedNewEmail)) {
			throw new IllegalArgumentException("Email mới phải khác email hiện tại.");
		}

		AppUser user = userRepository.findByEmailIgnoreCase(normalizedCurrentEmail)
				.orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tài khoản đang đăng nhập."));

		if (!"ACTIVE".equals(user.getAccountStatus())) {
			throw new IllegalArgumentException("Tài khoản chưa được kích hoạt hoặc không thể đổi email.");
		}

		if (userRepository.existsByEmailIgnoreCase(normalizedNewEmail)) {
			throw new IllegalArgumentException("Email này đã được sử dụng. Vui lòng chọn email khác.");
		}

		String otpCode = String.format(Locale.ROOT, "%06d", secureRandom.nextInt(1_000_000));

		EmailOtp otp = new EmailOtp();
		otp.setUser(user);
		otp.setEmail(normalizedNewEmail);
		otp.setPurpose(CHANGE_EMAIL_PURPOSE);
		otp.setOtpHash(passwordEncoder.encode(otpCode));
		otp.setExpiresAt(LocalDateTime.now().plusMinutes(OTP_LIFETIME_MINUTES));
		otp.setFailedAttempts(0);

		otpRepository.save(otp);

		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom(senderEmail);
		message.setTo(normalizedNewEmail);
		message.setSubject("Mã xác minh đổi email - UsedBook Marketplace");
		message.setText("Xin chào " + user.getFullName() + ",\n\n" + "Mã OTP xác minh email mới của bạn là: " + otpCode
				+ "\n\n" + "Mã có hiệu lực trong " + OTP_LIFETIME_MINUTES
				+ " phút. Không chia sẻ mã này với người khác.\n\n"
				+ "Nếu bạn không yêu cầu đổi email, hãy bỏ qua email này.\n\n" + "UsedBook Marketplace");

		mailSender.send(message);
	}

	@Transactional
	public void verifyEmailChange(String currentUserEmail, String newEmail, String otpCode) {
		String normalizedCurrentEmail = normalizeEmail(currentUserEmail);
		String normalizedNewEmail = normalizeEmail(newEmail);

		AppUser user = userRepository.findByEmailIgnoreCase(normalizedCurrentEmail)
				.orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tài khoản đang đăng nhập."));

		if (!"ACTIVE".equals(user.getAccountStatus())) {
			throw new IllegalArgumentException("Tài khoản không thể đổi email.");
		}

		if (normalizedCurrentEmail.equals(normalizedNewEmail)) {
			throw new IllegalArgumentException("Email mới phải khác email hiện tại.");
		}

		if (userRepository.existsByEmailIgnoreCase(normalizedNewEmail)) {
			throw new IllegalArgumentException("Email này đã được sử dụng. Vui lòng chọn email khác.");
		}

		EmailOtp otp = otpRepository
				.findByEmailIgnoreCaseAndPurposeOrderByCreatedAtDesc(normalizedNewEmail, CHANGE_EMAIL_PURPOSE).stream()
				.filter(item -> item.getConsumedAt() == null)
				.filter(item -> item.getUser().getUserId().equals(user.getUserId())).findFirst()
				.orElseThrow(() -> new IllegalArgumentException(
						"Không tìm thấy mã OTP đang có hiệu lực. Vui lòng gửi mã mới."));

		if (otp.getFailedAttempts() >= MAX_FAILED_ATTEMPTS) {
			throw new IllegalArgumentException("Mã OTP đã bị khóa do nhập sai quá nhiều lần. Vui lòng yêu cầu mã mới.");
		}

		if (!otp.getExpiresAt().isAfter(LocalDateTime.now())) {
			throw new IllegalArgumentException("Mã OTP đã hết hạn. Vui lòng yêu cầu mã mới.");
		}

		if (!passwordEncoder.matches(otpCode, otp.getOtpHash())) {
			otp.setFailedAttempts(otp.getFailedAttempts() + 1);
			otpRepository.save(otp);
			throw new IllegalArgumentException("Mã OTP không chính xác.");
		}

		otp.setConsumedAt(LocalDateTime.now());
		otpRepository.save(otp);

		user.setEmail(normalizedNewEmail);
		userRepository.save(user);
	}
}