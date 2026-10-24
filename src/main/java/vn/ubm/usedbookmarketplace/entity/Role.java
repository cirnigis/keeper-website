package vn.ubm.usedbookmarketplace.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Roles", schema = "dbo")
public class Role {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "RoleId")
	private Integer roleId;

	@Column(name = "RoleCode", nullable = false, unique = true, length = 30)
	private String roleCode;

	@Column(name = "RoleName", nullable = false, length = 100)
	private String roleName;

	@Column(name = "CreatedAt", insertable = false, updatable = false)
	private LocalDateTime createdAt;

	public Role() {
	}

	public Integer getRoleId() {
		return roleId;
	}

	public void setRoleId(Integer roleId) {
		this.roleId = roleId;
	}

	public String getRoleCode() {
		return roleCode;
	}

	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
}