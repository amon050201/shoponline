package com.qzy.springbootlogin.controller;

import com.qzy.springbootlogin.pojo.Result;
import com.qzy.springbootlogin.service.AnalyticsService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    @Autowired
    private AnalyticsService analyticsService;

    @GetMapping("/sales-overview")
    public Result getSalesOverview() {
        Map<String, Object> data = analyticsService.getSalesOverview();
        return Result.success("获取成功", data);
    }

    @GetMapping("/top-selling")
    public Result getTopSelling(@RequestParam(defaultValue = "10") int limit) {
        List<Map<String, Object>> data = analyticsService.getTopSellingProducts(limit);
        return Result.success("获取成功", data);
    }

    @GetMapping("/most-viewed")
    public Result getMostViewed(@RequestParam(defaultValue = "10") int limit) {
        List<Map<String, Object>> data = analyticsService.getMostViewedProducts(limit);
        return Result.success("获取成功", data);
    }

    @GetMapping("/category-distribution")
    public Result getCategoryDistribution() {
        List<Map<String, Object>> data = analyticsService.getCategorySalesDistribution();
        return Result.success("获取成功", data);
    }

    @GetMapping("/sales-trend")
    public Result getSalesTrend(@RequestParam(defaultValue = "30") int days) {
        List<Map<String, Object>> data = analyticsService.getDailySalesTrend(days);
        return Result.success("获取成功", data);
    }

    @GetMapping("/behavior-stats")
    public Result getBehaviorStats() {
        Map<String, Object> data = analyticsService.getUserBehaviorStats();
        return Result.success("获取成功", data);
    }

    @GetMapping("/merchant-stats")
    public Result getMerchantStats(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return Result.error("请先登录");
        }
        Map<String, Object> data = analyticsService.getMerchantSalesStats(userId);
        return Result.success("获取成功", data);
    }

    @GetMapping("/merchant-top-products")
    public Result getMerchantTopProducts(HttpSession session,
                                         @RequestParam(defaultValue = "10") int limit) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return Result.error("请先登录");
        }
        List<Map<String, Object>> data = analyticsService.getMerchantTopProducts(userId, limit);
        return Result.success("获取成功", data);
    }
}
