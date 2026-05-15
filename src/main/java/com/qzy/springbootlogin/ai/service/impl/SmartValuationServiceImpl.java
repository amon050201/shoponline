package com.qzy.springbootlogin.ai.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qzy.springbootlogin.ai.pojo.ValuationReport;
import com.qzy.springbootlogin.ai.service.AiProviderSelector;
import com.qzy.springbootlogin.ai.service.SmartValuationService;
import com.qzy.springbootlogin.mapper.ProductMapper;
import com.qzy.springbootlogin.pojo.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

@Service
public class SmartValuationServiceImpl implements SmartValuationService {

    private static final Logger log = LoggerFactory.getLogger(SmartValuationServiceImpl.class);

    @Autowired
    private AiProviderSelector aiProvider;

    @Autowired
    private ProductMapper productMapper;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public ValuationReport valuate(Integer productId, String scenario, Map<String, String> extraInfo) {
        Product product = productMapper.findById(productId);
        if (product == null) {
            ValuationReport empty = new ValuationReport();
            empty.setEstimatedValue(BigDecimal.ZERO);
            empty.setMarketAnalysis("商品不存在");
            return empty;
        }
        String condition = extraInfo != null ? extraInfo.getOrDefault("condition",
                product.getProductCondition() != null ? product.getProductCondition() : "good") : "good";

        if (aiProvider.isAvailable()) {
            try {
                return valuateFromDescription(
                        product.getDescription() != null ? product.getDescription() : product.getName(),
                        product.getBrand() != null ? product.getBrand() : "",
                        product.getModel() != null ? product.getModel() : "",
                        condition
                );
            } catch (Exception e) {
                log.error("AI valuation failed, using statistical fallback", e);
            }
        }
        return statisticalFallback(product, condition);
    }

    @Override
    public ValuationReport valuateFromDescription(String productDesc, String brand, String model, String condition) {
        if (!aiProvider.isAvailable()) {
            return statisticalFallback(productDesc);
        }

        String systemPrompt = "你是一个专业的商品估值专家。请根据提供的商品信息，给出合理的市场估值。" +
                "请以JSON格式返回，包含以下字段：estimatedValue（估值）, marketRangeLow（市场区间低）, " +
                "marketRangeHigh（市场区间高）, condition（成色）, marketAnalysis（市场分析）, recommendation（建议）。" +
                "只返回JSON，不要包含其他文字。";

        String userPrompt = String.format(
                "商品描述：%s\n品牌：%s\n型号：%s\n成色：%s\n请给出合理的二手市场估值。",
                productDesc, brand, model, condition
        );

        try {
            String response = aiProvider.getProvider().chat(systemPrompt, userPrompt);
            return parseValuationResponse(response);
        } catch (Exception e) {
            log.error("AI valuation failed, using statistical fallback", e);
            return statisticalFallback(productDesc);
        }
    }

    private ValuationReport parseValuationResponse(String response) {
        try {
            // Try to extract JSON from the response
            String json = response;
            if (response.contains("{")) {
                json = response.substring(response.indexOf("{"), response.lastIndexOf("}") + 1);
            }
            // The response might be nested in an AI API wrapper, try to extract content
            if (response.contains("\"content\"")) {
                int start = response.indexOf("\"content\":\"") + 11;
                int end = response.indexOf("\"}", start);
                if (end > start) {
                    String innerJson = response.substring(start, end)
                            .replace("\\\"", "\"").replace("\\n", "");
                    Map<String, Object> innerMap = objectMapper.readValue(innerJson,
                            new TypeReference<Map<String, Object>>() {});
                    return mapToReport(innerMap);
                }
            }
            Map<String, Object> map = objectMapper.readValue(json,
                    new TypeReference<Map<String, Object>>() {});
            return mapToReport(map);
        } catch (Exception e) {
            log.warn("Failed to parse AI valuation response", e);
            return statisticalFallback("parse error");
        }
    }

    @SuppressWarnings("unchecked")
    private ValuationReport mapToReport(Map<String, Object> map) {
        ValuationReport report = new ValuationReport();
        report.setEstimatedValue(getBigDecimal(map, "estimatedValue"));
        report.setMarketRangeLow(getBigDecimal(map, "marketRangeLow"));
        report.setMarketRangeHigh(getBigDecimal(map, "marketRangeHigh"));
        report.setCondition((String) map.getOrDefault("condition", "good"));
        report.setMarketAnalysis((String) map.getOrDefault("marketAnalysis", ""));
        report.setRecommendation((String) map.getOrDefault("recommendation", ""));
        if (map.containsKey("factors") && map.get("factors") instanceof Map) {
            report.setFactors((Map<String, Object>) map.get("factors"));
        }
        return report;
    }

    private BigDecimal getBigDecimal(Map<String, Object> map, String key) {
        Object val = map.get(key);
        if (val instanceof BigDecimal) return (BigDecimal) val;
        if (val instanceof Number) return BigDecimal.valueOf(((Number) val).doubleValue());
        if (val instanceof String) {
            try { return new BigDecimal((String) val); } catch (Exception e) { return BigDecimal.ZERO; }
        }
        return BigDecimal.ZERO;
    }

