package com.qzy.springbootlogin.ai.pojo;

import java.util.List;

public class ImageRecognitionResult {
    private String productName;
    private String brand;
    private String category;
    private String condition; // like-new, good, fair, poor
    private List<String> extractedAttributes;

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getCondition() { return condition; }
    public void setCondition(String condition) { this.condition = condition; }
    public List<String> getExtractedAttributes() { return extractedAttributes; }
    public void setExtractedAttributes(List<String> extractedAttributes) { this.extractedAttributes = extractedAttributes; }
}
