package com.qzy.springbootlogin.module.customer.controller;

import com.qzy.springbootlogin.pojo.Order;
import com.qzy.springbootlogin.pojo.Result;
import com.qzy.springbootlogin.service.OrderService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/customer/order")
public class CustomerOrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping
    public Result listOrders(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return Result.error("请先登录");
        }
        List<Order> orders = orderService.findByUserId(userId.intValue());
        return Result.success("获取成功", orders);
    }

    @GetMapping("/{id}")
    public Result getOrder(@PathVariable Integer id) {
        Order order = orderService.findById(id);
        if (order == null) {
            return Result.error("订单不存在");
        }
        return Result.success("获取成功", order);
    }

    @PostMapping("/pay/{orderId}")
    public Result pay(@PathVariable Integer orderId,
                      @RequestParam(required = false) String paymentMethod,
                      @RequestBody(required = false) Map<String, String> body) {
        String method = paymentMethod;
        if (method == null && body != null) {
            method = body.get("paymentMethod");
        }
        if (method == null) {
            return Result.error("请指定支付方式");
        }
        try {
            boolean success = orderService.payOrder(orderId, method);
            if (success) {
                return Result.success("支付成功");
            }
            return Result.error("支付失败");
        } catch (Exception e) {
            return Result.error("支付失败：" + e.getMessage());
        }
    }

    @PostMapping("/cancel/{orderId}")
    public Result cancel(@PathVariable Integer orderId) {
        try {
            int result = orderService.updateOrderStatus(orderId, 4);
            if (result > 0) {
                return Result.success("订单已取消");
            }
            return Result.error("取消失败");
        } catch (Exception e) {
            return Result.error("取消失败：" + e.getMessage());
        }
    }

    @PostMapping("/receive/{orderId}")
    public Result receive(@PathVariable Integer orderId) {
        try {
            boolean success = orderService.confirmReceived(orderId);
            if (success) {
                return Result.success("已确认收货");
            }
            return Result.error("确认收货失败");
        } catch (Exception e) {
            return Result.error("确认收货失败：" + e.getMessage());
        }
    }
}
