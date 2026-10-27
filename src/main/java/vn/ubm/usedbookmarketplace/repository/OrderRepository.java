
package vn.ubm.usedbookmarketplace.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vn.ubm.usedbookmarketplace.entity.Order;
import vn.ubm.usedbookmarketplace.repository.projection.OrderDetailView;
import vn.ubm.usedbookmarketplace.repository.projection.OrderListView;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long>, OrderRepositoryCustom {

	@Query(value = """
			SELECT
			    o.OrderId AS orderId,
			    o.OrderCode AS orderCode,
			    o.CreatedAt AS createdAt,
			    o.OrderStatus AS status,
			    (
			        SELECT TOP (1) p.PaymentStatus
			        FROM dbo.Payments p
			        WHERE p.OrderId = o.OrderId
			        ORDER BY p.CreatedAt DESC, p.PaymentId DESC
			    ) AS paymentStatus,
			    o.TotalAmount AS totalAmount,
			    s.ShopName AS shopName
			FROM dbo.Orders o
			INNER JOIN dbo.Users u ON u.UserId = o.BuyerUserId
			INNER JOIN dbo.Shops s ON s.ShopId = o.ShopId
			WHERE u.Email = :email
			ORDER BY o.CreatedAt DESC, o.OrderId DESC
			""", nativeQuery = true)
	List<OrderListView> findOrdersByBuyerEmail(@Param("email") String email);

	@Query(value = """
			SELECT
			    o.OrderId AS orderId,
			    o.OrderCode AS orderCode,
			    o.CreatedAt AS createdAt,
			    o.OrderStatus AS status,
			    o.PaymentMethod AS paymentMethod,
			    (
			        SELECT TOP (1) p.PaymentStatus
			        FROM dbo.Payments p
			        WHERE p.OrderId = o.OrderId
			        ORDER BY p.CreatedAt DESC, p.PaymentId DESC
			    ) AS paymentStatus,
			    o.Subtotal AS subtotal,
			    o.DiscountAmount AS discountAmount,
			    o.ShippingFee AS shippingFee,
			    o.TotalAmount AS totalAmount,
			    s.ShopName AS shopName,
			    o.RecipientName AS recipientName,
			    o.RecipientPhone AS recipientPhone,
			    o.Province AS province,
			    o.District AS district,
			    o.Ward AS ward,
			    o.AddressLine AS addressLine,
			    o.BuyerNote AS buyerNote
			FROM dbo.Orders o
			INNER JOIN dbo.Users u ON u.UserId = o.BuyerUserId
			INNER JOIN dbo.Shops s ON s.ShopId = o.ShopId
			WHERE o.OrderId = :orderId
			  AND u.Email = :email
			""", nativeQuery = true)
	Optional<OrderDetailView> findOrderDetailsByIdAndBuyerEmail(@Param("orderId") Long orderId,
			@Param("email") String email);
}
