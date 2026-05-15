package com.qzy.springbootlogin.controller;

import com.qzy.springbootlogin.util.CaptchaUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 验证码控制器
 */
@Controller
public class CaptchaController {
    
    private static final String CAPTCHA_SESSION_KEY = "captcha_code";
    
    /**
     * 获取验证码图片
     */
    @GetMapping("/captcha")
    public ResponseEntity<byte[]> getCaptcha(HttpSession session) {
        // 生成验证码
        CaptchaUtil.CaptchaResult captcha = CaptchaUtil.generateCaptcha();
        
        // 将验证码文本存储到session中（转换为小写以便不区分大小写验证）
        session.setAttribute(CAPTCHA_SESSION_KEY, captcha.getCode().toLowerCase());
        
        // 返回验证码图片
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .header("Cache-Control", "no-cache, no-store, must-revalidate")
                .header("Pragma", "no-cache")
                .header("Expires", "0")
                .body(captcha.getImageData());
    }
}
