package vn.ubm.usedbookmarketplace.repository;

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class SupportTicketRepository {
	private final NamedParameterJdbcTemplate jdbc;

	public SupportTicketRepository(NamedParameterJdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	public List<Map<String, Object>> findForUser(String email) {
		return jdbc.queryForList("""
				SELECT t.SupportTicketId AS ticketId, t.TicketCode AS ticketCode, t.Subject AS subject,
				       t.TicketStatus AS ticketStatus, t.Priority AS priority, t.CreatedAt AS createdAt,
				       t.OrderId AS orderId, t.ShopId AS shopId
				FROM dbo.SupportTickets t JOIN dbo.Users u ON u.UserId=t.CreatedByUserId
				WHERE LOWER(LTRIM(RTRIM(u.Email)))=LOWER(LTRIM(RTRIM(:email)))
				ORDER BY t.CreatedAt DESC
				""", Map.of("email", email));
	}

	public List<Map<String, Object>> findQueue(String email, boolean admin) {
		String sql = """
				SELECT t.SupportTicketId AS ticketId, t.TicketCode AS ticketCode, t.Subject AS subject,
				       t.Description AS description, t.TicketStatus AS ticketStatus, t.Priority AS priority,
				       t.CreatedAt AS createdAt, t.OrderId AS orderId, t.ShopId AS shopId,
				       creator.FullName AS creatorName, manager.Email AS assignedManagerEmail,
				       adminUser.Email AS assignedAdminEmail
				FROM dbo.SupportTickets t
				JOIN dbo.Users creator ON creator.UserId=t.CreatedByUserId
				LEFT JOIN dbo.Users manager ON manager.UserId=t.AssignedManagerUserId
				LEFT JOIN dbo.Users adminUser ON adminUser.UserId=t.AssignedAdminUserId
				WHERE (:admin=1 AND (t.TicketStatus='ESCALATED' OR t.AssignedAdminUserId IS NOT NULL))
				   OR (:admin=0 AND (LOWER(manager.Email)=LOWER(:email) OR (t.AssignedAdminUserId IS NULL AND t.ShopId IS NOT NULL AND EXISTS (SELECT 1 FROM dbo.Users mu JOIN dbo.ShopManagerAssignments sma ON sma.ManagerUserId=mu.UserId AND sma.EndedAt IS NULL WHERE LOWER(LTRIM(RTRIM(mu.Email)))=LOWER(LTRIM(RTRIM(:email))) AND sma.ShopId=t.ShopId))))
				ORDER BY CASE WHEN t.TicketStatus='ESCALATED' THEN 0 ELSE 1 END, t.CreatedAt ASC
				""";
		return jdbc.queryForList(sql, Map.of("email", email, "admin", admin));
	}

	public List<Map<String, Object>> findMessages(long ticketId, String email, boolean staff) {
		String access = staff ? "1=1"
				: "EXISTS (SELECT 1 FROM dbo.SupportTickets t JOIN dbo.Users u ON u.UserId=t.CreatedByUserId WHERE t.SupportTicketId=:id AND LOWER(u.Email)=LOWER(:email))";
		String sql = "SELECT m.SupportTicketId AS ticketId, u.FullName AS senderName, m.MessageText AS messageText, m.IsInternalNote AS internalNote, m.CreatedAt AS createdAt FROM dbo.SupportTicketMessages m JOIN dbo.Users u ON u.UserId=m.SenderUserId WHERE m.SupportTicketId=:id AND m.IsInternalNote=0 OR (m.SupportTicketId=:id AND m.IsInternalNote=1 AND "
				+ (staff ? "1=1" : "1=0") + ") ORDER BY m.CreatedAt";
		if (!staff)
			sql = "SELECT m.SupportTicketId AS ticketId, u.FullName AS senderName, m.MessageText AS messageText, m.IsInternalNote AS internalNote, m.CreatedAt AS createdAt FROM dbo.SupportTicketMessages m JOIN dbo.Users u ON u.UserId=m.SenderUserId WHERE m.SupportTicketId=:id AND m.IsInternalNote=0 AND "
					+ access + " ORDER BY m.CreatedAt";
		return jdbc.queryForList(sql, Map.of("id", ticketId, "email", email));
	}

	@Transactional
	public long create(String email, Long orderId, String subject, String description) {
		if (subject == null || subject.trim().length() < 4 || subject.trim().length() > 200)
			throw new IllegalArgumentException("Tiêu đề phải từ 4 đến 200 ký tự.");
		if (description == null || description.trim().length() < 10 || description.trim().length() > 4000)
			throw new IllegalArgumentException("Mô tả phải từ 10 đến 4.000 ký tự.");
		MapSqlParameterSource p = new MapSqlParameterSource().addValue("email", email).addValue("orderId", orderId);
		List<Map<String, Object>> users = jdbc.queryForList(
				"SELECT UserId FROM dbo.Users WHERE LOWER(LTRIM(RTRIM(Email)))=LOWER(LTRIM(RTRIM(:email)))", p);
		if (users.isEmpty())
			throw new IllegalArgumentException("Không tìm thấy tài khoản.");
		long userId = ((Number) users.get(0).get("UserId")).longValue();
		Long shopId = null;
		if (orderId != null) {
			List<Map<String, Object>> orders = jdbc.queryForList("""
					SELECT o.ShopId FROM dbo.Orders o JOIN dbo.Shops s ON s.ShopId=o.ShopId
					WHERE o.OrderId=:orderId AND (o.BuyerUserId=:userId OR s.OwnerUserId=:userId)
					""", Map.of("orderId", orderId, "userId", userId));
			if (orders.isEmpty())
				throw new IllegalArgumentException("Bạn không có quyền tạo khiếu nại cho đơn hàng này.");
			Object v = orders.get(0).get("ShopId");
			if (v instanceof Number n)
				shopId = n.longValue();
		}
		String code = "KT-" + java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
		MapSqlParameterSource t = new MapSqlParameterSource().addValue("code", code).addValue("userId", userId)
				.addValue("orderId", orderId).addValue("shopId", shopId).addValue("subject", subject.trim())
				.addValue("description", description.trim());
		jdbc.update(
				"INSERT INTO dbo.SupportTickets (TicketCode,CreatedByUserId,ShopId,OrderId,Subject,Description,Priority,TicketStatus,CreatedAt,UpdatedAt) VALUES (:code,:userId,:shopId,:orderId,:subject,:description,'NORMAL','OPEN',SYSDATETIME(),SYSDATETIME())",
				t);
		return jdbc.queryForObject("SELECT SupportTicketId FROM dbo.SupportTickets WHERE TicketCode=:code",
				Map.of("code", code), Long.class);
	}

	@Transactional
	public void reply(String email, long ticketId, String message, boolean internal, boolean staff, boolean admin) {
		if (message == null || message.trim().isEmpty() || message.trim().length() > 4000)
			throw new IllegalArgumentException("Nội dung phản hồi không hợp lệ.");
		if (internal && !staff)
			throw new IllegalArgumentException("Bạn không có quyền ghi chú nội bộ.");
		String access = staff ? (admin ? "(t.TicketStatus='ESCALATED' OR t.AssignedAdminUserId=:actor)"
				: "(t.AssignedAdminUserId IS NULL AND (t.AssignedManagerUserId=:actor OR (t.ShopId IS NOT NULL AND EXISTS (SELECT 1 FROM dbo.ShopManagerAssignments sma WHERE sma.ShopId=t.ShopId AND sma.ManagerUserId=:actor AND sma.EndedAt IS NULL))))")
				: "EXISTS (SELECT 1 FROM dbo.Users u WHERE u.UserId=t.CreatedByUserId AND LOWER(LTRIM(RTRIM(u.Email)))=LOWER(LTRIM(RTRIM(:email))))";
		long actor = jdbc.queryForObject(
				"SELECT UserId FROM dbo.Users WHERE LOWER(LTRIM(RTRIM(Email)))=LOWER(LTRIM(RTRIM(:email)))",
				Map.of("email", email), Long.class);
		MapSqlParameterSource p = new MapSqlParameterSource().addValue("ticketId", ticketId).addValue("email", email)
				.addValue("actor", actor);
		List<Map<String, Object>> allowed = jdbc.queryForList(
				"SELECT t.TicketStatus FROM dbo.SupportTickets t WHERE t.SupportTicketId=:ticketId AND " + access, p);
		if (allowed.isEmpty())
			throw new IllegalArgumentException("Không tìm thấy khiếu nại hoặc bạn không có quyền truy cập.");
		String currentStatus = String.valueOf(allowed.get(0).get("TicketStatus")).toUpperCase();
		if ("CLOSED".equals(currentStatus) || "RESOLVED".equals(currentStatus))
			throw new IllegalArgumentException("Khiếu nại đã được giải quyết/đóng nên không thể gửi thêm phản hồi.");
		long sender = jdbc.queryForObject(
				"SELECT UserId FROM dbo.Users WHERE LOWER(LTRIM(RTRIM(Email)))=LOWER(LTRIM(RTRIM(:email)))",
				Map.of("email", email), Long.class);
		jdbc.update(
				"INSERT INTO dbo.SupportTicketMessages (SupportTicketId,SenderUserId,MessageText,IsInternalNote,CreatedAt) VALUES (:ticketId,:sender,:message,:internal,SYSDATETIME())",
				new MapSqlParameterSource().addValue("ticketId", ticketId).addValue("sender", sender)
						.addValue("message", message.trim()).addValue("internal", internal));
		jdbc.update("UPDATE dbo.SupportTickets SET UpdatedAt=SYSDATETIME() WHERE SupportTicketId=:ticketId",
				Map.of("ticketId", ticketId));
	}

	@Transactional
	public void updateStatus(String email, long ticketId, String status, boolean admin) {
		status = status == null ? "" : status.trim().toUpperCase();
		List<String> allowedStatuses = admin ? List.of("IN_PROGRESS", "RESOLVED", "CLOSED")
				: List.of("IN_PROGRESS", "ESCALATED", "RESOLVED");
		if (!allowedStatuses.contains(status))
			throw new IllegalArgumentException("Trạng thái không hợp lệ hoặc vượt quyền xử lý của bạn.");

		Long actorValue = jdbc.query(
				"SELECT UserId FROM dbo.Users WHERE LOWER(LTRIM(RTRIM(Email)))=LOWER(LTRIM(RTRIM(:email)))",
				Map.of("email", email), rs -> rs.next() ? rs.getLong(1) : null);
		if (actorValue == null)
			throw new IllegalArgumentException("Không tìm thấy tài khoản.");
		long actor = actorValue;
		MapSqlParameterSource p = new MapSqlParameterSource().addValue("id", ticketId).addValue("actor", actor)
				.addValue("admin", admin).addValue("status", status);

		// Enforce both assignment scope and legal state transitions in the database
		// update.
		// Terminal tickets cannot be silently reopened; an administrator must
		// create/assign a new ticket if needed.
		String scope = admin ? "(t.TicketStatus='ESCALATED' OR t.AssignedAdminUserId=:actor)"
				: "(t.AssignedAdminUserId IS NULL AND (t.AssignedManagerUserId=:actor OR (t.AssignedManagerUserId IS NULL AND t.ShopId IS NOT NULL AND EXISTS (SELECT 1 FROM dbo.ShopManagerAssignments sma WHERE sma.ShopId=t.ShopId AND sma.ManagerUserId=:actor AND sma.EndedAt IS NULL))))";
		String transition = "t.TicketStatus NOT IN ('RESOLVED','CLOSED')";
		int changed = jdbc
				.update("""
						UPDATE t SET TicketStatus=:status, UpdatedAt=SYSDATETIME(),
						  AssignedAdminUserId=CASE WHEN :status='ESCALATED' THEN NULL WHEN :admin=1 THEN :actor ELSE t.AssignedAdminUserId END,
						  AssignedManagerUserId=CASE WHEN :status='ESCALATED' THEN t.AssignedManagerUserId WHEN :admin=0 AND t.AssignedManagerUserId IS NULL THEN :actor ELSE t.AssignedManagerUserId END,
						  EscalatedAt=CASE WHEN :status='ESCALATED' THEN SYSDATETIME() ELSE t.EscalatedAt END,
						  ResolvedAt=CASE WHEN :status IN ('RESOLVED','CLOSED') THEN SYSDATETIME() ELSE NULL END
						FROM dbo.SupportTickets t
						WHERE t.SupportTicketId=:id AND """
						+ scope + " AND " + transition, p);
		if (changed == 0)
			throw new IllegalArgumentException(
					"Không tìm thấy khiếu nại, khiếu nại đã kết thúc hoặc bạn không được phân quyền xử lý.");
	}
}
