package com.qzy.springbootlogin.ai.service.impl;

import com.qzy.springbootlogin.ai.pojo.PaymentResult;
import com.qzy.springbootlogin.ai.service.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 演示支付服务 — 模拟支付宝/微信支付流程
 *
 * 生产环境替换为 AlipayPaymentService / WechatPaymentService：
 *   注入 OkHttpClient，调用对应支付API获取二维码
 */
@Service
public class DemoPaymentService implements PaymentService {

    private static final Logger log = LoggerFactory.getLogger(DemoPaymentService.class);

    /** 模拟交易状态存储: orderNo -> tradeStatus */
    private final ConcurrentHashMap<String, String> paymentStore = new ConcurrentHashMap<>();

    @Override
    public PaymentResult createPayment(String orderNo, BigDecimal amount, String paymentMethod) {
        String transactionId = generateTransactionId(paymentMethod);
        String qrCodeUrl = buildQrCodeUrl(transactionId, amount);

        paymentStore.put(orderNo, "WAITING");

        log.info("[DemoPayment] 创建支付: orderNo={}, amount={}, method={}, transactionId={}",
                orderNo, amount, paymentMethod, transactionId);

        return PaymentResult.ok(transactionId, orderNo, amount, paymentMethod, qrCodeUrl);
    }

    @Override
    public boolean verifyCallback(Map<String, String> params) {
        String orderNo = params.get("orderNo");
        String transactionId = params.get("transactionId");
        if (orderNo == null || transactionId == null) {
            log.warn("[DemoPayment] 回调验证失败: 参数不完整");
            return false;
        }
        log.info("[DemoPayment] 回调验证通过: orderNo={}, transactionId={}", orderNo, transactionId);
        return true;
    }

    @Override
    public boolean refund(String orderNo, BigDecimal amount) {
        String status = paymentStore.get(orderNo);
        if (status == null || !"SUCCESS".equals(status)) {
            log.warn("[DemoPayment] 退款失败: orderNo={} 状态={}", orderNo, status);
            return false;
        }
        paymentStore.put(orderNo, "REFUNDED");
        log.info("[DemoPayment] 退款成功: orderNo={}, amount={}", orderNo, amount);
        return true;
    }

    @Override
    public String queryPaymentStatus(String orderNo) {
        return paymentStore.getOrDefault(orderNo, "NOT_FOUND");
    }

    /**
     * 模拟支付成功（供演示回调使用）
     */
    public void simulateSuccess(String orderNo) {
        paymentStore.put(orderNo, "SUCCESS");
        log.info("[DemoPayment] 模拟支付成功: orderNo={}", orderNo);
    }

    private String generateTransactionId(String method) {
        String prefix = "alipay".equals(method) ? "ALI" : "WECHAT".equals(method) ? "WX" : "DEMO";
        return prefix + System.currentTimeMillis();
    }

    private String buildQrCodeUrl(String transactionId, BigDecimal amount) {
        return "https://api.qrserver.com/v1/create-qr-code/?size=200x200&data="
                + transactionId + "|" + amount;
    }
}