    private ValuationReport statisticalFallback(Product product, String condition) {
        ValuationReport report = new ValuationReport();
        Integer categoryId = product.getCategoryId();

        // Condition-based depreciation multiplier
        double multiplier;
        String conditionLabel;
        switch (condition != null ? condition.toLowerCase() : "good") {
            case "like-new": case "like_new": case "全新":
                multiplier = 0.95;
                conditionLabel = "like-new";
                break;
            case "good": case "良好":
                multiplier = 0.85;
                conditionLabel = "good";
                break;
            case "fair": case "一般":
                multiplier = 0.70;
                conditionLabel = "fair";
                break;
            case "poor": case "较差":
                multiplier = 0.50;
                conditionLabel = "poor";
                break;
            default:
                multiplier = 0.85;
                conditionLabel = condition != null ? condition : "good";
        }

        BigDecimal avgPrice = null;
        BigDecimal minPrice = null;
        BigDecimal maxPrice = null;
        if (categoryId != null) {
            try {
                avgPrice = productMapper.getAveragePriceByCategory(categoryId);
                minPrice = productMapper.getMinPriceByCategory(categoryId);
                maxPrice = productMapper.getMaxPriceByCategory(categoryId);
            } catch (Exception e) {
                log.warn("Failed to query category pricing data", e);
            }
        }

        if (avgPrice != null && avgPrice.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal estimated = avgPrice.multiply(BigDecimal.valueOf(multiplier))
                    .setScale(2, RoundingMode.HALF_UP);
            report.setEstimatedValue(estimated);
            report.setMarketRangeLow(minPrice != null ?
                    minPrice.multiply(BigDecimal.valueOf(multiplier)).setScale(2, RoundingMode.HALF_UP) :
                    BigDecimal.ZERO);
            report.setMarketRangeHigh(maxPrice != null ?
                    maxPrice.multiply(BigDecimal.valueOf(multiplier)).setScale(2, RoundingMode.HALF_UP) :
                    BigDecimal.ZERO);

            String analysis = String.format(
                    "基于同品类商品统计：该类商品平均售价¥%s，市场区间¥%s~¥%s。当前成色：%s，估值系数%.0f%%。",
                    avgPrice,
                    minPrice != null ? minPrice : BigDecimal.ZERO,
                    maxPrice != null ? maxPrice : BigDecimal.ZERO,
                    conditionLabel, multiplier * 100
            );
            report.setMarketAnalysis(analysis);
            report.setRecommendation("建议参考同类商品近期成交价，合理定价");
        } else if (product.getPrice() != null && product.getPrice().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal estimated = product.getPrice().multiply(BigDecimal.valueOf(multiplier))
                    .setScale(2, RoundingMode.HALF_UP);
            report.setEstimatedValue(estimated);
            report.setMarketRangeLow(estimated.multiply(BigDecimal.valueOf(0.8))
                    .setScale(2, RoundingMode.HALF_UP));
            report.setMarketRangeHigh(product.getPrice().setScale(2, RoundingMode.HALF_UP));
            report.setMarketAnalysis(String.format(
                    "基于商品原价¥%s，%s成色估值系数%.0f%%。缺乏同品类统计数据，仅供参考。",
                    product.getPrice(), conditionLabel, multiplier * 100
            ));
            report.setRecommendation("请参考同品类商品平均价格");
        } else {
            report.setEstimatedValue(BigDecimal.ZERO);
            report.setMarketRangeLow(BigDecimal.ZERO);
            report.setMarketRangeHigh(BigDecimal.ZERO);
            report.setMarketAnalysis("AI估值服务暂不可用，且缺少同品类统计数据");
            report.setRecommendation("请参考同品类商品平均价格");
        }

        report.setCondition(conditionLabel);

        Map<String, Object> factors = new HashMap<>();
        factors.put("source", "statistical_fallback");
        factors.put("category_id", categoryId);
        factors.put("condition_multiplier", multiplier);
        factors.put("description", product.getDescription() != null ? product.getDescription() : product.getName());
        report.setFactors(factors);

        return report;
    }

    private ValuationReport statisticalFallback(String productDesc) {
        ValuationReport report = new ValuationReport();
        report.setEstimatedValue(new BigDecimal("0.00"));
        report.setMarketRangeLow(new BigDecimal("0.00"));
        report.setMarketRangeHigh(new BigDecimal("0.00"));
        report.setCondition("good");
        report.setMarketAnalysis("AI估值服务暂不可用，使用统计默认值");
        report.setRecommendation("请参考同品类商品平均价格");
        Map<String, Object> factors = new HashMap<>();
        factors.put("source", "statistical_fallback");
        factors.put("description", productDesc);
        report.setFactors(factors);
        return report;
    }
}
