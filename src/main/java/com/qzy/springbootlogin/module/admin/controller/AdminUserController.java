package com.qzy.springbootlogin.module.admin.controller;

import com.qzy.springbootlogin.mapper.UserMapper;
import com.qzy.springbootlogin.util.AdminOperation;
import com.qzy.springbootlogin.pojo.Result;
import com.qzy.springbootlogin.pojo.User;
import com.qzy.springbootlogin.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    private Result checkAdmin(HttpSession session) {
        Integer roleType = (Integer) session.getAttribute("roleType");
        if (roleType == null || roleType != 2) {
            return Result.error("需要管理员权限");
        }
        return null;
    }

    @AdminOperation(value = "查看用户列表", module = "用户管理")
    @GetMapping
    public Result listUsers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer roleType,
            HttpSession session) {
        Result auth = checkAdmin(session);
        if (auth != null) return auth;

        int offset = (page - 1) * pageSize;
        List<User> users;
        int total;

        if (keyword != null && !keyword.trim().isEmpty()) {
            users = userMapper.search(keyword.trim(), offset, pageSize);
            total = userMapper.countSearch(keyword.trim());
        } else if (roleType != null && roleType >= 0) {
            users = userMapper.findByRole(roleType, offset, pageSize);
            total = userMapper.countByRole(roleType);
        } else {
            users = userMapper.findByPage(offset, pageSize);
            total = userMapper.count();
        }

        users.forEach(u -> u.setPasswordHash(null));

        Map<String, Object> data = new HashMap<>();
        data.put("users", users);
        data.put("total", total);
        data.put("page", page);
        data.put("pageSize", pageSize);
        return Result.success("获取成功", data);
    }

    @AdminOperation(value = "查看用户详情", module = "用户管理")
    @GetMapping("/{id}")
    public Result getUser(@PathVariable Long id, HttpSession session) {
        Result auth = checkAdmin(session);
        if (auth != null) return auth;

        User user = userService.getUserById(id);
        if (user == null) return Result.error("用户不存在");
        user.setPasswordHash(null);
        return Result.success("获取成功", user);
    }

    @AdminOperation(value = "修改用户角色", module = "用户管理")
    @PutMapping("/{id}/role")
    public Result updateRole(@PathVariable Long id, @RequestBody Map<String, Integer> body, HttpSession session) {
        Result auth = checkAdmin(session);
        if (auth != null) return auth;

        Integer roleType = body.get("roleType");
        if (roleType == null || roleType < 0 || roleType > 2) return Result.error("无效的角色类型");
        User user = userService.getUserById(id);
        if (user == null) return Result.error("用户不存在");
        if ("admin".equals(user.getUsername())) return Result.error("不能修改默认管理员角色");
        userService.updateUserRole(id, roleType);
        return Result.success("角色更新成功");
    }

    @AdminOperation(value = "切换用户状态", module = "用户管理")
    @PutMapping("/{id}/status")
    public Result toggleStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body, HttpSession session) {
        Result auth = checkAdmin(session);
        if (auth != null) return auth;

        Integer status = body.get("status");
        if (status == null || (status != 0 && status != 1)) return Result.error("无效的状态值");
        User user = userService.getUserById(id);
        if (user == null) return Result.error("用户不存在");
        if ("admin".equals(user.getUsername())) return Result.error("不能禁用默认管理员账号");
        userMapper.updateUserStatus(id, status);
        return Result.success(status == 1 ? "用户已启用" : "用户已禁用");
    }

    @AdminOperation(value = "更新用户信息", module = "用户管理")
    @PutMapping("/{id}")
    public Result updateUser(@PathVariable Long id, @RequestBody User user, HttpSession session) {
        Result auth = checkAdmin(session);
        if (auth != null) return auth;

        User existing = userService.getUserById(id);
        if (existing == null) return Result.error("用户不存在");
        user.setId(id);
        if (user.getPasswordHash() == null || user.getPasswordHash().isEmpty()) {
            user.setPasswordHash(null);
        }
        return userService.updateUser(user);
    }

    @AdminOperation(value = "删除用户", module = "用户管理")
    @DeleteMapping("/{id}")
    public Result deleteUser(@PathVariable Long id, HttpSession session) {
        Result auth = checkAdmin(session);
        if (auth != null) return auth;

        return userService.deleteUser(id);
    }

    @AdminOperation(value = "添加用户", module = "用户管理")
    @PostMapping
    public Result addUser(@RequestBody User user, HttpSession session) {
        Result auth = checkAdmin(session);
        if (auth != null) return auth;

        return userService.addUser(user);
    }

    @AdminOperation(value = "批量删除用户", module = "用户管理")
    @PostMapping("/batch-delete")
    public Result batchDelete(@RequestBody Map<String, List<Long>> body, HttpSession session) {
        Result auth = checkAdmin(session);
        if (auth != null) return auth;

        List<Long> ids = body.get("ids");
        if (ids == null || ids.isEmpty()) return Result.error("请选择要删除的用户");
        int count = 0;
        for (Long id : ids) {
            try {
                User user = userService.getUserById(id);
                if (user != null && !"admin".equals(user.getUsername())) {
                    userMapper.deleteById(id);
                    count++;
                }
            } catch (Exception ignored) {}
        }
        return Result.success("成功删除 " + count + " 个用户");
    }
}
