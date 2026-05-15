package com.qzy.springbootlogin.ai.controller;

import com.qzy.springbootlogin.ai.pojo.FraudAlert;
import com.qzy.springbootlogin.ai.service.FraudDetectionService;
import com.qzy.springbootlogin.pojo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/fraud")
public class AiFraudDetectionController {

    @Autowired
    private FraudDetectionService fraudDetectionService;

    @GetMapping("/alerts")
    public Result getAlerts(@RequestParam(defaultValue = "false") boolean unresolvedOnly) {
        try {
            List<FraudAlert> alerts;
            if (unresolvedOnly) {
                alerts = fraudDetectionService.getUnresolvedAlerts();
            } else {
                alerts = fraudDetectionService.getAllAlerts();
            }
            return Result.success("获取成功", alerts);
        } catch (Exception e) {
            return Result.error("获取欺诈预警失败：" + e.getMessage());
        }
    }

    @PostMapping("/resolve/{id}")
    public Result resolveAlert(@PathVariable Long id) {
        try {
            fraudDetectionService.resolveAlert(id);
            return Result.success("预警已处理");
        } catch (Exception e) {
            return Result.error("处理失败：" + e.getMessage());
        }
    }

    @GetMapping("/user-risk/{userId}")
    public Result getUserRisk(@PathVariable Long userId) {
        try {
            List<FraudAlert> alerts = fraudDetectionService.getUserRiskProfile(userId);
            return Result.success("获取成功", alerts);
        } catch (Exception e) {
            return Result.error("获取用户风险信息失败：" + e.getMessage());
        }
    }

    @PostMapping("/evaluate-order/{orderId}")
    public Result evaluateOrder(@PathVariable Integer orderId) {
        try {
            FraudAlert alert = fraudDetectionService.evaluateOrder(orderId);
            if (alert != null) {
                return Result.success("订单欺诈评估完成，存在风险", alert);
            }
            return Result.success("订单欺诈评估完成，无风险");
        } catch (Exception e) {
            return Result.error("评估失败：" + e.getMessage());
        }
    }

    @PostMapping("/evaluate-exchange/{exchangeOrderId}")
    public Result evaluateExchange(@PathVariable Integer exchangeOrderId) {
        try {
            FraudAlert alert = fraudDetectionService.evaluateExchangeOrder(exchangeOrderId);
            if (alert != null) {
                return Result.success("交易欺诈评估完成，存在风险", alert);
            }
            return Result.success("交易欺诈评估完成，无风险");
        } catch (Exception e) {
            return Result.error("评估失败：" + e.getMessage());
        }
    }
}
