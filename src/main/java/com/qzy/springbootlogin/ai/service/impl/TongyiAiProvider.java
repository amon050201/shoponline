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
public class TongyiAiProvider implements AiProviderService {

    private static final Logger log = LoggerFactory.getLogger(TongyiAiProvider.class);
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    @Autowired
    private AiProviderConfig config;

    private final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build();

    @Override
    public String chat(String systemPrompt, String userPrompt) {
        String apiKey = config.getTongyi().getApiKey();
        if (apiKey == null || apiKey.isEmpty() || apiKey.contains("your-")) {
            log.warn("Tongyi API key not configured");
            return fallbackResponse("chat");
        }
        try {
            String json = String.format("""
                    {"model":"%s","input":{"messages":[{"role":"system","content":"%s"},{"role":"user","content":"%s"}]}}""",
                    config.getTongyi().getModel(),
                    escapeJson(systemPrompt),
                    escapeJson(userPrompt));

            Request request = new Request.Builder()
                    .url(config.getTongyi().getBaseUrl() + "/services/aigc/text-generation/generation")
                    .addHeader("Authorization", "Bearer " + apiKey)
                    .addHeader("Content-Type", "application/json")
                    .post(RequestBody.create(json, JSON))
                    .build();

            try (Response resp = client.newCall(request).execute()) {
                String body = resp.body() != null ? resp.body().string() : "{}";
                if (!resp.isSuccessful()) {
                    log.error("Tongyi API error: {} {}", resp.code(), body);
                    return fallbackResponse("chat");
                }
                return body;
            }
        } catch (Exception e) {
            log.error("Tongyi chat failed", e);
            return fallbackResponse("chat");
        }
    }

    @Override
    public String chatWithImage(String systemPrompt, String userPrompt, String imageBase64) {
        String apiKey = config.getTongyi().getApiKey();
        if (apiKey == null || apiKey.isEmpty() || apiKey.contains("your-")) {
            log.warn("Tongyi API key not configured");
            return fallbackResponse("vision");
        }
        try {
            String json = String.format("""
                    {"model":"%s","input":{"messages":[{"role":"user","content":[{"text":"%s"},{"image":"%s"}]}]}}""",
                    config.getTongyi().getVisionModel(),
                    escapeJson(systemPrompt + "\n" + userPrompt),
                    imageBase64);

            Request request = new Request.Builder()
                    .url(config.getTongyi().getBaseUrl() + "/services/aigc/multimodal-generation/generation")
                    .addHeader("Authorization", "Bearer " + apiKey)
                    .addHeader("Content-Type", "application/json")
                    .post(RequestBody.create(json, JSON))
                    .build();

            try (Response resp = client.newCall(request).execute()) {
                String body = resp.body() != null ? resp.body().string() : "{}";
                if (!resp.isSuccessful()) {
                    log.error("Tongyi vision API error: {} {}", resp.code(), body);
                    return fallbackResponse("vision");
                }
                return body;
            }
        } catch (Exception e) {
            log.error("Tongyi vision failed", e);
            return fallbackResponse("vision");
        }
    }

    @Override
    public boolean isAvailable() {
        String key = config.getTongyi().getApiKey();
        return key != null && !key.isEmpty() && !key.contains("your-");
    }

    @Override
    public String getProviderName() {
        return "tongyi";
    }

    private String escapeJson(String s) {
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private String fallbackResponse(String type) {
        if ("vision".equals(type)) {
            return "{\"output\":{\"choices\":[{\"message\":{\"content\":\"{\\\"productName\\\":\\\"未知商品\\\",\\\"brand\\\":\\\"unknown\\\",\\\"category\\\":\\\"unknown\\\",\\\"condition\\\":\\\"good\\\"}\"}}]}}";
        }
        return "{\"output\":{\"choices\":[{\"message\":{\"content\":\"{\\\"estimatedValue\\\":0,\\\"marketRangeLow\\\":0,\\\"marketRangeHigh\\\":0,\\\"condition\\\":\\\"good\\\",\\\"marketAnalysis\\\":\\\"AI服务未配置，使用默认估值\\\",\\\"recommendation\\\":\\\"请配置AI API Key\\\"}\"}}]}}";
    }
}
