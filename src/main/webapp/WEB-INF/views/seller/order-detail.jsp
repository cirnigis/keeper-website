
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<!DOCTYPE html>
<html lang="vi">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Chi tiết đơn hàng | Keeper</title>
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
	<div class="seller-page">
		<header class="seller-header">
			<div>
				<a class="seller-brand" href="${pageContext.request.contextPath}/">Keeper</a>
				<p class="seller-subtitle">Khu vực người bán</p>
			</div>
			<nav class="seller-nav">
				<a class="seller-nav-link"
					href="${pageContext.request.contextPath}/seller/shops">Shop của
					tôi</a> <a class="seller-nav-link"
					href="${pageContext.request.contextPath}/seller/listings">Sản
					phẩm</a> <a class="seller-nav-link active"
					href="${pageContext.request.contextPath}/seller/orders">Đơn
					hàng</a>
			</nav>
		</header>

		<main class="seller-main">
			<c:if test="${not empty successMessage}">
				<div class="seller-alert seller-alert-success">
					<c:out value="${successMessage}" />
				</div>
			</c:if>
			<c:if test="${not empty errorMessage}">
				<div class="seller-alert seller-alert-error">
					<c:out value="${errorMessage}" />
				</div>
			</c:if>

			<div class="seller-page-heading">
				<div>
					<p class="seller-eyebrow">ORDER DETAILS</p>
					<h1>
						Đơn hàng
						<c:out value="${order.orderCode}" />
					</h1>
					<p class="seller-muted">
						Mã đơn nội bộ: #
						<c:out value="${order.orderId}" />
					</p>
				</div>
				<a class="seller-button seller-button-outline"
					href="${pageContext.request.contextPath}/seller/orders?shopId=${order.shopId}">Quay
					lại</a>
			</div>

			<div class="seller-detail-grid">
				<div class="seller-detail-main">
					<section class="seller-panel">
						<h2>Sản phẩm trong đơn</h2>
						<div class="seller-table-wrap">
							<table class="seller-table">
								<thead>
									<tr>
										<th>Sản phẩm</th>
										<th>Tình trạng</th>
										<th>Đơn giá</th>
										<th>Số lượng</th>
										<th class="seller-text-right">Thành tiền</th>
									</tr>
								</thead>
								<tbody>
									<c:forEach var="item" items="${items}">
										<tr>
											<td><strong><c:out value="${item.bookTitle}" /></strong>
												<span class="seller-table-subtitle">Listing #<c:out
														value="${item.listingId}" /></span></td>
											<td><c:out value="${item.conditionCode}" /></td>
											<td><fmt:formatNumber value="${item.unitPrice}"
													pattern="#,##0" /> ₫</td>
											<td><c:out value="${item.quantity}" /></td>
											<td class="seller-text-right"><strong><fmt:formatNumber
														value="${item.lineTotal}" pattern="#,##0" /> ₫</strong></td>
										</tr>
									</c:forEach>
								</tbody>
							</table>
						</div>

						<div class="seller-total-box">
							<div>
								<span>Tạm tính</span><strong><fmt:formatNumber
										value="${order.subtotal}" pattern="#,##0" /> ₫</strong>
							</div>
							<div>
								<span>Giảm giá</span><strong>- <fmt:formatNumber
										value="${order.discountAmount}" pattern="#,##0" /> ₫
								</strong>
							</div>
							<div>
								<span>Phí vận chuyển</span><strong><fmt:formatNumber
										value="${order.shippingFee}" pattern="#,##0" /> ₫</strong>
							</div>
							<div class="seller-grand-total">
								<span>Tổng thanh toán</span><strong><fmt:formatNumber
										value="${order.totalAmount}" pattern="#,##0" /> ₫</strong>
							</div>
						</div>
					</section>

					<section class="seller-panel">
						<h2>Lịch sử trạng thái</h2>
						<c:choose>
							<c:when test="${empty histories}">
								<p class="seller-muted">Chưa có lịch sử trạng thái.</p>
							</c:when>
							<c:otherwise>
								<div class="seller-timeline">
									<c:forEach var="history" items="${histories}">
										<div class="seller-timeline-item">
											<span class="seller-timeline-dot"></span>
											<div class="seller-timeline-content">
												<strong> <c:out
														value="${empty history.previousStatus ? 'Khởi tạo đơn hàng' : history.previousStatus}" />
													<c:if test="${not empty history.newStatus}">
                                                    → <c:out
															value="${history.newStatus}" />
													</c:if>
												</strong>
												<c:if test="${not empty history.note}">
													<p>
														<c:out value="${history.note}" />
													</p>
												</c:if>
												<span class="seller-table-subtitle"> <fmt:formatDate
														value="${history.createdAt}" pattern="dd/MM/yyyy HH:mm" />
													<c:if test="${not empty history.actorEmail}">
                                                    · <c:out
															value="${history.actorEmail}" />
													</c:if>
												</span>
											</div>
										</div>
									</c:forEach>
								</div>
							</c:otherwise>
						</c:choose>
					</section>
				</div>

				<aside class="seller-detail-sidebar">
					<section class="seller-panel">
						<h2>Trạng thái đơn hàng</h2>
						<p>
							<strong><c:out value="${order.orderStatus}" /></strong>
						</p>
						<p class="seller-muted">Chỉ thực hiện bước xử lý phù hợp với
							trạng thái hiện tại.</p>

						<c:choose>
							<c:when test="${order.orderStatus eq 'PENDING_SELLER'}">
								<form method="post"
									action="${pageContext.request.contextPath}/seller/orders/status"
									class="seller-status-form">
									<input type="hidden" name="${_csrf.parameterName}"
										value="${_csrf.token}"> <input type="hidden"
										name="shopId" value="${order.shopId}"> <input
										type="hidden" name="orderId" value="${order.orderId}">
									<input type="hidden" name="action" value="CONFIRM"> <label
										for="confirmNote">Ghi chú</label>
									<textarea id="confirmNote" name="note" rows="3"
										maxlength="1000"></textarea>
									<button type="submit"
										class="seller-button seller-button-primary seller-button-full">Xác
										nhận đơn</button>
								</form>
							</c:when>
							<c:when test="${order.orderStatus eq 'CONFIRMED'}">
								<form method="post"
									action="${pageContext.request.contextPath}/seller/orders/status"
									class="seller-status-form">
									<input type="hidden" name="${_csrf.parameterName}"
										value="${_csrf.token}"> <input type="hidden"
										name="shopId" value="${order.shopId}"> <input
										type="hidden" name="orderId" value="${order.orderId}">
									<input type="hidden" name="action" value="PREPARE"> <label
										for="prepareNote">Ghi chú</label>
									<textarea id="prepareNote" name="note" rows="3"
										maxlength="1000"></textarea>
									<button type="submit"
										class="seller-button seller-button-primary seller-button-full">Bắt
										đầu chuẩn bị</button>
								</form>
							</c:when>
							<c:when test="${order.orderStatus eq 'PREPARING'}">
								<form method="post"
									action="${pageContext.request.contextPath}/seller/orders/status"
									class="seller-status-form">
									<input type="hidden" name="${_csrf.parameterName}"
										value="${_csrf.token}"> <input type="hidden"
										name="shopId" value="${order.shopId}"> <input
										type="hidden" name="orderId" value="${order.orderId}">
									<input type="hidden" name="action" value="READY_TO_SHIP">
									<label for="readyNote">Ghi chú</label>
									<textarea id="readyNote" name="note" rows="3" maxlength="1000"></textarea>
									<button type="submit"
										class="seller-button seller-button-primary seller-button-full">Sẵn
										sàng giao hàng</button>
								</form>
							</c:when>
							<c:otherwise>
								<p class="seller-muted">Không có thao tác tiếp theo dành cho
									người bán ở trạng thái này.</p>
							</c:otherwise>
						</c:choose>
					</section>

					<section class="seller-panel">
						<h2>Thông tin người nhận</h2>
						<div class="seller-info-list">
							<div>
								<span>Họ tên</span><strong><c:out
										value="${order.recipientName}" /></strong>
							</div>
							<div>
								<span>Số điện thoại</span><strong><c:out
										value="${order.recipientPhone}" /></strong>
							</div>
							<div>
								<span>Email người mua</span><strong><c:out
										value="${order.buyerEmail}" /></strong>
							</div>
							<div>
								<span>Địa chỉ</span> <strong> <c:out
										value="${order.addressLine}" />, <c:out value="${order.ward}" />,
									<c:out value="${order.district}" />, <c:out
										value="${order.province}" />
								</strong>
							</div>
							<div>
								<span>Thanh toán</span><strong><c:out
										value="${order.paymentMethod}" /></strong>
							</div>
							<div>
								<span>Ngày đặt</span><strong><fmt:formatDate
										value="${order.createdAt}" pattern="dd/MM/yyyy HH:mm" /></strong>
							</div>
							<div>
								<span>Ghi chú người mua</span><strong><c:out
										value="${empty order.buyerNote ? 'Không có' : order.buyerNote}" /></strong>
							</div>
						</div>
					</section>
				</aside>
			</div>
		</main>
	</div>
</body>
</html>
