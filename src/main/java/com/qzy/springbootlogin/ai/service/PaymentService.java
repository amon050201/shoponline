package com.qzy.springbootlogin.ai.service;

import com.qzy.springbootlogin.ai.pojo.PaymentResult;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 支付服务接口
 * 支持支付宝、微信支付等渠道
 */
public interface PaymentService {

    /**
     * 创建支付
     * @param orderNo 订单号
     * @param amount 支付金额
     * @param paymentMethod 支付方式 (alipay/wechat)
     * @return 支付结果（含二维码URL）
     */
    PaymentResult createPayment(String orderNo, BigDecimal amount, String paymentMethod);

    /**
     * 验证支付回调
     * @param params 回调参数
     * @return 验证是否通过
     */
    boolean verifyCallback(Map<String, String> params);

    /**
     * 发起退款
     * @param orderNo 订单号
     * @param amount 退款金额
     * @return 是否成功
     */
    boolean refund(String orderNo, BigDecimal amount);

    /**
     * 查询支付状态
     * @param orderNo 订单号
     * @return 交易状态
     */
    String queryPaymentStatus(String orderNo);
}
