package com.qzy.springbootlogin.pojo;

import java.time.LocalDateTime;

public class Favorite {
    private Long id;
    private Long userId;
    private Integer productId;
    private String productName;
    private java.math.BigDecimal productPrice;
    private String productImage;
    private LocalDateTime createdTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Integer getProductId() { return productId; }
    public void setProductId(Integer productId) { this.productId = productId; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public java.math.BigDecimal getProductPrice() { return productPrice; }
    public void setProductPrice(java.math.BigDecimal productPrice) { this.productPrice = productPrice; }
    public String getProductImage() { return productImage; }
    public void setProductImage(String productImage) { this.productImage = productImage; }
    public LocalDateTime getCreatedTime() { return createdTime; }
    public void setCreatedTime(LocalDateTime createdTime) { this.createdTime = createdTime; }
}
