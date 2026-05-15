package com.qzy.springbootlogin.controller;

import com.qzy.springbootlogin.pojo.Result;
import com.qzy.springbootlogin.service.RecommendationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/recommend")
public class RecommendationController {

    @Autowired
    private RecommendationService recommendationService;

    @GetMapping("/personalized")
    public Result getPersonalized(HttpSession session,
                                   @RequestParam(defaultValue = "10") int limit) {
        Long userId = (Long) session.getAttribute("userId");
        List<Map<String, Object>> recommendations =
                recommendationService.getPersonalizedRecommendations(userId, limit);
        return Result.success("获取成功", recommendations);
    }

    @GetMapping("/also-bought/{productId}")
    public Result getAlsoBought(@PathVariable Integer productId,
                                 @RequestParam(defaultValue = "6") int limit) {
        List<Map<String, Object>> result = recommendationService.getAlsoBought(productId, limit);
        return Result.success("获取成功", result);
    }

    @GetMapping("/similar/{productId}")
    public Result getSimilar(@PathVariable Integer productId,
                              @RequestParam(defaultValue = "6") int limit) {
        List<Map<String, Object>> result = recommendationService.getSimilarProducts(productId, limit);
        return Result.success("获取成功", result);
    }

    @GetMapping("/trending")
    public Result getTrending(@RequestParam(defaultValue = "10") int limit) {
        List<Map<String, Object>> result = recommendationService.getTrendingProducts(limit);
        return Result.success("获取成功", result);
    }

    @GetMapping("/new-products")
    public Result getNewProducts(@RequestParam(defaultValue = "6") int limit) {
        List<Map<String, Object>> result = recommendationService.getNewProducts(limit);
        return Result.success("获取成功", result);
    }

    @GetMapping("/flash-sale")
    public Result getFlashSale(@RequestParam(defaultValue = "4") int limit) {
        List<Map<String, Object>> result = recommendationService.getFlashSale(limit);
        return Result.success("获取成功", result);
    }

    @GetMapping("/hot-keywords")
    public Result getHotKeywords(@RequestParam(defaultValue = "10") int limit) {
        List<Map<String, Object>> result = recommendationService.getHotSearchKeywords(limit);
        return Result.success("获取成功", result);
    }

    @GetMapping("/recently-viewed")
    public Result getRecentlyViewed(HttpSession session,
                                     @RequestParam(defaultValue = "10") int limit) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return Result.error("请先登录");
        }
        List<Map<String, Object>> result = recommendationService.getRecentlyViewed(userId, limit);
        return Result.success("获取成功", result);
    }

    @PostMapping("/track")
    public Result trackBehavior(@RequestBody(required = false) Map<String, Object> body,
                                HttpSession session) {
        Long userId = body != null && body.containsKey("userId")
                ? ((Number) body.get("userId")).longValue() : null;
        Integer productId = body != null && body.containsKey("productId")
                ? ((Number) body.get("productId")).intValue() : null;
        String action = body != null ? (String) body.get("action") : null;
        String keyword = body != null ? (String) body.get("keyword") : null;
        Integer duration = body != null && body.containsKey("duration")
                ? ((Number) body.get("duration")).intValue() : null;

        Long uid = userId != null ? userId : (Long) session.getAttribute("userId");
        if (uid == null || action == null) {
            return Result.error("请提供用户ID/action或先登录");
        }
        recommendationService.recordBehavior(uid, productId, action, keyword, duration);
        return Result.success("记录成功");
    }
}
