package com.qzy.springbootlogin.ai.service.impl;

import com.qzy.springbootlogin.ai.config.AiProviderConfig;
import com.qzy.springbootlogin.ai.service.AiProviderService;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class WenxinAiProvider implements AiProviderService {

    private static final Logger log = LoggerFactory.getLogger(WenxinAiProvider.class);
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    @Autowired
    private AiProviderConfig config;

    private final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build();

    private String accessToken = null;
    private long tokenExpiry = 0;

    @Override
    public String chat(String systemPrompt, String userPrompt) {
        try {
            ensureToken();
            String json = String.format("""
                    {"messages":[{"role":"user","content":"%s"}]}""",
                    escapeJson(systemPrompt + "\n" + userPrompt));

            Request request = new Request.Builder()
                    .url("https://aip.baidubce.com/rpc/2.0/ai_custom/v1/wenxinworkshop/chat/" + config.getWenxin().getModel()
                            + "?access_token=" + accessToken)
                    .addHeader("Content-Type", "application/json")
                    .post(RequestBody.create(json, JSON))
                    .build();

            try (Response resp = client.newCall(request).execute()) {
                String body = resp.body() != null ? resp.body().string() : "{}";
                return body;
            }
        } catch (Exception e) {
            log.error("Wenxin chat failed", e);
            return "{\"result\":\"{\\\"estimatedValue\\\":0}\"}";
        }
    }

    @Override
    public String chatWithImage(String systemPrompt, String userPrompt, String imageBase64) {
        // 文心一言暂不支持直接图片输入，回退到文本
        return chat(systemPrompt, userPrompt + "\n[图片数据已省略]");
    }

    @Override
    public boolean isAvailable() {
        String key = config.getWenxin().getApiKey();
        return key != null && !key.isEmpty() && !key.contains("your-");
    }

    @Override
    public String getProviderName() {
        return "wenxin";
    }

    private void ensureToken() throws Exception {
        if (accessToken != null && System.currentTimeMillis() < tokenExpiry) return;
        String url = "https://aip.baidubce.com/oauth/2.0/token?grant_type=client_credentials"
                + "&client_id=" + config.getWenxin().getApiKey()
                + "&client_secret=" + config.getWenxin().getSecretKey();
        Request request = new Request.Builder().url(url).get().build();
        try (Response resp = client.newCall(request).execute()) {
            String body = resp.body() != null ? resp.body().string() : "{}";
            // Parse JSON to get access_token
            String tokenKey = "\"access_token\":\"";
            int start = body.indexOf(tokenKey);
            if (start > 0) {
                start += tokenKey.length();
                int end = body.indexOf("\"", start);
                accessToken = body.substring(start, end);
                tokenExpiry = System.currentTimeMillis() + 86400000L; // 24h
            }
        }
    }

    private String escapeJson(String s) {
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
