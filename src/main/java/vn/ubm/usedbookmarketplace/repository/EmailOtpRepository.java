
package vn.ubm.usedbookmarketplace.repository;

import vn.ubm.usedbookmarketplace.entity.EmailOtp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmailOtpRepository extends JpaRepository<EmailOtp, Long> {

	List<EmailOtp> findByEmailIgnoreCaseAndPurposeOrderByCreatedAtDesc(String email, String purpose);
}
