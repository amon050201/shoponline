package com.qzy.springbootlogin.mapper;

import com.qzy.springbootlogin.pojo.UserBehavior;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 用户行为记录Mapper - 用于推荐系统
 */
@Mapper
public interface BehaviorMapper {

    @Insert("INSERT INTO user_behavior (user_id, product_id, action, keyword, duration) " +
            "VALUES (#{userId}, #{productId}, #{action}, #{keyword}, #{duration})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(UserBehavior behavior);

    @Select("SELECT p.id, p.name, COUNT(*) as freq FROM user_behavior b " +
            "JOIN product p ON b.product_id = p.id " +
            "WHERE b.action = 'purchase' AND p.status = 1 " +
            "GROUP BY p.id, p.name ORDER BY freq DESC LIMIT #{limit}")
    List<java.util.Map<String, Object>> findTopPurchased(@Param("limit") int limit);

    @Select("SELECT p.id, p.name, p.price, p.original_price as originalPrice, " +
            "p.image_url as imageUrl, p.sales_count as salesCount, p.view_count as viewCount, " +
            "p.brand, p.stock, COUNT(*) as freq FROM user_behavior b " +
            "JOIN product p ON b.product_id = p.id " +
            "WHERE b.action = 'view' AND p.status = 1 " +
            "GROUP BY p.id, p.name, p.price, p.original_price, p.image_url, " +
            "p.sales_count, p.view_count, p.brand, p.stock " +
            "ORDER BY freq DESC LIMIT #{limit}")
    List<java.util.Map<String, Object>> findTopViewed(@Param("limit") int limit);

    /** 查找购买了指定商品的用户也买了什么 */
    @Select("SELECT b2.product_id as id, p.name, p.price, p.original_price as originalPrice, " +
            "p.image_url as imageUrl, p.sales_count as salesCount, COUNT(*) as score " +
            "FROM user_behavior b1 " +
            "JOIN user_behavior b2 ON b1.user_id = b2.user_id AND b2.action = 'purchase' " +
            "JOIN product p ON b2.product_id = p.id " +
            "WHERE b1.product_id = #{productId} AND b1.action = 'purchase' " +
            "AND b2.product_id != #{productId} AND p.status = 1 " +
            "GROUP BY b2.product_id, p.name, p.price, p.original_price, p.image_url, p.sales_count " +
            "ORDER BY score DESC LIMIT #{limit}")
    List<java.util.Map<String, Object>> findAlsoBought(@Param("productId") Integer productId, @Param("limit") int limit);

    /** 获取用户的购买历史分类偏好 */
    @Select("SELECT c.id, c.name, COUNT(*) as score FROM user_behavior b " +
            "JOIN product p ON b.product_id = p.id " +
            "JOIN category c ON p.category_id = c.id " +
            "WHERE b.user_id = #{userId} AND (b.action = 'purchase' OR b.action = 'view') " +
            "GROUP BY c.id, c.name ORDER BY score DESC LIMIT 5")
    List<java.util.Map<String, Object>> findUserCategoryPrefs(@Param("userId") Long userId);

    /** 根据分类偏好推荐商品 */
    @Select("SELECT DISTINCT p.*, c.name as category_name FROM product p " +
            "JOIN category c ON p.category_id = c.id " +
            "WHERE p.category_id IN (SELECT category_id FROM (" +
            "SELECT p2.category_id, COUNT(*) as cnt FROM user_behavior b " +
            "JOIN product p2 ON b.product_id = p2.id " +
            "WHERE b.user_id = #{userId} AND (b.action = 'purchase' OR b.action = 'view') " +
            "GROUP BY p2.category_id ORDER BY cnt DESC LIMIT 3) prefs) " +
            "AND p.status = 1 AND p.id NOT IN (" +
            "SELECT DISTINCT product_id FROM user_behavior WHERE user_id = #{userId} AND action = 'purchase') " +
            "ORDER BY p.sales_count DESC LIMIT #{limit}")
    List<java.util.Map<String, Object>> recommendByCategoryPref(@Param("userId") Long userId, @Param("limit") int limit);

    /** 搜索关键词热榜 */
    @Select("SELECT keyword, COUNT(*) as freq FROM user_behavior " +
            "WHERE action = 'search' AND keyword IS NOT NULL " +
            "GROUP BY keyword ORDER BY freq DESC LIMIT #{limit}")
    List<java.util.Map<String, Object>> findHotSearchKeywords(@Param("limit") int limit);
}
