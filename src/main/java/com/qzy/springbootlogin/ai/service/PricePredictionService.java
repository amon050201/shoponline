package com.qzy.springbootlogin.ai.service;

import com.qzy.springbootlogin.ai.pojo.PricePredictionResult;
import com.qzy.springbootlogin.ai.pojo.PriceHistory;

import java.util.List;

public interface PricePredictionService {
    PricePredictionResult predict(Integer productId, int forecastDays);
    void recordPriceSnapshot(Integer productId, java.math.BigDecimal price);
    List<PriceHistory> getPriceHistory(Integer productId, int days);
    void recordAllProductSnapshots();
}
