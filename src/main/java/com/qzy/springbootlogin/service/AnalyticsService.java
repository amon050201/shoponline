package com.qzy.springbootlogin.service;

import java.util.List;
import java.util.Map;

public interface AnalyticsService {

    /** 销售概览 */
    Map<String, Object> getSalesOverview();

    /** 销量排行商品 */
    List<Map<String, Object>> getTopSellingProducts(int limit);

    /** 浏览排行商品 */
    List<Map<String, Object>> getMostViewedProducts(int limit);

    /** 分类销售分布 */
    List<Map<String, Object>> getCategorySalesDistribution();

    /** 近期销售趋势（按天） */
    List<Map<String, Object>> getDailySalesTrend(int days);

    /** 用户行为统计 */
    Map<String, Object> getUserBehaviorStats();

    /** 商家销售统计 */
    Map<String, Object> getMerchantSalesStats(Long merchantId);

    /** 商家Top商品 */
    List<Map<String, Object>> getMerchantTopProducts(Long merchantId, int limit);
}
