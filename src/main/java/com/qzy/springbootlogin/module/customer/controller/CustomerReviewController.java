package com.qzy.springbootlogin.module.customer.controller;

import com.qzy.springbootlogin.pojo.Result;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/customer")
public class CustomerReviewController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/products/{productId}/reviews")
    public Result listReviews(@PathVariable Integer productId,
                              @RequestParam(defaultValue = "1") int page,
                              @RequestParam(defaultValue = "10") int size) {
        int offset = (page - 1) * size;
        String sql = "SELECT r.id, r.user_id as userId, u.username, r.product_id as productId, " +
                "r.rating, r.content, r.images, r.created_time as createdTime " +
                "FROM review r JOIN users u ON r.user_id = u.id " +
                "WHERE r.product_id = ? AND r.status = 1 ORDER BY r.created_time DESC LIMIT ?, ?";
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, productId, offset, size);
        return Result.success("获取成功", rows);
    }

    @PostMapping("/products/{productId}/review")
    public Result submitReview(@PathVariable Integer productId,
                               @RequestBody Map<String, Object> body,
                               HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return Result.error("请先登录");
        Integer rating = body.get("rating") != null ? ((Number) body.get("rating")).intValue() : null;
        String content = (String) body.get("content");
        Integer orderId = body.get("orderId") != null ? ((Number) body.get("orderId")).intValue() : null;
        if (rating == null || rating < 1 || rating > 5) return Result.error("评分必须在1-5之间");
        if (content == null || content.trim().isEmpty()) return Result.error("评价内容不能为空");
        jdbcTemplate.update("INSERT INTO review (user_id, product_id, order_id, rating, content, status) " +
                "VALUES (?, ?, ?, ?, ?, 1)", userId, productId, orderId, rating, content.trim());
        return Result.success("评价成功");
    }

    @DeleteMapping("/products/{productId}/review/{reviewId}")
    public Result deleteReview(@PathVariable Integer productId, @PathVariable Long reviewId, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return Result.error("请先登录");
        int deleted = jdbcTemplate.update("DELETE FROM review WHERE id = ? AND user_id = ? AND product_id = ?",
                reviewId, userId, productId);
        if (deleted > 0) return Result.success("删除成功");
        return Result.error("无权删除或评价不存在");
    }
}
