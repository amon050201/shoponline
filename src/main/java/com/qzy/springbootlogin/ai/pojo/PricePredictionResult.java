package com.qzy.springbootlogin.ai.pojo;

import java.math.BigDecimal;
import java.util.List;

public class PricePredictionResult {
    private Integer productId;
    private BigDecimal currentPrice;
    private BigDecimal predictedPrice7d;
    private BigDecimal predictedPrice30d;
    private BigDecimal predictedPrice90d;
    private String trend; // up, down, stable
    private Double confidence;
    private List<BigDecimal> movingAverage7d;
    private List<BigDecimal> movingAverage30d;

    public Integer getProductId() { return productId; }
    public void setProductId(Integer productId) { this.productId = productId; }
    public BigDecimal getCurrentPrice() { return currentPrice; }
    public void setCurrentPrice(BigDecimal currentPrice) { this.currentPrice = currentPrice; }
    public BigDecimal getPredictedPrice7d() { return predictedPrice7d; }
    public void setPredictedPrice7d(BigDecimal predictedPrice7d) { this.predictedPrice7d = predictedPrice7d; }
    public BigDecimal getPredictedPrice30d() { return predictedPrice30d; }
    public void setPredictedPrice30d(BigDecimal predictedPrice30d) { this.predictedPrice30d = predictedPrice30d; }
    public BigDecimal getPredictedPrice90d() { return predictedPrice90d; }
    public void setPredictedPrice90d(BigDecimal predictedPrice90d) { this.predictedPrice90d = predictedPrice90d; }
    public String getTrend() { return trend; }
    public void setTrend(String trend) { this.trend = trend; }
    public Double getConfidence() { return confidence; }
    public void setConfidence(Double confidence) { this.confidence = confidence; }
    public List<BigDecimal> getMovingAverage7d() { return movingAverage7d; }
    public void setMovingAverage7d(List<BigDecimal> movingAverage7d) { this.movingAverage7d = movingAverage7d; }
    public List<BigDecimal> getMovingAverage30d() { return movingAverage30d; }
    public void setMovingAverage30d(List<BigDecimal> movingAverage30d) { this.movingAverage30d = movingAverage30d; }
}
