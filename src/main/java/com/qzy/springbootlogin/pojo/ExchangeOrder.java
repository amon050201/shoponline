package com.qzy.springbootlogin.pojo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 交换订单实体类 - 以物易物
 */
public class ExchangeOrder implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Integer id;
    private String orderNo; // 订单号
    private Long initiatorId; // 发起者ID
    private Long receiverId; // 接收者ID
    private Integer initiatorProductId; // 发起者商品ID
    private Integer receiverProductId; // 接收者商品ID
    private BigDecimal priceDifference; // 差价（正数表示发起者需补钱，负数表示接收者需补钱）
    private Integer status; // 状态：0-待确认，1-已确认，2-已完成，3-已取消，4-已拒绝
    private Integer paymentStatus; // 支付状态：0-未支付，1-已支付
    private String paymentMethod; // 支付方式
    private LocalDateTime paymentTime; // 支付时间
    private LocalDateTime confirmTime; // 确认时间
    private LocalDateTime completeTime; // 完成时间
    private String initiatorAddress; // 发起者地址
    private String receiverAddress; // 接收者地址
    private String remark; // 备注
    // AI估值相关字段
    private BigDecimal aiInitiatorValuation;
    private BigDecimal aiReceiverValuation;
    private BigDecimal recommendedDifference;
    private Double fraudRiskScore;

    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
    
    // 关联查询字段
    private User initiator; // 发起者信息
    private User receiver; // 接收者信息
    private Product initiatorProduct; // 发起者商品
    private Product receiverProduct; // 接收者商品

    public ExchangeOrder() {
        this.priceDifference = BigDecimal.ZERO;
        this.status = 0;
        this.paymentStatus = 0;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Long getInitiatorId() {
        return initiatorId;
    }

    public void setInitiatorId(Long initiatorId) {
        this.initiatorId = initiatorId;
    }

    public Long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }

    public Integer getInitiatorProductId() {
        return initiatorProductId;
    }

    public void setInitiatorProductId(Integer initiatorProductId) {
        this.initiatorProductId = initiatorProductId;
    }

    public Integer getReceiverProductId() {
        return receiverProductId;
    }

    public void setReceiverProductId(Integer receiverProductId) {
        this.receiverProductId = receiverProductId;
    }

    public BigDecimal getPriceDifference() {
        return priceDifference;
    }

    public void setPriceDifference(BigDecimal priceDifference) {
        this.priceDifference = priceDifference;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(Integer paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public LocalDateTime getPaymentTime() {
        return paymentTime;
    }

    public void setPaymentTime(LocalDateTime paymentTime) {
        this.paymentTime = paymentTime;
    }

    public LocalDateTime getConfirmTime() {
        return confirmTime;
    }

    public void setConfirmTime(LocalDateTime confirmTime) {
        this.confirmTime = confirmTime;
    }

    public LocalDateTime getCompleteTime() {
        return completeTime;
    }

    public void setCompleteTime(LocalDateTime completeTime) {
        this.completeTime = completeTime;
    }

    public String getInitiatorAddress() {
        return initiatorAddress;
    }

    public void setInitiatorAddress(String initiatorAddress) {
        this.initiatorAddress = initiatorAddress;
    }

    public String getReceiverAddress() {
        return receiverAddress;
    }

    public void setReceiverAddress(String receiverAddress) {
        this.receiverAddress = receiverAddress;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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

    public User getInitiator() {
        return initiator;
    }

    public void setInitiator(User initiator) {
        this.initiator = initiator;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public Product getInitiatorProduct() {
        return initiatorProduct;
    }

    public void setInitiatorProduct(Product initiatorProduct) {
        this.initiatorProduct = initiatorProduct;
    }

    public Product getReceiverProduct() {
        return receiverProduct;
    }

    public void setReceiverProduct(Product receiverProduct) {
        this.receiverProduct = receiverProduct;
    }

    /**
     * 获取状态文本
     */
    public String getStatusText() {
        switch (this.status) {
            case 0: return "待确认";
            case 1: return "已确认";
            case 2: return "已完成";
            case 3: return "已取消";
            case 4: return "已拒绝";
            default: return "未知";
        }
    }

    /**
     * 获取支付状态文本
     */
    public String getPaymentStatusText() {
        return this.paymentStatus == 1 ? "已支付" : "未支付";
    }

    public BigDecimal getAiInitiatorValuation() { return aiInitiatorValuation; }
    public void setAiInitiatorValuation(BigDecimal aiInitiatorValuation) { this.aiInitiatorValuation = aiInitiatorValuation; }
    public BigDecimal getAiReceiverValuation() { return aiReceiverValuation; }
    public void setAiReceiverValuation(BigDecimal aiReceiverValuation) { this.aiReceiverValuation = aiReceiverValuation; }
    public BigDecimal getRecommendedDifference() { return recommendedDifference; }
    public void setRecommendedDifference(BigDecimal recommendedDifference) { this.recommendedDifference = recommendedDifference; }
    public Double getFraudRiskScore() { return fraudRiskScore; }
    public void setFraudRiskScore(Double fraudRiskScore) { this.fraudRiskScore = fraudRiskScore; }

    @Override
    public String toString() {
        return "ExchangeOrder{" +
                "id=" + id +
                ", orderNo='" + orderNo + '\'' +
                ", initiatorId=" + initiatorId +
                ", receiverId=" + receiverId +
                ", priceDifference=" + priceDifference +
                ", status=" + status +
                ", paymentStatus=" + paymentStatus +
                '}';
    }
}
