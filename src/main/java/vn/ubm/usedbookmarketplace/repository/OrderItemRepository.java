
package vn.ubm.usedbookmarketplace.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vn.ubm.usedbookmarketplace.entity.AppUser;
import vn.ubm.usedbookmarketplace.repository.projection.OrderItemView;

@Repository
public interface OrderItemRepository extends JpaRepository<AppUser, Long> {

	@Query(value = """
			SELECT
			    oi.OrderItemId AS orderItemId,
			    oi.BookTitleSnapshot AS bookTitle,
			    oi.ConditionSnapshot AS [condition],
			    oi.UnitPrice AS unitPrice,
			    oi.Quantity AS quantity,
			    COALESCE(oi.LineTotal, oi.UnitPrice * oi.Quantity) AS lineTotal
			FROM dbo.OrderItems oi
			INNER JOIN dbo.Orders o ON o.OrderId = oi.OrderId
			INNER JOIN dbo.Users u ON u.UserId = o.BuyerUserId
			WHERE o.OrderId = :orderId
			  AND u.Email = :email
			ORDER BY oi.OrderItemId
			""", nativeQuery = true)
	List<OrderItemView> findItemsByOrderIdAndBuyerEmail(@Param("orderId") Long orderId, @Param("email") String email);
}
