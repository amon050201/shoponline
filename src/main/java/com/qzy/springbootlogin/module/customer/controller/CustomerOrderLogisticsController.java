package com.qzy.springbootlogin.module.customer.controller;

import com.qzy.springbootlogin.pojo.Order;
import com.qzy.springbootlogin.pojo.Result;
import com.qzy.springbootlogin.service.OrderService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/customer/order")
public class CustomerOrderLogisticsController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/{id}/logistics")
    public Result getLogistics(@PathVariable Integer id, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return Result.error("请先登录");
        Order order = orderService.findById(id);
        if (order == null) return Result.error("订单不存在");
        if (!order.getUserId().equals(userId.intValue())) return Result.error("无权查看此订单");
        Map<String, Object> data = new HashMap<>();
        data.put("orderNo", order.getOrderNo());
        data.put("status", order.getStatus());
        data.put("shippingCompany", order.getShippingCompany());
        data.put("trackingNumber", order.getTrackingNumber());
        data.put("deliveryTime", order.getDeliveryTime());
        data.put("receiverName", order.getReceiverName());
        data.put("shippingAddress", order.getShippingAddress());
        return Result.success("获取成功", data);
    }
}
