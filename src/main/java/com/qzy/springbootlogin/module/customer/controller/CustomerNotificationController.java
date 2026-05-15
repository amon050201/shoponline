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
@RequestMapping("/api/customer/notifications")
public class CustomerNotificationController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping
    public Result listNotifications(@RequestParam(defaultValue = "1") int page,
                                    @RequestParam(defaultValue = "20") int size,
                                    HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return Result.error("请先登录");
        int offset = (page - 1) * size;
        String sql = "SELECT id, title, content, type, reference_id as referenceId, " +
                "is_read as isRead, created_time as createdTime " +
                "FROM notification WHERE user_id = ? ORDER BY created_time DESC LIMIT ?, ?";
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, userId, offset, size);
        String countSql = "SELECT COUNT(*) FROM notification WHERE user_id = ? AND is_read = 0";
        Integer unreadCount = jdbcTemplate.queryForObject(countSql, Integer.class, userId);
        Map<String, Object> data = new HashMap<>();
        data.put("list", rows);
        data.put("unreadCount", unreadCount != null ? unreadCount : 0);
        return Result.success("获取成功", data);
    }

    @PostMapping("/read/{id}")
    public Result markRead(@PathVariable Long id, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return Result.error("请先登录");
        jdbcTemplate.update("UPDATE notification SET is_read = 1 WHERE id = ? AND user_id = ?", id, userId);
        return Result.success("已标记为已读");
    }

    @PostMapping("/read-all")
    public Result markAllRead(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return Result.error("请先登录");
        jdbcTemplate.update("UPDATE notification SET is_read = 1 WHERE user_id = ?", userId);
        return Result.success("已全部标记为已读");
    }
}
