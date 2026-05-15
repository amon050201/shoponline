package com.qzy.springbootlogin.ai.service.impl;

import com.qzy.springbootlogin.ai.mapper.FraudAlertMapper;
import com.qzy.springbootlogin.ai.pojo.FraudAlert;
import com.qzy.springbootlogin.ai.service.FraudDetectionService;
import com.qzy.springbootlogin.mapper.ExchangeOrderMapper;
import com.qzy.springbootlogin.mapper.OrderMapper;
import com.qzy.springbootlogin.mapper.UserMapper;
import com.qzy.springbootlogin.pojo.ExchangeOrder;
import com.qzy.springbootlogin.pojo.Order;
import com.qzy.springbootlogin.pojo.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FraudDetectionServiceImpl implements FraudDetectionService {

    private static final Logger log = LoggerFactory.getLogger(FraudDetectionServiceImpl.class);

    @Autowired
    private FraudAlertMapper fraudAlertMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private ExchangeOrderMapper exchangeOrderMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public FraudAlert evaluateOrder(Integer orderId) {
        Order order = orderMapper.findById(orderId);
        if (order == null) return null;

        Long userId = order.getUserId().longValue();
        FraudAlert highestAlert = null;

        // Rule 1: Rapid ordering (>5 orders in 10 minutes)
        LocalDateTime tenMinAgo = LocalDateTime.now().minusMinutes(10);
        int recentCount = orderMapper.countRecentOrders(userId, 10);
        if (recentCount > 5) {
            String desc = String.format("用户 %d 在10分钟内创建了 %d 个订单", userId, recentCount);
            highestAlert = createAlert(userId, "rapid_order", desc, 0.7 + (recentCount - 5) * 0.05, 2, orderId.toString());
        }

        // Rule 2: High cancellation rate (>40% in 30 days)
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        int total30 = orderMapper.countByUserIdSince(userId, thirtyDaysAgo);
        int cancelled30 = orderMapper.countCancelledByUserIdSince(userId, thirtyDaysAgo);
        if (total30 > 10 && (double) cancelled30 / total30 > 0.4) {
            double rate = (double) cancelled30 / total30;
            String desc = String.format("用户 %d 近30天取消率 %.0f%% (%d/%d)", userId, rate * 100, cancelled30, total30);
            FraudAlert alert = createAlert(userId, "high_cancel_rate", desc, rate, 2, orderId.toString());
            if (highestAlert == null || alert.getRiskScore() > highestAlert.getRiskScore()) highestAlert = alert;
        }

        return highestAlert;
    }

    @Override
    public FraudAlert evaluateExchangeOrder(Integer exchangeOrderId) {
        ExchangeOrder exOrder = exchangeOrderMapper.findById(exchangeOrderId);
        if (exOrder == null) return null;

        FraudAlert highestAlert = null;

        // Rule 3: Self-exchange (same user on both sides)
        if (exOrder.getInitiatorId().equals(exOrder.getReceiverId())) {
            String desc = String.format("交换订单 %s 发起者和接收者是同一用户", exOrder.getOrderNo());
            highestAlert = createAlert(exOrder.getInitiatorId(), "self_exchange", desc, 1.0, 3, exchangeOrderId.toString());
        }

        // Rule 4: Price anomaly (>300% of average exchange diff)
        if (exOrder.getPriceDifference() != null) {
            double diff = Math.abs(exOrder.getPriceDifference().doubleValue());
            // Get average difference from recent exchanges
            Double avgDiff = exchangeOrderMapper.getAveragePriceDifference();
            if (avgDiff != null && avgDiff > 0 && diff > avgDiff * 3) {
                String desc = String.format("交换 %s 差价 ¥%.2f 是平均值的 %.1f 倍",
                        exOrder.getOrderNo(), diff, diff / avgDiff);
                FraudAlert alert = createAlert(exOrder.getInitiatorId(), "exchange_anomaly", desc,
                        Math.min(0.9, diff / (avgDiff * 5)), 2, exchangeOrderId.toString());
                if (highestAlert == null || alert.getRiskScore() > highestAlert.getRiskScore()) highestAlert = alert;
            }
        }

        // Rule 5: Excessive exchange requests from same user
        int exchangeCount = exchangeOrderMapper.countByInitiatorSince(exOrder.getInitiatorId(), LocalDateTime.now().minusDays(1));
        if (exchangeCount > 10) {
            String desc = String.format("用户 %d 24小时内发起了 %d 次交换请求", exOrder.getInitiatorId(), exchangeCount);
            FraudAlert alert = createAlert(exOrder.getInitiatorId(), "exchange_anomaly", desc,
                    Math.min(0.9, exchangeCount * 0.08), 2, exchangeOrderId.toString());
            if (highestAlert == null || alert.getRiskScore() > highestAlert.getRiskScore()) highestAlert = alert;
        }

        return highestAlert;
    }

    @Override
    public List<FraudAlert> getUserRiskProfile(Long userId) {
        return fraudAlertMapper.findByUserId(userId);
    }

    @Override
    public List<FraudAlert> getUnresolvedAlerts() {
        return fraudAlertMapper.findUnresolved();
    }

    @Override
    public List<FraudAlert> getAllAlerts() {
        return fraudAlertMapper.findAll();
    }

    @Override
    public void resolveAlert(Long alertId) {
        fraudAlertMapper.resolveAlert(alertId);
    }

    private FraudAlert createAlert(Long userId, String type, String desc, double riskScore, int severity, String refId) {
        // Check if similar unresolved alert exists
        List<FraudAlert> existing = fraudAlertMapper.findByUserId(userId);
        for (FraudAlert a : existing) {
            if (!a.getResolved() && type.equals(a.getAlertType())) {
                return a; // deduplicate
            }
        }
        FraudAlert alert = new FraudAlert();
        alert.setUserId(userId);
        alert.setAlertType(type);
        alert.setDescription(desc);
        alert.setRiskScore(riskScore);
        alert.setSeverity(severity);
        alert.setResolved(false);
        alert.setReferenceId(refId);
        fraudAlertMapper.insert(alert);
        log.warn("Fraud alert created: {} (user={}, score={})", type, userId, riskScore);
        return alert;
    }
}
