package vn.ubm.usedbookmarketplace.service;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Releases stock only for expired, still-unpaid orders; committed reservations
 * are never touched.
 */
@Service
public class InventoryReservationExpiryService {
	private final NamedParameterJdbcTemplate jdbc;

	public InventoryReservationExpiryService(NamedParameterJdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	/**
	 * Runs periodically; all reservation, stock, and order changes commit or roll
	 * back together.
	 */
	@Scheduled(fixedDelayString = "${keeper.inventory-reservations.expiry-scan-ms:60000}", initialDelayString = "${keeper.inventory-reservations.expiry-initial-delay-ms:30000}")
	@Transactional
	public void releaseExpiredReservations() {
		jdbc.getJdbcTemplate().execute(
				"""
						SET NOCOUNT ON;
						DECLARE @Released TABLE (OrderItemId BIGINT NOT NULL, ListingId BIGINT NOT NULL, Quantity INT NOT NULL);
						DECLARE @OrderId BIGINT;
						DECLARE @OrderStatus VARCHAR(30);
						DECLARE @HasPaid BIT;
						DECLARE @HasLiveReservation BIT;

						-- Candidate discovery is deliberately read-only. Each order is then locked
						-- first, matching PaymentService's lock order (Orders -> Reservations -> Listings).
						DECLARE expired_order_cursor CURSOR LOCAL FAST_FORWARD FOR
						    SELECT DISTINCT oi.OrderId
						    FROM dbo.InventoryReservations r
						    JOIN dbo.OrderItems oi ON oi.OrderItemId = r.OrderItemId
						    WHERE r.ReservationStatus = 'RESERVED'
						      AND r.ExpiresAt <= SYSUTCDATETIME()
						    ORDER BY oi.OrderId;

						OPEN expired_order_cursor;
						FETCH NEXT FROM expired_order_cursor INTO @OrderId;
						WHILE @@FETCH_STATUS = 0
						BEGIN
						    SET @OrderStatus = NULL;
						    SELECT @OrderStatus = o.OrderStatus
						    FROM dbo.Orders o WITH (UPDLOCK, HOLDLOCK, ROWLOCK)
						    WHERE o.OrderId = @OrderId;

						    IF @OrderStatus = 'PENDING_PAYMENT'
						    BEGIN
						        SET @HasPaid = 0;
						        IF EXISTS (SELECT 1 FROM dbo.Payments p WITH (UPDLOCK, HOLDLOCK) WHERE p.OrderId=@OrderId AND p.PaymentStatus='PAID')
						            SET @HasPaid = 1;

						        SET @HasLiveReservation = 0;
						        IF EXISTS (
						            SELECT 1 FROM dbo.InventoryReservations r WITH (UPDLOCK, HOLDLOCK)
						            JOIN dbo.OrderItems oi ON oi.OrderItemId=r.OrderItemId
						            WHERE oi.OrderId=@OrderId AND r.ReservationStatus='RESERVED'
						              AND r.ExpiresAt > SYSUTCDATETIME()
						        ) SET @HasLiveReservation = 1;

						        IF @HasPaid = 0 AND @HasLiveReservation = 0
						        BEGIN
						            DELETE FROM @Released;
						            UPDATE r
						               SET ReservationStatus='RELEASED', ReleasedAt=SYSUTCDATETIME()
						            OUTPUT inserted.OrderItemId, inserted.ListingId, inserted.Quantity
						              INTO @Released(OrderItemId, ListingId, Quantity)
						            FROM dbo.InventoryReservations r WITH (UPDLOCK, HOLDLOCK)
						            JOIN dbo.OrderItems oi ON oi.OrderItemId=r.OrderItemId
						            WHERE oi.OrderId=@OrderId
						              AND r.ReservationStatus='RESERVED'
						              AND r.ExpiresAt <= SYSUTCDATETIME();

						            UPDATE l
						               SET StockQuantity = l.StockQuantity + x.Quantity,
						                   ListingStatus = CASE WHEN l.ListingStatus='SOLD_OUT' THEN 'ACTIVE' ELSE l.ListingStatus END,
						                   UpdatedAt = SYSUTCDATETIME()
						            FROM dbo.ShopListings l
						            JOIN (SELECT ListingId, SUM(Quantity) Quantity FROM @Released GROUP BY ListingId) x
						              ON x.ListingId=l.ListingId;

						            IF NOT EXISTS (
						                SELECT 1 FROM dbo.InventoryReservations r WITH (UPDLOCK, HOLDLOCK)
						                JOIN dbo.OrderItems oi ON oi.OrderItemId=r.OrderItemId
						                WHERE oi.OrderId=@OrderId AND r.ReservationStatus='RESERVED'
						            )
						            BEGIN
						                UPDATE dbo.Orders
						                   SET OrderStatus='CANCELLED', CanceledAt=COALESCE(CanceledAt,SYSUTCDATETIME()),
						                       UpdatedAt=SYSUTCDATETIME()
						                WHERE OrderId=@OrderId AND OrderStatus='PENDING_PAYMENT'
						                  AND NOT EXISTS (SELECT 1 FROM dbo.Payments p WHERE p.OrderId=@OrderId AND p.PaymentStatus='PAID');

						                IF @@ROWCOUNT = 1
						                    INSERT INTO dbo.OrderStatusHistories(OrderId, ActorUserId, PreviousStatus, NewStatus, Note)
						                    VALUES (@OrderId, NULL, 'PENDING_PAYMENT', 'CANCELLED', N'Hết hạn giữ hàng/thanh toán; tồn kho đã được giải phóng tự động.');
						            END
						        END
						    END
						    FETCH NEXT FROM expired_order_cursor INTO @OrderId;
						END
						CLOSE expired_order_cursor;
						DEALLOCATE expired_order_cursor;
						""");
	}
}
