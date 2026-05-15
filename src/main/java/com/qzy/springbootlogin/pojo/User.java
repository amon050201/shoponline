package com.qzy.springbootlogin.pojo;

import java.io.Serializable;
import java.util.Arrays;

/**
 * 用户实体类 - POJO层
 */
public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Long id;
    private String username;
    private String passwordHash; // 对应数据库中的 password_hash 字段
    private String email;
    private String phone;
    private Integer roleType; // 角色类型：0-顾客，1-商家，2-管理员
    private Integer status; // 状态：0-禁用，1-启用
    private java.time.LocalDateTime createdAt;
    private java.time.LocalDateTime updatedAt;

    public User() {
    }

    public User(String username, String passwordHash) {
        this.username = username;
        this.passwordHash = passwordHash;
    }

    public User(String username, String passwordHash, Integer roleType, Integer status) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.roleType = roleType;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Integer getRoleType() {
        return roleType;
    }

    public void setRoleType(Integer roleType) {
        this.roleType = roleType;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public java.time.LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(java.time.LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public java.time.LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(java.time.LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    /**
     * 获取角色名称
     */
    public String getRoleName() {
        return UserRole.getNameByCode(this.roleType != null ? this.roleType : 0);
    }
    
    /**
     * 判断是否为顾客
     */
    public boolean isCustomer() {
        return this.roleType != null && this.roleType == 0;
    }
    
    /**
     * 判断是否为商家
     */
    public boolean isMerchant() {
        return this.roleType != null && this.roleType == 1;
    }
    
    /**
     * 判断是否为管理员
     */
    public boolean isAdmin() {
        return this.roleType != null && this.roleType == 2;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", roleType=" + roleType +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
