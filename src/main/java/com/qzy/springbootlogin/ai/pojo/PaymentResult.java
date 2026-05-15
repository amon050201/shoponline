package com.qzy.springbootlogin.ai.pojo;

import java.math.BigDecimal;

public class PaymentResult {
    private boolean success;
    private String transactionId;
    private String orderNo;
    private BigDecimal amount;
    private String paymentMethod;
    private String qrCodeUrl;
    private String tradeStatus;
    private String message;

    public PaymentResult() {}

    public PaymentResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public static PaymentResult ok(String transactionId, String orderNo, BigDecimal amount,
                                   String paymentMethod, String qrCodeUrl) {
        PaymentResult r = new PaymentResult(true, "创建支付成功");
        r.transactionId = transactionId;
        r.orderNo = orderNo;
        r.amount = amount;
        r.paymentMethod = paymentMethod;
        r.qrCodeUrl = qrCodeUrl;
        r.tradeStatus = "WAITING";
        return r;
    }

    public static PaymentResult fail(String message) {
        return new PaymentResult(false, message);
    }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
    public String getOrderNo() { return orderNo; }
    public void setOrderNo(String orderNo) { this.orderNo = orderNo; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public String getQrCodeUrl() { return qrCodeUrl; }
    public void setQrCodeUrl(String qrCodeUrl) { this.qrCodeUrl = qrCodeUrl; }
    public String getTradeStatus() { return tradeStatus; }
    public void setTradeStatus(String tradeStatus) { this.tradeStatus = tradeStatus; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
