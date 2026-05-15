package com.qzy.springbootlogin.ai.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qzy.springbootlogin.ai.pojo.ImageRecognitionResult;
import com.qzy.springbootlogin.ai.service.AiProviderSelector;
import com.qzy.springbootlogin.ai.service.ImageRecognitionService;
import com.qzy.springbootlogin.mapper.ProductMapper;
import com.qzy.springbootlogin.pojo.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ImageRecognitionServiceImpl implements ImageRecognitionService {

    private static final Logger log = LoggerFactory.getLogger(ImageRecognitionServiceImpl.class);

    @Autowired
    private AiProviderSelector aiProvider;

    @Autowired
    private ProductMapper productMapper;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public ImageRecognitionResult recognize(MultipartFile image) {
        ImageRecognitionResult result = new ImageRecognitionResult();
        result.setProductName("unknown");
        result.setBrand("unknown");
        result.setCategory("unknown");
        result.setCondition("good");

        if (!aiProvider.isAvailable()) {
            result.setProductName("AI服务未配置");
            return result;
        }

        try {
            String base64 = Base64.getEncoder().encodeToString(image.getBytes());

            String systemPrompt = "你是一个商品识别专家。请从图片中识别商品信息,以JSON格式返回以下字段: " +
                    "productName(商品名称), brand(品牌), category(分类), " +
                    "condition(成色: like-new/good/fair/poor), " +
                    "extractedAttributes(以数组形式列出识别到的其他属性). 只返回JSON。";

            String response = aiProvider.getProvider().chatWithImage(systemPrompt, "", base64);
            return parseRecognitionResponse(response);
        } catch (Exception e) {
            log.error("Image recognition failed", e);
            return result;
        }
    }

    @Override
    public List<Product> searchByImage(MultipartFile image) {
        ImageRecognitionResult recognition = recognize(image);
        if (recognition.getBrand() == null || recognition.getBrand().isEmpty()) {
            return Collections.emptyList();
        }
        // Use extracted info to search products
        String keyword = recognition.getBrand();
        if (recognition.getProductName() != null && !recognition.getProductName().isEmpty()) {
            keyword = recognition.getProductName();
        }
        List<Product> results = productMapper.smartSearch(keyword);
        // Filter by category if recognized
        if (recognition.getCategory() != null && !"unknown".equals(recognition.getCategory())) {
            results = results.stream()
                    .filter(p -> p.getCategoryName() != null &&
                            (p.getCategoryName().contains(recognition.getCategory()) ||
                             recognition.getCategory().contains(p.getCategoryName())))
                    .collect(Collectors.toList());
        }
        return results.size() > 20 ? results.subList(0, 20) : results;
    }

    @SuppressWarnings("unchecked")
    private ImageRecognitionResult parseRecognitionResponse(String response) {
        ImageRecognitionResult result = new ImageRecognitionResult();
        result.setProductName("unknown");
        result.setBrand("unknown");
        result.setCategory("unknown");
        result.setCondition("good");

        try {
            // Extract JSON from response (handle AI API wrappers)
            String json = response;
            if (response.contains("\"content\"")) {
                int start = response.indexOf("\"content\":\"") + 11;
                int end = response.indexOf("\"}", start);
                if (end > start) {
                    String inner = response.substring(start, end)
                            .replace("\\\"", "\"").replace("\\n", "");
                    json = inner;
                }
            }
            if (json.contains("{")) {
                json = json.substring(json.indexOf("{"), json.lastIndexOf("}") + 1);
            }

            Map<String, Object> map = objectMapper.readValue(json,
                    new TypeReference<Map<String, Object>>() {});
            result.setProductName((String) map.getOrDefault("productName", "unknown"));
            result.setBrand((String) map.getOrDefault("brand", "unknown"));
            result.setCategory((String) map.getOrDefault("category", "unknown"));
            result.setCondition((String) map.getOrDefault("condition", "good"));

            if (map.containsKey("extractedAttributes")) {
                Object attrs = map.get("extractedAttributes");
                if (attrs instanceof List) {
                    result.setExtractedAttributes((List<String>) attrs);
                }
            }
        } catch (Exception e) {
            log.warn("Failed to parse recognition response", e);
        }
        return result;
    }
}
