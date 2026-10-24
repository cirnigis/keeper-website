package vn.ubm.usedbookmarketplace.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "Users", schema = "dbo")
public class AppUser {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "UserId")
	private Long userId;

	@Column(name = "Email", nullable = false, unique = true, length = 254)
	private String email;

	@Column(name = "PasswordHash", nullable = false, length = 255)
	private String passwordHash;

	@Column(name = "FullName", nullable = false, length = 150)
	private String fullName;

	@Column(name = "Phone", length = 20)
	private String phone;

	@Column(name = "AvatarUrl", length = 1000)
	private String avatarUrl;

	@Column(name = "AvatarPublicId", length = 255)
	private String avatarPublicId;

	@Column(name = "AccountStatus", nullable = false, length = 20)
	private String accountStatus = "PENDING_EMAIL";

	@Column(name = "EmailVerifiedAt")
	private LocalDateTime emailVerifiedAt;

	@Column(name = "LastLoginAt")
	private LocalDateTime lastLoginAt;

	@Column(name = "CreatedAt", insertable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(name = "UpdatedAt", insertable = false, updatable = false)
	private LocalDateTime updatedAt;

	@Version
	@JdbcTypeCode(SqlTypes.BINARY)
	@Column(name = "RowVersion", columnDefinition = "timestamp", insertable = false, updatable = false)
	private byte[] rowVersion;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "UserRoles", schema = "dbo", joinColumns = @JoinColumn(name = "UserId", referencedColumnName = "UserId"), inverseJoinColumns = @JoinColumn(name = "RoleId", referencedColumnName = "RoleId"))
	private Set<Role> roles = new HashSet<>();

	public AppUser() {
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getAvatarUrl() {
		return avatarUrl;
	}

	public void setAvatarUrl(String avatarUrl) {
		this.avatarUrl = avatarUrl;
	}

	public String getAvatarPublicId() {
		return avatarPublicId;
	}

	public void setAvatarPublicId(String avatarPublicId) {
		this.avatarPublicId = avatarPublicId;
	}

	public String getAccountStatus() {
		return accountStatus;
	}

	public void setAccountStatus(String accountStatus) {
		this.accountStatus = accountStatus;
	}

	public LocalDateTime getEmailVerifiedAt() {
		return emailVerifiedAt;
	}

	public void setEmailVerifiedAt(LocalDateTime emailVerifiedAt) {
		this.emailVerifiedAt = emailVerifiedAt;
	}

	public LocalDateTime getLastLoginAt() {
		return lastLoginAt;
	}

	public void setLastLoginAt(LocalDateTime lastLoginAt) {
		this.lastLoginAt = lastLoginAt;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public byte[] getRowVersion() {
		return rowVersion;
	}

	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}
}