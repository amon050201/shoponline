package com.qzy.springbootlogin.ai.service;

/**
 * AI Provider 抽象接口
 * 支持通义千问、文心一言等大模型API
 */
public interface AiProviderService {

    /**
     * 纯文本对话
     */
    String chat(String systemPrompt, String userPrompt);

    /**
     * 带图片的对话（视觉模型）
     */
    String chatWithImage(String systemPrompt, String userPrompt, String imageBase64);

    /**
     * 检查服务是否可用
     */
    boolean isAvailable();

    /**
     * 获取提供商名称
     */
    String getProviderName();
}
