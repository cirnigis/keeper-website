
package vn.ubm.usedbookmarketplace.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.ubm.usedbookmarketplace.entity.Payment;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

	List<Payment> findByOrderIdOrderByCreatedAtDesc(Long orderId);
}
