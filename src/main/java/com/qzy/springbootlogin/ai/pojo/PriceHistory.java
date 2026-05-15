package com.qzy.springbootlogin.ai.pojo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PriceHistory {
    private Long id;
    private Integer productId;
    private BigDecimal price;
    private LocalDateTime recordedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Integer getProductId() { return productId; }
    public void setProductId(Integer productId) { this.productId = productId; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public LocalDateTime getRecordedAt() { return recordedAt; }
    public void setRecordedAt(LocalDateTime recordedAt) { this.recordedAt = recordedAt; }
}
