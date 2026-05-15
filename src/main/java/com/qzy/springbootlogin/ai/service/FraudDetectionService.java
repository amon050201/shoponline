package com.qzy.springbootlogin.ai.service;

import com.qzy.springbootlogin.ai.pojo.FraudAlert;

import java.util.List;

public interface FraudDetectionService {
    FraudAlert evaluateOrder(Integer orderId);
    FraudAlert evaluateExchangeOrder(Integer exchangeOrderId);
    List<FraudAlert> getUserRiskProfile(Long userId);
    List<FraudAlert> getUnresolvedAlerts();
    List<FraudAlert> getAllAlerts();
    void resolveAlert(Long alertId);
}
