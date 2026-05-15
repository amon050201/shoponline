package com.qzy.springbootlogin.module.customer.controller;

import com.qzy.springbootlogin.pojo.Result;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/customer")
public class CustomerFeedbackController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostMapping("/feedback")
    public Result submitFeedback(@RequestBody Map<String, String> body, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return Result.error("请先登录");
        String content = body.get("content");
        String contact = body.get("contact");
        String images = body.get("images");
        if (content == null || content.trim().isEmpty()) return Result.error("反馈内容不能为空");
        jdbcTemplate.update("INSERT INTO feedback (user_id, content, contact, images, status) VALUES (?, ?, ?, ?, 0)",
                userId, content.trim(), contact, images);
        return Result.success("感谢您的反馈！");
    }
}
