package com.qzy.springbootlogin.pojo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class UserCoupon {
    private Long id;
    private Long userId;
    private Long couponId;
    private String couponName;
    private String couponType;
    private BigDecimal couponValue;
    private BigDecimal minAmount;
    private Integer status;
    private LocalDateTime receivedTime;
    private LocalDateTime usedTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getCouponId() { return couponId; }
    public void setCouponId(Long couponId) { this.couponId = couponId; }
    public String getCouponName() { return couponName; }
    public void setCouponName(String couponName) { this.couponName = couponName; }
    public String getCouponType() { return couponType; }
    public void setCouponType(String couponType) { this.couponType = couponType; }
    public BigDecimal getCouponValue() { return couponValue; }
    public void setCouponValue(BigDecimal couponValue) { this.couponValue = couponValue; }
    public BigDecimal getMinAmount() { return minAmount; }
    public void setMinAmount(BigDecimal minAmount) { this.minAmount = minAmount; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public LocalDateTime getReceivedTime() { return receivedTime; }
    public void setReceivedTime(LocalDateTime receivedTime) { this.receivedTime = receivedTime; }
    public LocalDateTime getUsedTime() { return usedTime; }
    public void setUsedTime(LocalDateTime usedTime) { this.usedTime = usedTime; }
}
