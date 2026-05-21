package com.qzy.springbootlogin.ai.service.impl;

import com.qzy.springbootlogin.ai.mapper.PriceHistoryMapper;
import com.qzy.springbootlogin.ai.pojo.PriceHistory;
import com.qzy.springbootlogin.ai.pojo.PricePredictionResult;
import com.qzy.springbootlogin.ai.service.PricePredictionService;
import com.qzy.springbootlogin.mapper.ProductMapper;
import com.qzy.springbootlogin.pojo.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PricePredictionServiceImpl implements PricePredictionService {

    private static final Logger log = LoggerFactory.getLogger(PricePredictionServiceImpl.class);

    @Autowired
    private PriceHistoryMapper priceHistoryMapper;

    @Autowired
    private ProductMapper productMapper;

    @Override
    public PricePredictionResult predict(Integer productId, int forecastDays) {
        PricePredictionResult result = new PricePredictionResult();
        result.setProductId(productId);

        Product product = productMapper.findById(productId);
        if (product == null) return result;

        BigDecimal currentPrice = product.getPrice();
        result.setCurrentPrice(currentPrice);

        // Get historical prices for 90 days
        List<PriceHistory> history = priceHistoryMapper.findByProductIdAndDateRange(
                productId, LocalDateTime.now().minusDays(90));

        List<BigDecimal> prices = history.stream()
                .map(PriceHistory::getPrice)
                .collect(Collectors.toList());
        prices.add(currentPrice);

        // Calculate simple moving averages
        List<BigDecimal> ma7 = calculateSMA(prices, 7);
        List<BigDecimal> ma30 = calculateSMA(prices, 30);
        result.setMovingAverage7d(ma7);
        result.setMovingAverage30d(ma30);

        // Record initial snapshot if no history
        if (history.isEmpty()) {
            recordPriceSnapshot(productId, currentPrice);
            prices.add(currentPrice);
        }

        // Linear regression for trend prediction
        int n = prices.size();
        if (n < 2) {
            try {
                BigDecimal catAvg = productMapper.getAveragePriceByCategory(product.getCategoryId());
                if (catAvg != null && catAvg.compareTo(BigDecimal.ZERO) > 0) {
                    double ratio = currentPrice.doubleValue() / catAvg.doubleValue();
                    result.setTrend(ratio > 1.1 ? "down" : ratio < 0.9 ? "up" : "stable");
                    result.setConfidence(0.3);
                    result.setPredictedPrice7d(currentPrice.multiply(BigDecimal.valueOf(0.99)).setScale(2, RoundingMode.HALF_UP));
                    result.setPredictedPrice30d(currentPrice.multiply(BigDecimal.valueOf(0.97)).setScale(2, RoundingMode.HALF_UP));
                    result.setPredictedPrice90d(currentPrice.multiply(BigDecimal.valueOf(0.95)).setScale(2, RoundingMode.HALF_UP));
                    result.setTrendAnalysis("基于同品类均价的统计预测，仅供参考");
                    return result;
                }
            } catch (Exception e) { /* fall through */ }
            result.setTrend("stable");
            result.setConfidence(0.1);
            result.setPredictedPrice7d(currentPrice);
            result.setPredictedPrice30d(currentPrice);
            result.setPredictedPrice90d(currentPrice);
            result.setTrendAnalysis("缺乏足够历史数据，预测可信度较低");
            return result;
        }

        // Simple linear regression: y = a + bx
        double sumX = 0, sumY = 0, sumXY = 0, sumX2 = 0;
        for (int i = 0; i < n; i++) {
            double x = i;
            double y = prices.get(i).doubleValue();
            sumX += x;
            sumY += y;
            sumXY += x * y;
            sumX2 += x * x;
        }
        double slope = (n * sumXY - sumX * sumY) / (n * sumX2 - sumX * sumX);
        double intercept = (sumY - slope * sumX) / n;

        // Predict future prices
        BigDecimal pred7d = BigDecimal.valueOf(slope * (n + 7) + intercept);
        BigDecimal pred30d = BigDecimal.valueOf(slope * (n + 30) + intercept);
        BigDecimal pred90d = BigDecimal.valueOf(slope * (n + 90) + intercept);

        result.setPredictedPrice7d(pred7d.compareTo(BigDecimal.ZERO) > 0 ? pred7d.setScale(2, RoundingMode.HALF_UP) : currentPrice);
        result.setPredictedPrice30d(pred30d.compareTo(BigDecimal.ZERO) > 0 ? pred30d.setScale(2, RoundingMode.HALF_UP) : currentPrice);
        result.setPredictedPrice90d(pred90d.compareTo(BigDecimal.ZERO) > 0 ? pred90d.setScale(2, RoundingMode.HALF_UP) : currentPrice);

        // Determine trend
        double changeRatio = (pred90d.doubleValue() - currentPrice.doubleValue()) / currentPrice.doubleValue();
        if (changeRatio > 0.05) result.setTrend("up");
        else if (changeRatio < -0.05) result.setTrend("down");
        else result.setTrend("stable");

        // Confidence based on data amount
        result.setConfidence(Math.min(1.0, n / 30.0));

        return result;
    }

    @Override
    public void recordPriceSnapshot(Integer productId, BigDecimal price) {
        PriceHistory history = new PriceHistory();
        history.setProductId(productId);
        history.setPrice(price);
        history.setRecordedAt(LocalDateTime.now());
        priceHistoryMapper.insert(history);
    }

    @Override
    public List<PriceHistory> getPriceHistory(Integer productId, int days) {
        return priceHistoryMapper.findByProductIdAndDateRange(
                productId, LocalDateTime.now().minusDays(days));
    }

    @Override
    @Scheduled(cron = "0 0 2 * * *") // daily at 2am
    public void recordAllProductSnapshots() {
        List<Product> allProducts = productMapper.findAll();
        for (Product p : allProducts) {
            try {
                recordPriceSnapshot(p.getId(), p.getPrice());
            } catch (Exception e) {
                log.error("Failed to record price snapshot for product {}", p.getId(), e);
            }
        }
        log.info("Recorded price snapshots for {} products", allProducts.size());
    }

    private List<BigDecimal> calculateSMA(List<BigDecimal> prices, int window) {
        List<BigDecimal> sma = new ArrayList<>();
        for (int i = 0; i < prices.size(); i++) {
            if (i < window - 1) {
                sma.add(null);
                continue;
            }
            BigDecimal sum = BigDecimal.ZERO;
            for (int j = i - window + 1; j <= i; j++) {
                sum = sum.add(prices.get(j));
            }
            sma.add(sum.divide(BigDecimal.valueOf(window), 2, RoundingMode.HALF_UP));
        }
        return sma;
    }
}
