package com.qzy.springbootlogin.service.impl;

import com.qzy.springbootlogin.service.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AnalyticsServiceImpl implements AnalyticsService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public Map<String, Object> getSalesOverview() {
        Map<String, Object> overview = new HashMap<>();

        try {
            Double totalRevenue = jdbcTemplate.queryForObject(
                    "SELECT COALESCE(SUM(actual_amount), 0) FROM orders WHERE status >= 1", Double.class);
            Integer totalOrders = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM orders", Integer.class);
            Integer completedOrders = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM orders WHERE status = 3", Integer.class);
            Integer pendingOrders = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM orders WHERE status = 0", Integer.class);
            Double avgOrderValue = jdbcTemplate.queryForObject(
                    "SELECT COALESCE(AVG(actual_amount), 0) FROM orders WHERE status >= 1", Double.class);
            Integer totalUsers = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM users", Integer.class);

            overview.put("totalRevenue", totalRevenue != null ? totalRevenue : 0.0);
            overview.put("totalOrders", totalOrders != null ? totalOrders : 0);
            overview.put("completedOrders", completedOrders != null ? completedOrders : 0);
            overview.put("pendingOrders", pendingOrders != null ? pendingOrders : 0);
            overview.put("avgOrderValue", Math.round((avgOrderValue != null ? avgOrderValue : 0.0) * 100) / 100.0);
            overview.put("totalUsers", totalUsers != null ? totalUsers : 0);
        } catch (Exception e) {
            System.err.println("获取销售概览失败: " + e.getMessage());
        }

        return overview;
    }

    @Override
    public List<Map<String, Object>> getTopSellingProducts(int limit) {
        try {
            return jdbcTemplate.queryForList(
                    "SELECT p.id, p.name, p.price, p.image_url, p.sales_count, " +
                    "c.name as category_name " +
                    "FROM product p LEFT JOIN category c ON p.category_id = c.id " +
                    "WHERE p.status = 1 ORDER BY p.sales_count DESC LIMIT ?", limit);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    @Override
    public List<Map<String, Object>> getMostViewedProducts(int limit) {
        try {
            return jdbcTemplate.queryForList(
                    "SELECT p.id, p.name, p.price, p.image_url, p.view_count, " +
                    "c.name as category_name " +
                    "FROM product p LEFT JOIN category c ON p.category_id = c.id " +
                    "WHERE p.status = 1 ORDER BY p.view_count DESC LIMIT ?", limit);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    @Override
    public List<Map<String, Object>> getCategorySalesDistribution() {
        try {
            return jdbcTemplate.queryForList(
                    "SELECT c.id, c.name, COUNT(oi.id) as order_count, " +
                    "COALESCE(SUM(oi.total_price), 0) as revenue " +
                    "FROM category c " +
                    "LEFT JOIN product p ON c.id = p.category_id " +
                    "LEFT JOIN order_item oi ON p.id = oi.product_id " +
                    "LEFT JOIN orders o ON oi.order_id = o.id AND o.status >= 1 " +
                    "GROUP BY c.id, c.name ORDER BY revenue DESC");
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    @Override
    public List<Map<String, Object>> getDailySalesTrend(int days) {
        try {
            return jdbcTemplate.queryForList(
                    "SELECT DATE(created_time) as date, " +
                    "COUNT(*) as order_count, COALESCE(SUM(actual_amount), 0) as revenue " +
                    "FROM orders WHERE created_time >= DATE_SUB(CURDATE(), INTERVAL ? DAY) " +
                    "AND status >= 1 " +
                    "GROUP BY DATE(created_time) ORDER BY date ASC", days);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    @Override
    public Map<String, Object> getUserBehaviorStats() {
        Map<String, Object> stats = new HashMap<>();

        try {
            Integer totalViews = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM user_behavior WHERE action = 'view'", Integer.class);
            Integer totalSearches = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM user_behavior WHERE action = 'search'", Integer.class);
            Integer totalAddCart = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM user_behavior WHERE action = 'add_cart'", Integer.class);
            Integer totalPurchases = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM user_behavior WHERE action = 'purchase'", Integer.class);
            Integer activeUsers = jdbcTemplate.queryForObject(
                    "SELECT COUNT(DISTINCT user_id) FROM user_behavior", Integer.class);

            stats.put("totalViews", totalViews != null ? totalViews : 0);
            stats.put("totalSearches", totalSearches != null ? totalSearches : 0);
            stats.put("totalAddCart", totalAddCart != null ? totalAddCart : 0);
            stats.put("totalPurchases", totalPurchases != null ? totalPurchases : 0);
            stats.put("activeUsers", activeUsers != null ? activeUsers : 0);
        } catch (Exception e) {
            System.err.println("获取行为统计失败: " + e.getMessage());
        }

        return stats;
    }

    @Override
    public Map<String, Object> getMerchantSalesStats(Long merchantId) {
        Map<String, Object> stats = new HashMap<>();

        try {
            Double totalRevenue = jdbcTemplate.queryForObject(
                    "SELECT COALESCE(SUM(oi.total_price), 0) FROM order_item oi " +
                    "JOIN product p ON oi.product_id = p.id " +
                    "JOIN orders o ON oi.order_id = o.id " +
                    "WHERE p.merchant_id = ? AND o.status >= 1", Double.class, merchantId);
            Integer totalOrders = jdbcTemplate.queryForObject(
                    "SELECT COUNT(DISTINCT o.id) FROM orders o " +
                    "JOIN order_item oi ON o.id = oi.order_id " +
                    "JOIN product p ON oi.product_id = p.id " +
                    "WHERE p.merchant_id = ?", Integer.class, merchantId);
            Integer productCount = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM product WHERE merchant_id = ? AND status = 1", Integer.class, merchantId);

            stats.put("totalRevenue", totalRevenue != null ? totalRevenue : 0.0);
            stats.put("totalOrders", totalOrders != null ? totalOrders : 0);
            stats.put("productCount", productCount != null ? productCount : 0);
        } catch (Exception e) {
            System.err.println("获取商家统计失败: " + e.getMessage());
        }

        return stats;
    }

    @Override
    public List<Map<String, Object>> getMerchantTopProducts(Long merchantId, int limit) {
        try {
            return jdbcTemplate.queryForList(
                    "SELECT p.id, p.name, p.price, p.image_url, p.sales_count, p.view_count, " +
                    "c.name as category_name " +
                    "FROM product p LEFT JOIN category c ON p.category_id = c.id " +
                    "WHERE p.merchant_id = ? AND p.status = 1 " +
                    "ORDER BY p.sales_count DESC LIMIT ?", merchantId, limit);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}
