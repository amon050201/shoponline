package com.qzy.springbootlogin.ai.controller;

import com.qzy.springbootlogin.ai.pojo.AiValuationRequest;
import com.qzy.springbootlogin.ai.pojo.ValuationReport;
import com.qzy.springbootlogin.ai.service.SmartValuationService;
import com.qzy.springbootlogin.pojo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ai/valuation")
public class AiValuationController {

    @Autowired
    private SmartValuationService valuationService;

    @PostMapping("/evaluate")
    public Result evaluate(@RequestBody AiValuationRequest request) {
        try {
            ValuationReport report = valuationService.valuate(
                    request.getProductId(),
                    request.getScenario(),
                    request.getExtraInfo()
            );
            return Result.success("估值完成", report);
        } catch (Exception e) {
            return Result.error("估值失败：" + e.getMessage());
        }
    }

    @PostMapping("/evaluate-from-desc")
    public Result evaluateFromDesc(@RequestBody Map<String, String> params) {
        try {
            String productDesc = params.getOrDefault("productDesc", "");
            String brand = params.getOrDefault("brand", "");
            String model = params.getOrDefault("model", "");
            String condition = params.getOrDefault("condition", "good");

            ValuationReport report = valuationService.valuateFromDescription(
                    productDesc, brand, model, condition
            );
            return Result.success("估值完成", report);
        } catch (Exception e) {
            return Result.error("估值失败：" + e.getMessage());
        }
    }
}
