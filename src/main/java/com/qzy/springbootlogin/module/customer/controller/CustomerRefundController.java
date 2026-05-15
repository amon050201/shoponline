package com.qzy.springbootlogin.module.customer.controller;

import com.qzy.springbootlogin.pojo.Order;
import com.qzy.springbootlogin.pojo.Result;
import com.qzy.springbootlogin.service.OrderService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customer/order")
public class CustomerRefundController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/refund/{orderId}")
    public Result applyRefund(@PathVariable Integer orderId, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return Result.error("请先登录");
        Order order = orderService.findById(orderId);
        if (order == null) return Result.error("订单不存在");
        if (!order.getUserId().equals(userId.intValue())) return Result.error("无权操作此订单");
        if (order.getStatus() < 1 || order.getStatus() > 3) return Result.error("当前订单状态不支持退款");
        orderService.updateOrderStatus(orderId, 5);
        return Result.success("退款申请已提交，请等待处理");
    }

    @PostMapping("/refund/{orderId}/cancel")
    public Result cancelRefund(@PathVariable Integer orderId, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return Result.error("请先登录");
        Order order = orderService.findById(orderId);
        if (order == null) return Result.error("订单不存在");
        if (!order.getUserId().equals(userId.intValue())) return Result.error("无权操作此订单");
        if (order.getStatus() != 5) return Result.error("当前订单不在退款中状态");
        orderService.updateOrderStatus(orderId, 1);
        return Result.success("已取消退款申请");
    }
}
