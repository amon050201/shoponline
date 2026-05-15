package com.qzy.springbootlogin.module.customer.controller;

import com.qzy.springbootlogin.pojo.Result;
import com.qzy.springbootlogin.pojo.User;
import com.qzy.springbootlogin.service.UserService;
import com.qzy.springbootlogin.util.PasswordUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class CustomerUserController {

    @Autowired
    private UserService userService;

    @GetMapping("/profile")
    public Result getProfile(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return Result.error("请先登录");
        User user = userService.getUserById(userId);
        if (user == null) return Result.error("用户不存在");
        Map<String, Object> profile = new HashMap<>();
        profile.put("id", user.getId());
        profile.put("username", user.getUsername());
        profile.put("email", user.getEmail());
        profile.put("phone", user.getPhone());
        profile.put("roleType", user.getRoleType());
        profile.put("status", user.getStatus());
        return Result.success("获取成功", profile);
    }

    @PostMapping("/profile/update")
    public Result updateProfile(@RequestBody Map<String, String> body, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return Result.error("请先登录");
        User user = userService.getUserById(userId);
        if (user == null) return Result.error("用户不存在");
        String email = body.get("email");
        String phone = body.get("phone");
        if (email != null) user.setEmail(email);
        if (phone != null) user.setPhone(phone);
        Result<Void> result = userService.updateUser(user);
        if (result.isSuccess()) return Result.success("更新成功");
        return Result.error(result.getMessage());
    }

    @PostMapping("/password/change")
    public Result changePassword(@RequestBody Map<String, String> body, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return Result.error("请先登录");
        String oldPassword = body.get("oldPassword");
        String newPassword = body.get("newPassword");
        if (oldPassword == null || newPassword == null) return Result.error("旧密码和新密码不能为空");
        if (newPassword.length() < 6) return Result.error("新密码长度不能少于6位");
        User user = userService.getUserById(userId);
        if (user == null) return Result.error("用户不存在");
        if (!PasswordUtil.matches(oldPassword, user.getPasswordHash())) return Result.error("旧密码不正确");
        user.setPasswordHash(newPassword);
        Result<Void> result = userService.updateUser(user);
        if (result.isSuccess()) return Result.success("密码修改成功");
        return Result.error(result.getMessage());
    }

    @PostMapping("/logout")
    public Result logout(HttpSession session) {
        session.invalidate();
        return Result.success("已退出登录");
    }
}
