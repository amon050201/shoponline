package com.qzy.springbootlogin.module.admin.controller;

import com.qzy.springbootlogin.pojo.Result;
import com.qzy.springbootlogin.pojo.User;
import com.qzy.springbootlogin.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public Result listUsers(HttpSession session) {
        Integer roleType = (Integer) session.getAttribute("roleType");
        if (roleType == null || roleType != 2) {
            return Result.error("需要管理员权限");
        }
        List<User> users = userService.list();
        return Result.success("获取成功", users);
    }

    @GetMapping("/{id}")
    public Result getUser(@PathVariable Long id) {
        User user = userService.getUserById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        return Result.success("获取成功", user);
    }

    @PostMapping("/role")
    public Result updateRole(@RequestParam Long userId,
                             @RequestParam Integer roleType,
                             HttpSession session) {
        Integer currentRole = (Integer) session.getAttribute("roleType");
        if (currentRole == null || currentRole != 2) {
            return Result.error("需要管理员权限");
        }
        try {
            boolean updated = userService.updateUserRole(userId, roleType);
            if (updated) {
                return Result.success("角色更新成功");
            }
            return Result.error("角色更新失败");
        } catch (Exception e) {
            return Result.error("更新失败：" + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public Result deleteUser(@PathVariable Long id, HttpSession session) {
        Integer currentRole = (Integer) session.getAttribute("roleType");
        if (currentRole == null || currentRole != 2) {
            return Result.error("需要管理员权限");
        }
        try {
            userService.deleteUser(id);
            return Result.success("删除成功");
        } catch (Exception e) {
            return Result.error("删除失败：" + e.getMessage());
        }
    }
}
