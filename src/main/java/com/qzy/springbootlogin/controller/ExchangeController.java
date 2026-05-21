package com.qzy.springbootlogin.controller;

import com.qzy.springbootlogin.ai.pojo.ValuationReport;
import com.qzy.springbootlogin.pojo.ExchangeOrder;
import com.qzy.springbootlogin.pojo.Product;
import com.qzy.springbootlogin.pojo.Result;
import com.qzy.springbootlogin.service.ExchangeOrderService;
import com.qzy.springbootlogin.service.ProductService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 交换订单控制器 - 以物易物
 */
@Controller
@RequestMapping("/exchange")
public class ExchangeController {
    
    @Autowired
    private ExchangeOrderService exchangeOrderService;
    
    @Autowired
    private ProductService productService;
    
    /**
     * 交换市场首页
     */
    @GetMapping("/market")
    public String market(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }
        
        // 获取所有可交换的商品（排除自己的）
        List<Product> products = productService.findAll();
        model.addAttribute("products", products);
        model.addAttribute("username", session.getAttribute("username"));
        model.addAttribute("roleType", session.getAttribute("roleType"));
        
        return "pages/exchange/market";
    }
    
    /**
     * 发起交换请求页面
     */
    @GetMapping("/request/{productId}")
    public String requestPage(@PathVariable Integer productId, HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }
        
        Product targetProduct = productService.findById(productId);
        if (targetProduct == null) {
            return "redirect:/exchange/market";
        }

        // 获取可交换的商品列表（排除目标商品）
        List<Product> allProducts = productService.findAll();
        List<Product> myProducts = new ArrayList<>();
        for (Product p : allProducts) {
            if (!p.getId().equals(productId)) {
                myProducts.add(p);
            }
        }

        model.addAttribute("targetProduct", targetProduct);
        model.addAttribute("myProducts", myProducts);
        model.addAttribute("username", session.getAttribute("username"));
        model.addAttribute("roleType", session.getAttribute("roleType"));
        
        return "pages/exchange/request";
    }
    
    /**
     * 提交交换请求
     */
    @PostMapping("/request")
    @ResponseBody
    public Result submitRequest(@RequestBody ExchangeOrder order, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return Result.error("请先登录");
        }
        
        order.setInitiatorId(userId);
        
        return exchangeOrderService.createExchangeOrder(order);
    }
    
    /**
     * 我的交换订单 - JSON接口
     */
    @GetMapping("/my-orders-json")
    @ResponseBody
    public Result myOrdersJson(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return Result.error("请先登录");
        }
        List<ExchangeOrder> initiatedOrders = exchangeOrderService.findMyInitiatedOrders(userId);
        List<ExchangeOrder> receivedOrders = exchangeOrderService.findMyReceivedOrders(userId);
        java.util.Map<String, Object> data = new java.util.HashMap<>();
        data.put("initiated", initiatedOrders);
        data.put("received", receivedOrders);
        return Result.success("获取成功", data);
    }

    /**
     * 我的交换订单
     */
    @GetMapping("/my-orders")
    public String myOrders(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }
        
        List<ExchangeOrder> initiatedOrders = exchangeOrderService.findMyInitiatedOrders(userId);
        List<ExchangeOrder> receivedOrders = exchangeOrderService.findMyReceivedOrders(userId);
        
        model.addAttribute("initiatedOrders", initiatedOrders);
        model.addAttribute("receivedOrders", receivedOrders);
        model.addAttribute("username", session.getAttribute("username"));
        model.addAttribute("roleType", session.getAttribute("roleType"));
        
        return "pages/exchange/my-orders";
    }
    
    /**
     * 确认订单
     */
    @PostMapping("/confirm/{id}")
    @ResponseBody
    public Result confirmOrder(@PathVariable Integer id, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return Result.error("请先登录");
        }
        
        return exchangeOrderService.confirmOrder(id, userId);
    }
    
    /**
     * 拒绝订单
     */
    @PostMapping("/reject/{id}")
    @ResponseBody
    public Result rejectOrder(@PathVariable Integer id, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return Result.error("请先登录");
        }
        
        return exchangeOrderService.rejectOrder(id, userId);
    }
    
    /**
     * 取消订单
     */
    @PostMapping("/cancel/{id}")
    @ResponseBody
    public Result cancelOrder(@PathVariable Integer id, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return Result.error("请先登录");
        }
        
        return exchangeOrderService.cancelOrder(id, userId);
    }
    
    /**
     * 完成订单
     */
    @PostMapping("/complete/{id}")
    @ResponseBody
    public Result completeOrder(@PathVariable Integer id, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return Result.error("请先登录");
        }
        
        return exchangeOrderService.completeOrder(id, userId);
    }
    
    /**
     * 支付差价
     */
    @PostMapping("/pay/{id}")
    @ResponseBody
    public Result payDifference(@PathVariable Integer id, @RequestParam String paymentMethod, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return Result.error("请先登录");
        }

        return exchangeOrderService.payDifference(id, paymentMethod, userId);
    }

    /**
     * 获取商品AI估值（用于交换参考）
     */
    @GetMapping("/valuation/{productId}")
    @ResponseBody
    public Result getValuation(@PathVariable Integer productId, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return Result.error("请先登录");
        }
        try {
            ValuationReport report = exchangeOrderService.getExchangeValuation(productId, "exchange");
            return Result.success("估值完成", report);
        } catch (Exception e) {
            return Result.error("估值失败：" + e.getMessage());
        }
    }

    /**
     * 获取AI推荐差价（多退少补参考）
     */
    @GetMapping("/recommended-diff/{initiatorProductId}/{receiverProductId}")
    @ResponseBody
    public Result getRecommendedDifference(@PathVariable Integer initiatorProductId,
                                            @PathVariable Integer receiverProductId,
                                            HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return Result.error("请先登录");
        }
        try {
            BigDecimal diff = exchangeOrderService.calculateRecommendedDifference(
                    initiatorProductId, receiverProductId);
            return Result.success("推荐差价计算完成", diff);
        } catch (Exception e) {
            return Result.error("计算失败：" + e.getMessage());
        }
    }
}
