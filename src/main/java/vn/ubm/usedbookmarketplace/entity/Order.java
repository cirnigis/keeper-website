package vn.ubm.usedbookmarketplace.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;


@Entity
@Table(name = "Orders", schema = "dbo", uniqueConstraints = {
		@UniqueConstraint(name = "UQ_Orders_OrderCode", columnNames = "OrderCode") })
public class Order {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "OrderId")
	private Long orderId;

	@Column(name = "OrderCode", nullable = false, length = 32)
	private String orderCode;

	@Column(name = "BuyerUserId", nullable = false)
	private Long buyerUserId;

	@Column(name = "ShopId", nullable = false)
	private Long shopId;

	@Column(name = "OrderStatus", nullable = false, length = 30)
	private String orderStatus;

	@Column(name = "PaymentMethod", nullable = false, length = 20)
	private String paymentMethod;

	@Column(name = "Subtotal", nullable = false, precision = 18, scale = 2)
	private BigDecimal subtotal;

	@Column(name = "DiscountAmount", nullable = false, precision = 18, scale = 2)
	private BigDecimal discountAmount;

	@Column(name = "ShippingFee", nullable = false, precision = 18, scale = 2)
	private BigDecimal shippingFee;

	@Column(name = "PlatformCommission", nullable = false, precision = 18, scale = 2)
	private BigDecimal platformCommission;

	@Column(name = "TotalAmount", nullable = false, precision = 18, scale = 2)
	private BigDecimal totalAmount;

	@Column(name = "CouponId")
	private Long couponId;

	@Column(name = "CouponCodeSnapshot", length = 50)
	private String couponCodeSnapshot;

	@Column(name = "ShippingProviderId")
	private Long shippingProviderId;

	@Column(name = "RecipientName", nullable = false, length = 150)
	private String recipientName;

	@Column(name = "RecipientPhone", nullable = false, length = 20)
	private String recipientPhone;

	@Column(name = "Province", nullable = false, length = 100)
	private String province;

	@Column(name = "District", nullable = false, length = 100)
	private String district;

	@Column(name = "Ward", nullable = false, length = 100)
	private String ward;

	@Column(name = "AddressLine", nullable = false, length = 300)
	private String addressLine;

	@Column(name = "BuyerNote", length = 1000)
	private String buyerNote;

	@Column(name = "SellerConfirmedAt")
	private LocalDateTime sellerConfirmedAt;

	@Column(name = "DeliveredAt")
	private LocalDateTime deliveredAt;

	@Column(name = "CompletedAt")
	private LocalDateTime completedAt;

	@Column(name = "CanceledAt")
	private LocalDateTime canceledAt;

	@Column(name = "CreatedAt", nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "UpdatedAt", nullable = false)
	private LocalDateTime updatedAt;



	public Order() {
	}

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public String getOrderCode() {
		return orderCode;
	}

	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}

	public Long getBuyerUserId() {
		return buyerUserId;
	}

	public void setBuyerUserId(Long buyerUserId) {
		this.buyerUserId = buyerUserId;
	}

	public Long getShopId() {
		return shopId;
	}

	public void setShopId(Long shopId) {
		this.shopId = shopId;
	}

	public String getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}

	public String getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public BigDecimal getSubtotal() {
		return subtotal;
	}

	public void setSubtotal(BigDecimal subtotal) {
		this.subtotal = subtotal;
	}

	public BigDecimal getDiscountAmount() {
		return discountAmount;
	}

	public void setDiscountAmount(BigDecimal discountAmount) {
		this.discountAmount = discountAmount;
	}

	public BigDecimal getShippingFee() {
		return shippingFee;
	}

	public void setShippingFee(BigDecimal shippingFee) {
		this.shippingFee = shippingFee;
	}

	public BigDecimal getPlatformCommission() {
		return platformCommission;
	}

	public void setPlatformCommission(BigDecimal platformCommission) {
		this.platformCommission = platformCommission;
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	public Long getCouponId() {
		return couponId;
	}

	public void setCouponId(Long couponId) {
		this.couponId = couponId;
	}

	public String getCouponCodeSnapshot() {
		return couponCodeSnapshot;
	}

	public void setCouponCodeSnapshot(String couponCodeSnapshot) {
		this.couponCodeSnapshot = couponCodeSnapshot;
	}

	public Long getShippingProviderId() {
		return shippingProviderId;
	}

	public void setShippingProviderId(Long shippingProviderId) {
		this.shippingProviderId = shippingProviderId;
	}

	public String getRecipientName() {
		return recipientName;
	}

	public void setRecipientName(String recipientName) {
		this.recipientName = recipientName;
	}

	public String getRecipientPhone() {
		return recipientPhone;
	}

	public void setRecipientPhone(String recipientPhone) {
		this.recipientPhone = recipientPhone;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public String getWard() {
		return ward;
	}

	public void setWard(String ward) {
		this.ward = ward;
	}

	public String getAddressLine() {
		return addressLine;
	}

	public void setAddressLine(String addressLine) {
		this.addressLine = addressLine;
	}

	public String getBuyerNote() {
		return buyerNote;
	}

	public void setBuyerNote(String buyerNote) {
		this.buyerNote = buyerNote;
	}

	public LocalDateTime getSellerConfirmedAt() {
		return sellerConfirmedAt;
	}

	public void setSellerConfirmedAt(LocalDateTime sellerConfirmedAt) {
		this.sellerConfirmedAt = sellerConfirmedAt;
	}

	public LocalDateTime getDeliveredAt() {
		return deliveredAt;
	}

	public void setDeliveredAt(LocalDateTime deliveredAt) {
		this.deliveredAt = deliveredAt;
	}

	public LocalDateTime getCompletedAt() {
		return completedAt;
	}

	public void setCompletedAt(LocalDateTime completedAt) {
		this.completedAt = completedAt;
	}

	public LocalDateTime getCanceledAt() {
		return canceledAt;
	}

	public void setCanceledAt(LocalDateTime canceledAt) {
		this.canceledAt = canceledAt;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}


}