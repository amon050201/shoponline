package com.qzy.springbootlogin.service.impl;

import com.qzy.springbootlogin.mapper.BehaviorMapper;
import com.qzy.springbootlogin.mapper.ProductMapper;
import com.qzy.springbootlogin.pojo.Product;
import com.qzy.springbootlogin.pojo.UserBehavior;
import com.qzy.springbootlogin.service.RecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecommendationServiceImpl implements RecommendationService {

    @Autowired
    private BehaviorMapper behaviorMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void recordBehavior(Long userId, Integer productId, String action, String keyword, Integer duration) {
        UserBehavior behavior = new UserBehavior();
        behavior.setUserId(userId);
        behavior.setProductId(productId);
        behavior.setAction(action);
        behavior.setKeyword(keyword);
        behavior.setDuration(duration);
        behaviorMapper.insert(behavior);
    }

    @Override
    public List<Map<String, Object>> getPersonalizedRecommendations(Long userId, int limit) {
        if (userId == null) {
            return getTrendingProducts(limit);
        }

        List<Map<String, Object>> categoryBased = behaviorMapper.recommendByCategoryPref(userId, limit);
        List<Map<String, Object>> normalized = normalizeMaps(categoryBased);
        if (normalized.size() >= limit) {
            return normalized.subList(0, limit);
        }

        Set<Integer> existingIds = normalized.stream()
                .map(m -> ((Number) m.get("id")).intValue())
                .collect(Collectors.toSet());

        List<Map<String, Object>> popular = getTrendingProducts(limit * 2);
        List<Map<String, Object>> result = new ArrayList<>(normalized);
        for (Map<String, Object> item : popular) {
            if (result.size() >= limit) break;
            Integer id = ((Number) item.get("id")).intValue();
            if (!existingIds.contains(id)) {
                result.add(item);
                existingIds.add(id);
            }
        }

        return result;
    }

    @Override
    public List<Map<String, Object>> getAlsoBought(Integer productId, int limit) {
        return normalizeMaps(behaviorMapper.findAlsoBought(productId, limit));
    }

    @Override
    public List<Map<String, Object>> getSimilarProducts(Integer productId, int limit) {
        Product product = productMapper.findById(productId);
        if (product == null) return Collections.emptyList();

        List<Product> allProducts = productMapper.findAll();
        List<Product> sameCategory = allProducts.stream()
                .filter(p -> p.getCategoryId() != null && p.getCategoryId().equals(product.getCategoryId())
                        && !p.getId().equals(productId))
                .collect(Collectors.toList());

        List<ScoredProduct> scored = new ArrayList<>();
        for (Product p : sameCategory) {
            double score = 0;
            score += 50;
            if (product.getBrand() != null && product.getBrand().equals(p.getBrand())) {
                score += 30;
            }
            if (product.getPrice() != null && p.getPrice() != null
                    && product.getPrice().compareTo(java.math.BigDecimal.ZERO) > 0) {
                double ratio = Math.min(product.getPrice().doubleValue(), p.getPrice().doubleValue())
                        / Math.max(product.getPrice().doubleValue(), p.getPrice().doubleValue());
                score += ratio * 20;
            }
            score += Math.min(p.getSalesCount() != null ? p.getSalesCount() : 0, 100) * 0.1;
            scored.add(new ScoredProduct(p, score));
        }

        scored.sort((a, b) -> Double.compare(b.score, a.score));

        return scored.stream()
                .limit(limit)
                .map(sp -> productToMap(sp.product, sp.score))
                .collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> getTrendingProducts(int limit) {
        return normalizeMaps(behaviorMapper.findTopViewed(limit));
    }

    @Override
    public List<Map<String, Object>> getNewProducts(int limit) {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                "SELECT id, name, price, original_price as originalPrice, " +
                "image_url as imageUrl, sales_count as salesCount, stock, brand, " +
                "created_time as createdTime FROM product WHERE status = 1 " +
                "ORDER BY created_time DESC LIMIT ?", limit);
        return normalizeMaps(rows);
    }

    @Override
    public List<Map<String, Object>> getFlashSale(int limit) {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                "SELECT id, name, price, original_price as originalPrice, " +
                "image_url as imageUrl, sales_count as salesCount, stock, brand " +
                "FROM product WHERE status = 1 AND stock > 0 " +
                "ORDER BY RAND() LIMIT ?", limit);
        for (Map<String, Object> row : rows) {
            if (row.get("originalPrice") != null && row.get("price") != null) {
                double op = ((Number) row.get("originalPrice")).doubleValue();
                double p = ((Number) row.get("price")).doubleValue();
                if (op > 0) {
                    row.put("discount", Math.round((1 - p / op) * 100));
                }
            }
            row.put("flashPrice", row.get("price"));
        }
        return normalizeMaps(rows);
    }

    @Override
    @Cacheable(value = "hotKeywords", unless = "#result == null || #result.isEmpty()")
    public List<Map<String, Object>> getHotSearchKeywords(int limit) {
        return behaviorMapper.findHotSearchKeywords(limit);
    }

    @Override
    public List<Map<String, Object>> getRecentlyViewed(Long userId, int limit) {
        if (userId == null) return Collections.emptyList();
        return normalizeMaps(behaviorMapper.findTopViewed(limit));
    }

    /** 将 Product 对象转为统一格式的 Map */
    private Map<String, Object> productToMap(Product p, double score) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", p.getId());
        map.put("name", p.getName());
        map.put("price", p.getPrice());
        map.put("originalPrice", p.getOriginalPrice());
        map.put("image", p.getImageUrl());
        map.put("imageUrl", p.getImageUrl());
        map.put("brand", p.getBrand());
        map.put("categoryName", p.getCategoryName());
        map.put("salesCount", p.getSalesCount());
        map.put("stock", p.getStock());
        if (p.getPrice() != null && p.getOriginalPrice() != null
                && p.getOriginalPrice().compareTo(java.math.BigDecimal.ZERO) > 0) {
            map.put("discount", Math.round((1 - p.getPrice().doubleValue() / p.getOriginalPrice().doubleValue()) * 100));
        }
        if (score > 0) map.put("score", Math.round(score * 100) / 100.0);
        return map;
    }

    /** 统一字段名：确保每个 map 同时包含 image 和 imageUrl */
    private List<Map<String, Object>> normalizeMaps(List<Map<String, Object>> list) {
        if (list == null) return Collections.emptyList();
        for (Map<String, Object> map : list) {
            if (map.containsKey("imageUrl") && !map.containsKey("image")) {
                map.put("image", map.get("imageUrl"));
            } else if (map.containsKey("image") && !map.containsKey("imageUrl")) {
                map.put("imageUrl", map.get("image"));
            }
            if (map.containsKey("salesCount") && !map.containsKey("sales")) {
                map.put("sales", map.get("salesCount"));
            } else if (map.containsKey("sales") && !map.containsKey("salesCount")) {
                map.put("salesCount", map.get("sales"));
            }
            if (map.containsKey("originalPrice") && map.containsKey("price")
                    && !map.containsKey("discount")) {
                Object op = map.get("originalPrice");
                Object p = map.get("price");
                if (op instanceof Number && p instanceof Number
                        && ((Number) op).doubleValue() > 0) {
                    map.put("discount", Math.round((1 - ((Number) p).doubleValue() / ((Number) op).doubleValue()) * 100));
                }
            }
        }
        return list;
    }

    private static class ScoredProduct {
        final Product product;
        final double score;
        ScoredProduct(Product product, double score) {
            this.product = product;
            this.score = score;
        }
    }
}
