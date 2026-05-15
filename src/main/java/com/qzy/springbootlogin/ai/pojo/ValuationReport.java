package com.qzy.springbootlogin.ai.pojo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

public class ValuationReport {
    private BigDecimal estimatedValue;
    private BigDecimal marketRangeLow;
    private BigDecimal marketRangeHigh;
    private String condition;
    private String marketAnalysis;
    private String recommendation;
    private Map<String, Object> factors;
    private LocalDateTime evaluatedAt = LocalDateTime.now();

    public BigDecimal getEstimatedValue() { return estimatedValue; }
    public void setEstimatedValue(BigDecimal estimatedValue) { this.estimatedValue = estimatedValue; }
    public BigDecimal getMarketRangeLow() { return marketRangeLow; }
    public void setMarketRangeLow(BigDecimal marketRangeLow) { this.marketRangeLow = marketRangeLow; }
    public BigDecimal getMarketRangeHigh() { return marketRangeHigh; }
    public void setMarketRangeHigh(BigDecimal marketRangeHigh) { this.marketRangeHigh = marketRangeHigh; }
    public String getCondition() { return condition; }
    public void setCondition(String condition) { this.condition = condition; }
    public String getMarketAnalysis() { return marketAnalysis; }
    public void setMarketAnalysis(String marketAnalysis) { this.marketAnalysis = marketAnalysis; }
    public String getRecommendation() { return recommendation; }
    public void setRecommendation(String recommendation) { this.recommendation = recommendation; }
    public Map<String, Object> getFactors() { return factors; }
    public void setFactors(Map<String, Object> factors) { this.factors = factors; }
    public LocalDateTime getEvaluatedAt() { return evaluatedAt; }
    public void setEvaluatedAt(LocalDateTime evaluatedAt) { this.evaluatedAt = evaluatedAt; }
}
