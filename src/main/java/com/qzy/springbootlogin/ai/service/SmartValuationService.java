package com.qzy.springbootlogin.ai.service;

import com.qzy.springbootlogin.ai.pojo.ValuationReport;

import java.util.Map;

public interface SmartValuationService {
    ValuationReport valuate(Integer productId, String scenario, Map<String, String> extraInfo);
    ValuationReport valuateFromDescription(String productDesc, String brand, String model, String condition);
}
