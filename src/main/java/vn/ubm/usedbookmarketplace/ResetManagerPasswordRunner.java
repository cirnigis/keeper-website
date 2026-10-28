package vn.ubm.usedbookmarketplace;

import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class ResetManagerPasswordRunner implements CommandLineRunner {

	private final NamedParameterJdbcTemplate jdbc;
	private final PasswordEncoder passwordEncoder;

	public ResetManagerPasswordRunner(NamedParameterJdbcTemplate jdbc, PasswordEncoder passwordEncoder) {
		this.jdbc = jdbc;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public void run(String... args) {
		// Đổi thành mật khẩu mới bạn muốn sử dụng
		String newPassword = "Manager@2026!";

		String encodedPassword = passwordEncoder.encode(newPassword);

		String sql = """
				UPDATE dbo.Users
				SET PasswordHash = :passwordHash
				WHERE Email = :email
				""";

		int updated = jdbc.update(sql, new MapSqlParameterSource().addValue("passwordHash", encodedPassword)
				.addValue("email", "manager.test@keeper.local"));

		if (updated == 1) {
			System.out.println("Đã cập nhật mật khẩu cho tài khoản Manager.");
		} else {
			System.out.println("Không cập nhật được: không tìm thấy tài khoản hoặc có nhiều dòng khớp.");
		}
	}
}