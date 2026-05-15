package com.qzy.springbootlogin.module.admin.controller;

import com.qzy.springbootlogin.ai.pojo.FraudAlert;
import com.qzy.springbootlogin.ai.service.FraudDetectionService;
import com.qzy.springbootlogin.pojo.Result;
import com.qzy.springbootlogin.pojo.User;
import com.qzy.springbootlogin.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminDashboardController {

    @Autowired
    private UserService userService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired(required = false)
    private FraudDetectionService fraudDetectionService;

    @GetMapping("/dashboard")
    public Result dashboard(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        Integer roleType = (Integer) session.getAttribute("roleType");
        if (userId == null || roleType == null || roleType != 2) {
            return Result.error("需要管理员权限");
        }

        Map<String, Object> stats = new HashMap<>();
        try {
            List<User> users = userService.list();
            stats.put("totalUsers", users.size());
            stats.put("customerCount", users.stream().filter(u -> u.getRoleType() != null && u.getRoleType() == 0).count());
            stats.put("merchantCount", users.stream().filter(u -> u.getRoleType() != null && u.getRoleType() == 1).count());
            stats.put("adminCount", users.stream().filter(u -> u.getRoleType() != null && u.getRoleType() == 2).count());
            stats.put("activeUsers", users.stream().filter(u -> u.getStatus() != null && u.getStatus() == 1).count());

            Integer productCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM product WHERE status = 1", Integer.class);
            Integer orderCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM orders", Integer.class);
            Double totalRevenue = jdbcTemplate.queryForObject("SELECT COALESCE(SUM(actual_amount), 0) FROM orders WHERE status >= 1", Double.class);

            stats.put("productCount", productCount != null ? productCount : 0);
            stats.put("orderCount", orderCount != null ? orderCount : 0);
            stats.put("totalRevenue", totalRevenue != null ? totalRevenue : 0.0);

            if (fraudDetectionService != null) {
                List<FraudAlert> alerts = fraudDetectionService.getUnresolvedAlerts();
                stats.put("unresolvedFraudAlerts", alerts.size());
                stats.put("fraudAlerts", alerts);
            }
        } catch (Exception e) {
            return Result.error("获取统计数据失败：" + e.getMessage());
        }
        return Result.success("获取成功", stats);
    }
}
