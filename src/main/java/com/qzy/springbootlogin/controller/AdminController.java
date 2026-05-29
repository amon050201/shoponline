package com.qzy.springbootlogin.controller;

import com.qzy.springbootlogin.mapper.OperationLogMapper;
import com.qzy.springbootlogin.pojo.OperationLog;
import com.qzy.springbootlogin.pojo.Result;
import com.qzy.springbootlogin.pojo.User;
import com.qzy.springbootlogin.service.UserService;
import com.qzy.springbootlogin.util.AdminOperation;
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

    @Autowired
    private OperationLogMapper logMapper;

    /**
     * 管理员后台首页
     */
    @AdminOperation(value = "查看管理后台首页", module = "仪表盘")
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
    @AdminOperation(value = "查看用户管理页面", module = "用户管理")
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

        int customerCount = (int) users.stream().filter(u -> u.getRoleType() != null && u.getRoleType() == 0).count();
        int merchantCount = (int) users.stream().filter(u -> u.getRoleType() != null && u.getRoleType() == 1).count();
        int adminCount = (int) users.stream().filter(u -> u.getRoleType() != null && u.getRoleType() == 2).count();
        model.addAttribute("customerCount", customerCount);
        model.addAttribute("merchantCount", merchantCount);
        model.addAttribute("adminCount", adminCount);

        return "pages/admin/users";
    }

    /**
     * 更新用户角色
     */
    @AdminOperation(value = "修改用户角色", module = "用户管理")
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


    @AdminOperation(value = "查看操作日志", module = "系统日志")
    @GetMapping("/logs")
    public String operationLogs(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        Integer roleType = (Integer) session.getAttribute("roleType");
        if (userId == null || roleType != 2) {
            return "redirect:/login";
        }
        model.addAttribute("username", session.getAttribute("username"));
        model.addAttribute("roleType", roleType);
        return "pages/admin/logs";
    }

    @GetMapping("/api/logs")
    @ResponseBody
    public Result getLogs(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "15") int pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String module,
            @RequestParam(required = false) String dateStart,
            @RequestParam(required = false) String dateEnd) {
        int offset = (page - 1) * pageSize;
        List<OperationLog> logs;
        int total;

        if (keyword != null && !keyword.trim().isEmpty()) {
            logs = logMapper.search(keyword.trim(), offset, pageSize);
            total = logMapper.countSearch(keyword.trim());
        } else if (module != null && !module.trim().isEmpty()) {
            logs = logMapper.findByModulePage(module.trim(), offset, pageSize);
            total = logMapper.countByModule(module.trim());
        } else if (dateStart != null && !dateStart.isEmpty() && dateEnd != null && !dateEnd.isEmpty()) {
            logs = logMapper.findByDateRange(dateStart + " 00:00:00", dateEnd + " 23:59:59", offset, pageSize);
            total = logMapper.countByDateRange(dateStart + " 00:00:00", dateEnd + " 23:59:59");
        } else {
            logs = logMapper.findByPage(offset, pageSize);
            total = logMapper.count();
        }

        Map<String, Object> data = new HashMap<>();
        data.put("logs", logs);
        data.put("total", total);
        data.put("page", page);
        data.put("pageSize", pageSize);
        return Result.success("获取成功", data);
    }

    @AdminOperation(value = "清理旧日志", module = "系统日志")
    @DeleteMapping("/api/logs/clean")
    @ResponseBody
    public Result cleanLogs(@RequestParam(defaultValue = "90") int days, HttpSession session) {
        Integer roleType = (Integer) session.getAttribute("roleType");
        if (roleType == null || roleType != 2) return Result.error("需要管理员权限");
        int deleted = logMapper.deleteOlderThan(days);
        return Result.success("已清理 " + deleted + " 条" + days + "天前的日志");
    }
}
