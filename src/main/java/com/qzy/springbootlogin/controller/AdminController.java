package com.qzy.springbootlogin.controller;

import com.qzy.springbootlogin.pojo.Result;
import com.qzy.springbootlogin.pojo.User;
import com.qzy.springbootlogin.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 管理员控制器
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 管理员后台首页
     */
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        Integer roleType = (Integer) session.getAttribute("roleType");

        if (userId == null || roleType != 2) {
            return "redirect:/login";
        }

        // 获取所有用户
        List<User> users = userService.list();
        model.addAttribute("users", users);
        model.addAttribute("username", session.getAttribute("username"));
        model.addAttribute("roleType", roleType);

        // 统计数据
        try {
            int totalUsers = users.size();
            int customerCount = (int) users.stream().filter(u -> u.getRoleType() != null && u.getRoleType() == 0).count();
            int merchantCount = (int) users.stream().filter(u -> u.getRoleType() != null && u.getRoleType() == 1).count();
            int adminCount = (int) users.stream().filter(u -> u.getRoleType() != null && u.getRoleType() == 2).count();
            int activeUsers = (int) users.stream().filter(u -> u.getStatus() != null && u.getStatus() == 1).count();

            Integer productCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM product WHERE status = 1", Integer.class);
            Integer orderCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM orders", Integer.class);
            Integer pendingOrderCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM orders WHERE status = 0", Integer.class);
            Integer paidOrderCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM orders WHERE status = 1", Integer.class);
            Double totalRevenue = jdbcTemplate.queryForObject("SELECT COALESCE(SUM(actual_amount), 0) FROM orders WHERE status >= 1", Double.class);

            model.addAttribute("totalUsers", totalUsers);
            model.addAttribute("customerCount", customerCount);
            model.addAttribute("merchantCount", merchantCount);
            model.addAttribute("adminCount", adminCount);
            model.addAttribute("activeUsers", activeUsers);
            model.addAttribute("productCount", productCount != null ? productCount : 0);
            model.addAttribute("orderCount", orderCount != null ? orderCount : 0);
            model.addAttribute("pendingOrderCount", pendingOrderCount != null ? pendingOrderCount : 0);
            model.addAttribute("paidOrderCount", paidOrderCount != null ? paidOrderCount : 0);
            model.addAttribute("totalRevenue", totalRevenue != null ? totalRevenue : 0.0);
        } catch (Exception e) {
            System.err.println("获取统计数据失败: " + e.getMessage());
        }

        return "pages/admin/dashboard";
    }

    /**
     * 用户管理页面
     */
    @GetMapping("/users")
    public String userManagement(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        Integer roleType = (Integer) session.getAttribute("roleType");

        if (userId == null || roleType != 2) {
            return "redirect:/login";
        }

        List<User> users = userService.list();
        model.addAttribute("users", users);
        model.addAttribute("username", session.getAttribute("username"));
        model.addAttribute("roleType", roleType);

        return "pages/admin/users";
    }

    /**
     * 更新用户角色
     */
    @PostMapping("/user/role")
    @ResponseBody
    public Result updateUserRole(@RequestParam Long userId, @RequestParam Integer roleType, HttpSession session) {
        Long currentUserId = (Long) session.getAttribute("userId");
        Integer currentRoleType = (Integer) session.getAttribute("roleType");

        if (currentUserId == null || currentRoleType != 2) {
            return Result.error("需要管理员权限");
        }

        try {
            User user = userService.getUserById(userId);
            if (user == null) {
                return Result.error("用户不存在");
            }

            user.setRoleType(roleType);
            boolean updated = userService.updateUserRole(userId, roleType);

            if (updated) {
                return Result.success("角色更新成功");
            } else {
                return Result.error("角色更新失败");
            }
        } catch (Exception e) {
            return Result.error("更新失败：" + e.getMessage());
        }
    }
}
