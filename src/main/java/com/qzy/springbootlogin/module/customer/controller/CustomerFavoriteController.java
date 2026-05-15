package com.qzy.springbootlogin.module.customer.controller;

import com.qzy.springbootlogin.pojo.Result;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/customer/favorites")
public class CustomerFavoriteController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping
    public Result listFavorites(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return Result.error("请先登录");
        String sql = "SELECT f.id, f.product_id as productId, p.name as productName, " +
                "p.price as productPrice, p.image_url as productImage, f.created_time as createdTime " +
                "FROM favorite f JOIN product p ON f.product_id = p.id " +
                "WHERE f.user_id = ? ORDER BY f.created_time DESC";
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, userId);
        return Result.success("获取成功", rows);
    }

    @PostMapping("/add")
    public Result addFavorite(@RequestBody Map<String, Object> body, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return Result.error("请先登录");
        Integer productId = body.get("productId") != null ? ((Number) body.get("productId")).intValue() : null;
        if (productId == null) return Result.error("商品ID不能为空");
        String checkSql = "SELECT COUNT(*) FROM favorite WHERE user_id = ? AND product_id = ?";
        Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, userId, productId);
        if (count != null && count > 0) return Result.error("已收藏过该商品");
        jdbcTemplate.update("INSERT INTO favorite (user_id, product_id) VALUES (?, ?)", userId, productId);
        return Result.success("收藏成功");
    }

    @DeleteMapping("/{id}")
    public Result removeFavorite(@PathVariable Long id, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return Result.error("请先登录");
        jdbcTemplate.update("DELETE FROM favorite WHERE id = ? AND user_id = ?", id, userId);
        return Result.success("已取消收藏");
    }

    @GetMapping("/check/{productId}")
    public Result checkFavorite(@PathVariable Integer productId, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return Result.error("请先登录");
        String sql = "SELECT COUNT(*) FROM favorite WHERE user_id = ? AND product_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, userId, productId);
        return Result.success("查询成功", Map.of("isFavorite", count != null && count > 0));
    }
}
