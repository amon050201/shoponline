package com.qzy.springbootlogin.module.customer.controller;

import com.qzy.springbootlogin.pojo.Result;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/customer/search")
public class CustomerSearchHistoryController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/history")
    public Result getSearchHistory(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return Result.error("请先登录");
        String sql = "SELECT DISTINCT keyword FROM search_history WHERE user_id = ? " +
                "ORDER BY created_time DESC LIMIT 20";
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, userId);
        return Result.success("获取成功", rows);
    }

    @DeleteMapping("/history")
    public Result clearSearchHistory(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return Result.error("请先登录");
        jdbcTemplate.update("DELETE FROM search_history WHERE user_id = ?", userId);
        return Result.success("已清除搜索历史");
    }
}
