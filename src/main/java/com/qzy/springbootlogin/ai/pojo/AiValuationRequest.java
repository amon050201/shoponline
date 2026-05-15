package com.qzy.springbootlogin.ai.pojo;

import java.util.Map;

public class AiValuationRequest {
    private Integer productId;
    private String scenario; // exchange, sale, listing
    private Map<String, String> extraInfo;

    public Integer getProductId() { return productId; }
    public void setProductId(Integer productId) { this.productId = productId; }
    public String getScenario() { return scenario; }
    public void setScenario(String scenario) { this.scenario = scenario; }
    public Map<String, String> getExtraInfo() { return extraInfo; }
    public void setExtraInfo(Map<String, String> extraInfo) { this.extraInfo = extraInfo; }
}
