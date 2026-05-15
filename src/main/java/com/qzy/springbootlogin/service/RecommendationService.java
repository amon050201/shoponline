package com.qzy.springbootlogin.service;

import java.util.List;
import java.util.Map;

/**
 * 推荐系统服务接口
 */
public interface RecommendationService {

    /** 记录用户行为 */
    void recordBehavior(Long userId, Integer productId, String action, String keyword, Integer duration);

    /** 获取用户个性化推荐 */
    List<Map<String, Object>> getPersonalizedRecommendations(Long userId, int limit);

    /** "买了又买" - 基于协同过滤 */
    List<Map<String, Object>> getAlsoBought(Integer productId, int limit);

    /** "相似商品" - 基于内容推荐 */
    List<Map<String, Object>> getSimilarProducts(Integer productId, int limit);

    /** 热门商品 - 基于流行度 */
    List<Map<String, Object>> getTrendingProducts(int limit);

    /** 新品首发 - 按上架时间排序 */
    List<Map<String, Object>> getNewProducts(int limit);

    /** 限时抢购 - 随机选取商品 + 折扣率 */
    List<Map<String, Object>> getFlashSale(int limit);

    /** 热门搜索关键词 */
    List<Map<String, Object>> getHotSearchKeywords(int limit);

    /** 获取用户浏览过的商品 */
    List<Map<String, Object>> getRecentlyViewed(Long userId, int limit);
}
