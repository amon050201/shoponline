package com.qzy.springbootlogin.ai.service;

import com.qzy.springbootlogin.ai.pojo.ImageRecognitionResult;
import com.qzy.springbootlogin.pojo.Product;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ImageRecognitionService {
    ImageRecognitionResult recognize(MultipartFile image);
    List<Product> searchByImage(MultipartFile image);
}
