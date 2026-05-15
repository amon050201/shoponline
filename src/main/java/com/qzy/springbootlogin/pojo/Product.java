package com.qzy.springbootlogin.pojo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品实体类
 */
public class Product implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Integer id;
    private String name;
    private String description;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private Integer stock;
    private Integer categoryId;
    private Integer merchantId; // 商家ID
    private String imageUrl;
    private String images;
    private String brand;
    private String model;
    private BigDecimal weight;
    private String dimensions;
    private Integer status;
    private Integer salesCount;
    private Integer viewCount;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
    
    // AI相关字段
    private String productCondition;
    private BigDecimal usageYears;
    private String accessories;
    private String aiTags;

    // 关联查询字段
    private String categoryName;

    public Product() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(BigDecimal originalPrice) {
        this.originalPrice = originalPrice;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }
    
    public Integer getMerchantId() {
        return merchantId;
    }
    
    public void setMerchantId(Integer merchantId) {
        this.merchantId = merchantId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public String getDimensions() {
        return dimensions;
    }

    public void setDimensions(String dimensions) {
        this.dimensions = dimensions;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getSalesCount() {
        return salesCount;
    }

    public void setSalesCount(Integer salesCount) {
        this.salesCount = salesCount;
    }

    public Integer getViewCount() {
        return viewCount;
    }

    public void setViewCount(Integer viewCount) {
        this.viewCount = viewCount;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    public LocalDateTime getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(LocalDateTime updatedTime) {
        this.updatedTime = updatedTime;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getProductCondition() { return productCondition; }
    public void setProductCondition(String productCondition) { this.productCondition = productCondition; }
    public BigDecimal getUsageYears() { return usageYears; }
    public void setUsageYears(BigDecimal usageYears) { this.usageYears = usageYears; }
    public String getAccessories() { return accessories; }
    public void setAccessories(String accessories) { this.accessories = accessories; }
    public String getAiTags() { return aiTags; }
    public void setAiTags(String aiTags) { this.aiTags = aiTags; }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", originalPrice=" + originalPrice +
                ", stock=" + stock +
                ", categoryId=" + categoryId +
                ", imageUrl='" + imageUrl + '\'' +
                ", brand='" + brand + '\'' +
                ", model='" + model + '\'' +
                ", status=" + status +
                ", salesCount=" + salesCount +
                ", viewCount=" + viewCount +
                ", categoryName='" + categoryName + '\'' +
                '}';
    }
}
