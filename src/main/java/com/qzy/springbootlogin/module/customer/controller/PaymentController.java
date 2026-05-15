package com.qzy.springbootlogin.module.customer.controller;

import com.qzy.springbootlogin.ai.pojo.PaymentResult;
import com.qzy.springbootlogin.ai.service.PaymentService;
import com.qzy.springbootlogin.ai.service.impl.DemoPaymentService;
import com.qzy.springbootlogin.pojo.Order;
import com.qzy.springbootlogin.pojo.Result;
import com.qzy.springbootlogin.service.OrderService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * 支付网关控制器
 *
 * 提供支付创建、回调、状态查询API
 */
@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Autowired
    private OrderService orderService;

    @Autowired
    @Qualifier("demoPaymentService")
    private PaymentService paymentService;

    /**
     * 创建支付
     * POST /api/payment/create
     * 参数: orderId, paymentMethod
     * 返回: 二维码URL + transactionId
     */
    @PostMapping("/create")
    public Result createPayment(@RequestParam Integer orderId,
                                @RequestParam(defaultValue = "alipay") String paymentMethod,
                                HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return Result.error("请先登录");
        }

        Order order = orderService.findById(orderId);
        if (order == null) {
            return Result.error("订单不存在");
        }
        if (order.getStatus() != 0) {
            return Result.error("订单状态不正确，无法支付");
        }
        if (!order.getUserId().equals(userId.intValue())) {
            return Result.error("无权操作此订单");
        }

        try {
            PaymentResult result = paymentService.createPayment(
                    order.getOrderNo(), order.getActualAmount(), paymentMethod);
            if (!result.isSuccess()) {
                return Result.error(result.getMessage());
            }

            // 更新订单的支付方式和transactionId
            orderService.updatePaymentInfo(order.getId(), result.getTransactionId(), paymentMethod);

            Map<String, Object> data = new HashMap<>();
            data.put("qrCodeUrl", result.getQrCodeUrl());
            data.put("transactionId", result.getTransactionId());
            data.put("orderNo", result.getOrderNo());
            data.put("amount", result.getAmount());
            data.put("paymentMethod", paymentMethod);
            return Result.success("创建支付成功", data);
        } catch (Exception e) {
            return Result.error("创建支付失败：" + e.getMessage());
        }
    }

    /**
     * 支付回调（演示模式 — 模拟支付平台通知）
     * POST /api/payment/callback
     */
    @PostMapping("/callback")
    public Result paymentCallback(@RequestParam String orderNo,
                                  @RequestParam String transactionId) {
        Map<String, String> params = new HashMap<>();
        params.put("orderNo", orderNo);
        params.put("transactionId", transactionId);

        boolean verified = paymentService.verifyCallback(params);
        if (!verified) {
            return Result.error("回调验证失败");
        }

        // 模拟支付成功
        if (paymentService instanceof DemoPaymentService demo) {
            demo.simulateSuccess(orderNo);
        }

        // 更新订单为已支付
        boolean paid = orderService.payOrder(orderNo, transactionId);
        if (paid) {
            return Result.success("支付成功");
        }
        return Result.error("订单状态更新失败");
    }

    /**
     * 查询支付状态
     * GET /api/payment/status/{orderNo}
     */
    @GetMapping("/status/{orderNo}")
    public Result queryStatus(@PathVariable String orderNo) {
        String status = paymentService.queryPaymentStatus(orderNo);
        Map<String, String> data = new HashMap<>();
        data.put("orderNo", orderNo);
        data.put("tradeStatus", status);
        return Result.success("查询成功", data);
    }
}
