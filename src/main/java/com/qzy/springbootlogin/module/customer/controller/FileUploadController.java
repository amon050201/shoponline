package com.qzy.springbootlogin.module.customer.controller;

import com.qzy.springbootlogin.pojo.Result;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class FileUploadController {

    @Value("${file.upload.dir:uploads}")
    private String uploadDir;

    @PostMapping("/upload/image")
    public Result uploadImage(@RequestParam("file") MultipartFile file, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return Result.error("请先登录");
        if (file.isEmpty()) return Result.error("请选择文件");
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) return Result.error("仅支持图片格式");

        try {
            Path uploadPath = Paths.get(uploadDir, "images");
            if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);
            String originalName = file.getOriginalFilename();
            String ext = "";
            if (originalName != null && originalName.contains(".")) {
                ext = originalName.substring(originalName.lastIndexOf("."));
            }
            String newFileName = UUID.randomUUID().toString() + ext;
            Path filePath = uploadPath.resolve(newFileName);
            file.transferTo(filePath.toFile());
            Map<String, String> data = new HashMap<>();
            data.put("url", "/uploads/images/" + newFileName);
            data.put("fileName", newFileName);
            return Result.success("上传成功", data);
        } catch (IOException e) {
            return Result.error("上传失败：" + e.getMessage());
        }
    }
}
