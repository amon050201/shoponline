package com.qzy.springbootlogin.ai.controller;

import com.qzy.springbootlogin.ai.pojo.PriceHistory;
import com.qzy.springbootlogin.ai.pojo.PricePredictionResult;
import com.qzy.springbootlogin.ai.service.PricePredictionService;
import com.qzy.springbootlogin.pojo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ai/price")
public class AiPricePredictionController {

    @Autowired
    private PricePredictionService pricePredictionService;

    @GetMapping("/predict/{productId}")
    public Result predict(@PathVariable Integer productId,
                          @RequestParam(defaultValue = "30") int forecastDays) {
        try {
            PricePredictionResult result = pricePredictionService.predict(productId, forecastDays);
            return Result.success("价格预测完成", result);
        } catch (Exception e) {
            return Result.error("价格预测失败：" + e.getMessage());
        }
    }

    @GetMapping("/history/{productId}")
    public Result history(@PathVariable Integer productId,
                          @RequestParam(defaultValue = "90") int days) {
        try {
            List<PriceHistory> history = pricePredictionService.getPriceHistory(productId, days);
            return Result.success("历史价格数据", history);
        } catch (Exception e) {
            return Result.error("获取历史价格失败：" + e.getMessage());
        }
    }
}
