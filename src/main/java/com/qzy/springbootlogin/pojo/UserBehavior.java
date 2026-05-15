package com.qzy.springbootlogin.pojo;

import java.time.LocalDateTime;

/**
 * 用户行为记录 - 用于推荐系统
 */
public class UserBehavior {
    private Long id;
    private Long userId;
    private Integer productId;
    private String action; // view, search, add_cart, purchase
    private String keyword; // 搜索关键词
    private Integer duration; // 停留时长(秒)
    private LocalDateTime createdTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Integer getProductId() { return productId; }
    public void setProductId(Integer productId) { this.productId = productId; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public String getKeyword() { return keyword; }
    public void setKeyword(String keyword) { this.keyword = keyword; }
    public Integer getDuration() { return duration; }
    public void setDuration(Integer duration) { this.duration = duration; }
    public LocalDateTime getCreatedTime() { return createdTime; }
    public void setCreatedTime(LocalDateTime createdTime) { this.createdTime = createdTime; }
}
