package com.qzy.springbootlogin.ai.controller;

import com.qzy.springbootlogin.ai.pojo.ImageRecognitionResult;
import com.qzy.springbootlogin.ai.service.ImageRecognitionService;
import com.qzy.springbootlogin.pojo.Product;
import com.qzy.springbootlogin.pojo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/ai/image")
public class AiImageRecognitionController {

    @Autowired
    private ImageRecognitionService imageRecognitionService;

    @PostMapping("/recognize")
    public Result recognize(@RequestParam("image") MultipartFile image) {
        if (image.isEmpty()) {
            return Result.error("请上传图片");
        }
        try {
            ImageRecognitionResult result = imageRecognitionService.recognize(image);
            return Result.success("识别完成", result);
        } catch (Exception e) {
            return Result.error("图片识别失败：" + e.getMessage());
        }
    }

    @PostMapping("/search")
    public Result searchByImage(@RequestParam("image") MultipartFile image) {
        if (image.isEmpty()) {
            return Result.error("请上传图片");
        }
        try {
            List<Product> products = imageRecognitionService.searchByImage(image);
            return Result.success("搜索完成", products);
        } catch (Exception e) {
            return Result.error("以图搜物失败：" + e.getMessage());
        }
    }
}
