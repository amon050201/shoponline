package com.qzy.springbootlogin.ai.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "ai")
public class AiProviderConfig {

    private String activeProvider = "tongyi";
    private Tongyi tongyi = new Tongyi();
    private Wenxin wenxin = new Wenxin();

    public String getActiveProvider() { return activeProvider; }
    public void setActiveProvider(String activeProvider) { this.activeProvider = activeProvider; }
    public Tongyi getTongyi() { return tongyi; }
    public void setTongyi(Tongyi tongyi) { this.tongyi = tongyi; }
    public Wenxin getWenxin() { return wenxin; }
    public void setWenxin(Wenxin wenxin) { this.wenxin = wenxin; }

    public static class Tongyi {
        private String apiKey = "";
        private String secretKey = "";
        private String model = "qwen-plus";
        private String visionModel = "qwen-vl-plus";
        private String baseUrl = "https://dashscope.aliyuncs.com/api/v1";

        public String getApiKey() { return apiKey; }
        public void setApiKey(String apiKey) { this.apiKey = apiKey; }
        public String getSecretKey() { return secretKey; }
        public void setSecretKey(String secretKey) { this.secretKey = secretKey; }
        public String getModel() { return model; }
        public void setModel(String model) { this.model = model; }
        public String getVisionModel() { return visionModel; }
        public void setVisionModel(String visionModel) { this.visionModel = visionModel; }
        public String getBaseUrl() { return baseUrl; }
        public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }
    }

    public static class Wenxin {
        private String apiKey = "";
        private String secretKey = "";
        private String model = "ernie-4.0-8k";
        private String baseUrl = "https://aip.baidubce.com";

        public String getApiKey() { return apiKey; }
        public void setApiKey(String apiKey) { this.apiKey = apiKey; }
        public String getSecretKey() { return secretKey; }
        public void setSecretKey(String secretKey) { this.secretKey = secretKey; }
        public String getModel() { return model; }
        public void setModel(String model) { this.model = model; }
        public String getBaseUrl() { return baseUrl; }
        public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }
    }

}
