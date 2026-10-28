package vn.ubm.usedbookmarketplace.service;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Manual shipment workflow. Every transition is checked and audited
 * transactionally.
 */
@Service
public class ShipmentWorkflowService {
	private final NamedParameterJdbcTemplate jdbc;

	public ShipmentWorkflowService(NamedParameterJdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	@Transactional(readOnly = true)
	public List<Map<String, Object>> providers() {
		return jdbc.queryForList(
				"SELECT ShippingProviderId AS providerId, ProviderName AS providerName FROM dbo.ShippingProviders WHERE IsActive=1 ORDER BY ProviderName",
				new MapSqlParameterSource());
	}

	@Transactional(readOnly = true)
	public List<Map<String, Object>> shipments(String email, long shopId, long orderId) {
		requireOwner(email, shopId, orderId);
		return jdbc.queryForList(
				"SELECT sh.ShipmentId AS shipmentId, sh.TrackingCode AS trackingCode, sh.ShipmentStatus AS shipmentStatus, sh.AttemptNumber AS attemptNumber, sh.CreatedAt AS createdAt, sp.ProviderName AS providerName FROM dbo.Shipments sh LEFT JOIN dbo.ShippingProviders sp ON sp.ShippingProviderId=sh.ShippingProviderId WHERE sh.OrderId=:orderId ORDER BY sh.AttemptNumber DESC, sh.ShipmentId DESC",
				new MapSqlParameterSource().addValue("orderId", orderId));
	}

	@Transactional
	public void createShipment(String email, long actorId, long shopId, long orderId, long providerId,
			String trackingCode) {
		requireOwner(email, shopId, orderId);
		if (actorId <= 0 || providerId <= 0)
			throw new IllegalArgumentException("Thông tin vận chuyển không hợp lệ.");
		String code = trackingCode == null ? "" : trackingCode.trim();
		if (code.isEmpty() || code.length() > 100)
			throw new IllegalArgumentException("Mã vận đơn bắt buộc và tối đa 100 ký tự.");
		MapSqlParameterSource p = new MapSqlParameterSource().addValue("orderId", orderId).addValue("shopId", shopId);
		// Lock the order first for every shipment mutation. This serializes duplicate
		// shipment creation and gives create/update paths the same lock order.
		String status = jdbc.query(
				"SELECT OrderStatus FROM dbo.Orders WITH (UPDLOCK, HOLDLOCK, ROWLOCK) WHERE OrderId=:orderId AND ShopId=:shopId",
				p, (rs, n) -> rs.getString(1)).stream().findFirst().orElse(null);
		if (!"READY_TO_SHIP".equalsIgnoreCase(status))
			throw new IllegalArgumentException("Chỉ tạo vận đơn khi đơn ở trạng thái READY_TO_SHIP.");
		Integer providerCount = jdbc.queryForObject(
				"SELECT COUNT(1) FROM dbo.ShippingProviders WHERE ShippingProviderId=:providerId AND IsActive=1",
				new MapSqlParameterSource("providerId", providerId), Integer.class);
		if (providerCount == null || providerCount == 0)
			throw new IllegalArgumentException("Đơn vị vận chuyển không tồn tại hoặc đã ngừng hoạt động.");
		Integer existing = jdbc.queryForObject(
				"SELECT COUNT(1) FROM dbo.Shipments WITH (UPDLOCK, HOLDLOCK) WHERE OrderId=:orderId AND ShipmentStatus NOT IN ('FAILED','CANCELED')",
				p, Integer.class);
		if (existing != null && existing > 0)
			throw new IllegalArgumentException("Đơn hàng đã có vận đơn đang hoạt động.");
		Long shipmentId = jdbc.queryForObject(
				"""
						INSERT INTO dbo.Shipments (OrderId,ShippingProviderId,TrackingCode,ShipmentStatus,AttemptNumber,AssignedAt,CreatedAt,UpdatedAt)
						OUTPUT INSERTED.ShipmentId
						VALUES (:orderId,:providerId,:trackingCode,'WAITING_ASSIGNMENT',
						        COALESCE((SELECT MAX(AttemptNumber)+1 FROM dbo.Shipments WITH (UPDLOCK, HOLDLOCK) WHERE OrderId=:orderId),1),
						        SYSUTCDATETIME(),SYSUTCDATETIME(),SYSUTCDATETIME())
						""",
				new MapSqlParameterSource().addValue("orderId", orderId).addValue("providerId", providerId)
						.addValue("trackingCode", code),
				Long.class);
		if (shipmentId == null)
			throw new IllegalStateException("Không lấy được mã vận đơn vừa tạo.");
		jdbc.update(
				"INSERT INTO dbo.ShipmentStatusHistories (ShipmentId,ActorUserId,PreviousStatus,NewStatus,Note,CreatedAt) VALUES (:shipmentId,:actorId,NULL,'WAITING_ASSIGNMENT','Tạo vận đơn thủ công',SYSUTCDATETIME())",
				new MapSqlParameterSource().addValue("shipmentId", shipmentId).addValue("actorId", actorId));
	}

	@Transactional
	public void updateShipmentStatus(String email, long actorId, long shopId, long orderId, long shipmentId,
			String requestedStatus, String note) {
		requireOwner(email, shopId, orderId);
		if (actorId <= 0)
			throw new IllegalArgumentException("Không xác định được tài khoản.");
		String next = requestedStatus == null ? "" : requestedStatus.trim().toUpperCase(Locale.ROOT);
		if (!List.of("PICKED_UP", "IN_TRANSIT", "DELIVERED", "FAILED").contains(next))
			throw new IllegalArgumentException("Trạng thái vận chuyển không hợp lệ.");
		// Keep the global lock order Orders -> Shipments to avoid races with creation
		// and with any order-state transition performed by this workflow.
		MapSqlParameterSource orderParams = new MapSqlParameterSource().addValue("orderId", orderId).addValue("shopId",
				shopId);
		String orderStatus = jdbc.query(
				"SELECT OrderStatus FROM dbo.Orders WITH (UPDLOCK, HOLDLOCK, ROWLOCK) WHERE OrderId=:orderId AND ShopId=:shopId",
				orderParams, (rs, n) -> rs.getString(1)).stream().findFirst().orElse(null);
		if (!List.of("READY_TO_SHIP", "SHIPPING")
				.contains(orderStatus == null ? "" : orderStatus.toUpperCase(Locale.ROOT))) {
			throw new IllegalArgumentException("Trạng thái đơn hàng không cho phép cập nhật vận chuyển.");
		}
		MapSqlParameterSource p = new MapSqlParameterSource().addValue("shipmentId", shipmentId).addValue("orderId",
				orderId);
		List<String> found = jdbc.query(
				"SELECT ShipmentStatus FROM dbo.Shipments WITH (UPDLOCK, HOLDLOCK, ROWLOCK) WHERE ShipmentId=:shipmentId AND OrderId=:orderId",
				p, (rs, n) -> rs.getString(1));
		if (found.isEmpty())
			throw new IllegalArgumentException("Không tìm thấy vận đơn của đơn hàng này.");
		String prev = found.get(0);
		boolean valid = switch (prev) {
		case "WAITING_ASSIGNMENT" -> next.equals("PICKED_UP") || next.equals("FAILED");
		case "PICKED_UP" -> next.equals("IN_TRANSIT") || next.equals("FAILED");
		case "IN_TRANSIT" -> next.equals("DELIVERED") || next.equals("FAILED");
		default -> false;
		};
		if (!valid)
			throw new IllegalArgumentException("Không thể chuyển vận đơn từ " + prev + " sang " + next + ".");
		String cleanNote = note == null ? null : note.trim();
		if (cleanNote != null && cleanNote.length() > 1000)
			throw new IllegalArgumentException("Ghi chú tối đa 1000 ký tự.");
		int changed = jdbc.update(
				"UPDATE dbo.Shipments SET ShipmentStatus=:next, PickedUpAt=CASE WHEN :next='PICKED_UP' THEN SYSUTCDATETIME() ELSE PickedUpAt END, DeliveredAt=CASE WHEN :next='DELIVERED' THEN SYSUTCDATETIME() ELSE DeliveredAt END, FailedReason=CASE WHEN :next='FAILED' THEN LEFT(:note,500) ELSE FailedReason END, UpdatedAt=SYSUTCDATETIME() WHERE ShipmentId=:shipmentId AND OrderId=:orderId AND ShipmentStatus=:prev",
				p.addValue("next", next).addValue("prev", prev).addValue("note", cleanNote));
		if (changed != 1)
			throw new IllegalArgumentException("Vận đơn vừa được cập nhật ở nơi khác; hãy tải lại trang.");
		jdbc.update(
				"INSERT INTO dbo.ShipmentStatusHistories (ShipmentId,ActorUserId,PreviousStatus,NewStatus,Note,CreatedAt) VALUES (:shipmentId,:actorId,:prev,:next,:note,SYSUTCDATETIME())",
				new MapSqlParameterSource().addValue("shipmentId", shipmentId).addValue("actorId", actorId)
						.addValue("prev", prev).addValue("next", next).addValue("note", cleanNote));
		if ("DELIVERED".equals(next)) {
			int orderChanged = jdbc.update(
					"UPDATE dbo.Orders SET OrderStatus='DELIVERED', DeliveredAt=COALESCE(DeliveredAt,SYSUTCDATETIME()), UpdatedAt=SYSUTCDATETIME() WHERE OrderId=:orderId AND ShopId=:shopId AND OrderStatus IN ('READY_TO_SHIP','SHIPPING')",
					new MapSqlParameterSource().addValue("orderId", orderId).addValue("shopId", shopId));
			if (orderChanged != 1)
				throw new IllegalArgumentException("Trạng thái đơn hàng vừa thay đổi; không thể xác nhận đã giao.");
			jdbc.update(
					"INSERT INTO dbo.OrderStatusHistories (OrderId,ActorUserId,PreviousStatus,NewStatus,Note,CreatedAt) VALUES (:orderId,:actorId,:previousStatus,'DELIVERED',:note,SYSUTCDATETIME())",
					new MapSqlParameterSource().addValue("orderId", orderId).addValue("actorId", actorId)
							.addValue("previousStatus", orderStatus)
							.addValue("note", cleanNote == null ? "Vận đơn đã giao thành công" : cleanNote));
		} else if ("PICKED_UP".equals(next) || "IN_TRANSIT".equals(next)) {
			if ("READY_TO_SHIP".equalsIgnoreCase(orderStatus)) {
				int orderChanged = jdbc.update(
						"UPDATE dbo.Orders SET OrderStatus='SHIPPING', UpdatedAt=SYSUTCDATETIME() WHERE OrderId=:orderId AND ShopId=:shopId AND OrderStatus='READY_TO_SHIP'",
						new MapSqlParameterSource().addValue("orderId", orderId).addValue("shopId", shopId));
				if (orderChanged != 1)
					throw new IllegalArgumentException("Trạng thái đơn hàng vừa thay đổi; hãy tải lại trang.");
				jdbc.update(
						"INSERT INTO dbo.OrderStatusHistories (OrderId,ActorUserId,PreviousStatus,NewStatus,Note,CreatedAt) VALUES (:orderId,:actorId,'READY_TO_SHIP','SHIPPING',:note,SYSUTCDATETIME())",
						new MapSqlParameterSource().addValue("orderId", orderId).addValue("actorId", actorId).addValue(
								"note", cleanNote == null ? "Đơn hàng đã bàn giao cho đơn vị vận chuyển" : cleanNote));
			}
		}
	}

	private void requireOwner(String email, long shopId, long orderId) {
		if (email == null || email.isBlank() || shopId <= 0 || orderId <= 0)
			throw new IllegalArgumentException("Thông tin shop hoặc đơn hàng không hợp lệ.");
		Integer count = jdbc.queryForObject(
				"SELECT COUNT(1) FROM dbo.Orders o JOIN dbo.Shops s ON s.ShopId=o.ShopId JOIN dbo.Users u ON u.UserId=s.OwnerUserId WHERE o.OrderId=:orderId AND o.ShopId=:shopId AND LOWER(LTRIM(RTRIM(u.Email)))=LOWER(LTRIM(RTRIM(:email))) AND s.ApprovalStatus='APPROVED' AND s.IsActive=1",
				new MapSqlParameterSource().addValue("orderId", orderId).addValue("shopId", shopId).addValue("email",
						email),
				Integer.class);
		if (count == null || count != 1)
			throw new IllegalArgumentException("Không có quyền quản lý đơn hàng này.");
	}
}
