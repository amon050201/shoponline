package com.qzy.springbootlogin.module.exchange.controller;

import com.qzy.springbootlogin.pojo.ExchangeOrder;
import com.qzy.springbootlogin.pojo.Product;
import com.qzy.springbootlogin.pojo.Result;
import com.qzy.springbootlogin.service.ExchangeOrderService;
import com.qzy.springbootlogin.service.ProductService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/exchange")
public class ExchangeApiController {

    @Autowired
    private ExchangeOrderService exchangeOrderService;

    @Autowired
    private ProductService productService;

    @GetMapping("/orders")
    public Result getMyOrders(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return Result.error("请先登录");

        List<ExchangeOrder> initiated = exchangeOrderService.findMyInitiatedOrders(userId);
        List<ExchangeOrder> received = exchangeOrderService.findMyReceivedOrders(userId);

        Map<String, Object> data = new HashMap<>();
        data.put("initiated", initiated);
        data.put("received", received);
        return Result.success("获取成功", data);
    }

    @GetMapping("/orders/{id}")
    public Result getOrderDetail(@PathVariable Integer id, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return Result.error("请先登录");

        ExchangeOrder order = exchangeOrderService.findByIdWithAiValuation(id);
        if (order == null) return Result.error("订单不存在");
        if (!userId.equals(order.getInitiatorId()) && !userId.equals(order.getReceiverId())) {
            return Result.error("无权查看此订单");
        }
        return Result.success("获取成功", order);
    }

    @GetMapping("/orders/order-no/{orderNo}")
    public Result getByOrderNo(@PathVariable String orderNo, HttpSession session) {
        if (session.getAttribute("userId") == null) return Result.error("请先登录");

        ExchangeOrder order = exchangeOrderService.findByOrderNo(orderNo);
        if (order == null) return Result.error("订单不存在");
        return Result.success("获取成功", order);
    }

    @PostMapping("/request")
    public Result createRequest(@RequestBody Map<String, Object> body, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return Result.error("请先登录");

        ExchangeOrder order = new ExchangeOrder();
        order.setInitiatorId(userId);
        order.setReceiverId(Long.valueOf(body.get("receiverId").toString()));
        order.setInitiatorProductId(Integer.valueOf(body.get("initiatorProductId").toString()));
        order.setReceiverProductId(Integer.valueOf(body.get("receiverProductId").toString()));

        if (body.containsKey("priceDifference")) {
            order.setPriceDifference(new BigDecimal(body.get("priceDifference").toString()));
        }
        if (body.containsKey("initiatorAddress")) {
            order.setInitiatorAddress(body.get("initiatorAddress").toString());
        }
        if (body.containsKey("receiverAddress")) {
            order.setReceiverAddress(body.get("receiverAddress").toString());
        }
        if (body.containsKey("remark")) {
            order.setRemark(body.get("remark").toString());
        }

        return exchangeOrderService.createExchangeOrder(order);
    }

    @PostMapping("/orders/{id}/confirm")
    public Result confirm(@PathVariable Integer id, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return Result.error("请先登录");
        return exchangeOrderService.confirmOrder(id, userId);
    }

    @PostMapping("/orders/{id}/reject")
    public Result reject(@PathVariable Integer id, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return Result.error("请先登录");
        return exchangeOrderService.rejectOrder(id, userId);
    }

    @PostMapping("/orders/{id}/cancel")
    public Result cancel(@PathVariable Integer id, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return Result.error("请先登录");
        return exchangeOrderService.cancelOrder(id, userId);
    }

    @PostMapping("/orders/{id}/complete")
    public Result complete(@PathVariable Integer id, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return Result.error("请先登录");
        return exchangeOrderService.completeOrder(id, userId);
    }

    @PostMapping("/orders/{id}/pay")
    public Result pay(@PathVariable Integer id, @RequestParam String paymentMethod, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return Result.error("请先登录");
        return exchangeOrderService.payDifference(id, paymentMethod, userId);
    }

    @DeleteMapping("/orders/{id}")
    public Result delete(@PathVariable Integer id, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return Result.error("请先登录");

        ExchangeOrder order = exchangeOrderService.findById(id);
        if (order == null) return Result.error("订单不存在");
        if (!userId.equals(order.getInitiatorId()) && !userId.equals(order.getReceiverId())) {
            return Result.error("无权操作此订单");
        }
        exchangeOrderService.deleteOrder(id);
        return Result.success("删除成功");
    }

    @GetMapping("/products")
    public Result getExchangeableProducts(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        List<Product> all = productService.findAll();

        List<Map<String, Object>> list = all.stream()
                .filter(p -> userId == null || p.getMerchantId() == null
                        || !p.getMerchantId().equals(userId.intValue()))
                .map(p -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("id", p.getId());
                    m.put("name", p.getName());
                    m.put("price", p.getPrice());
                    m.put("imageUrl", p.getImageUrl());
                    m.put("brand", p.getBrand());
                    m.put("categoryName", p.getCategoryName());
                    m.put("condition", p.getProductCondition());
                    m.put("usageYears", p.getUsageYears());
                    return m;
                })
                .collect(Collectors.toList());

        return Result.success("获取成功", list);
    }

    @GetMapping("/valuation/{productId}")
    public Result getValuation(@PathVariable Integer productId,
                               @RequestParam(defaultValue = "exchange") String scenario) {
        try {
            var report = exchangeOrderService.getExchangeValuation(productId, scenario);
            if (report == null) return Result.error("AI估值服务暂不可用，请检查AI配置");
            return Result.success("估值成功", report);
        } catch (Exception e) {
            return Result.error("估值失败: " + e.getMessage());
        }
    }

    @GetMapping("/recommended-diff/{initiatorProductId}/{receiverProductId}")
    public Result getRecommendedDiff(@PathVariable Integer initiatorProductId,
                                     @PathVariable Integer receiverProductId) {
        try {
            BigDecimal diff = exchangeOrderService.calculateRecommendedDifference(
                    initiatorProductId, receiverProductId);
            return Result.success("计算成功", Map.of("recommendedDifference", diff));
        } catch (Exception e) {
            return Result.error("计算失败: " + e.getMessage());
        }
    }

    @GetMapping("/stats/average-diff")
    public Result getAverageDiff() {
        Double avg = exchangeOrderService.getAveragePriceDifference();
        return Result.success("获取成功", Map.of("averageDifference", avg != null ? avg : 0));
    }

    @GetMapping("/stats/user-count")
    public Result getUserExchangeCount(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return Result.error("请先登录");

        int count = exchangeOrderService.countUserExchanges(userId);
        return Result.success("获取成功", Map.of("totalExchanges", count));
    }
}
