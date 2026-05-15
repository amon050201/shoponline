package com.qzy.springbootlogin.module.merchant.controller;

import com.qzy.springbootlogin.pojo.Product;
import com.qzy.springbootlogin.pojo.Result;
import com.qzy.springbootlogin.service.OrderService;
import com.qzy.springbootlogin.service.ProductService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/merchant")
public class MerchantProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderService orderService;

    @GetMapping("/products")
    public Result listMyProducts(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return Result.error("请先登录");
        }
        List<Product> products = productService.findByMerchantId(userId.intValue());
        return Result.success("获取成功", products);
    }

    @PostMapping("/product/add")
    public Result addProduct(@RequestBody Product product, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return Result.error("请先登录");
        }
        try {
            product.setMerchantId(userId.intValue());
            int result = productService.addProduct(product);
            if (result > 0) {
                return Result.success("商品添加成功");
            }
            return Result.error("商品添加失败");
        } catch (Exception e) {
            return Result.error("添加失败：" + e.getMessage());
        }
    }

    @PostMapping("/product/update")
    public Result updateProduct(@RequestBody Product product, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return Result.error("请先登录");
        }
        try {
            Product existing = productService.findById(product.getId());
            if (existing == null || !existing.getMerchantId().equals(userId.intValue())) {
                return Result.error("无权修改此商品");
            }
            int result = productService.updateProduct(product);
            if (result > 0) {
                return Result.success("商品更新成功");
            }
            return Result.error("商品更新失败");
        } catch (Exception e) {
            return Result.error("更新失败：" + e.getMessage());
        }
    }

    @DeleteMapping("/product/{id}")
    public Result deleteProduct(@PathVariable Integer id, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return Result.error("请先登录");
        }
        try {
            Product existing = productService.findById(id);
            if (existing == null || !existing.getMerchantId().equals(userId.intValue())) {
                return Result.error("无权删除此商品");
            }
            int result = productService.deleteProduct(id);
            if (result > 0) {
                return Result.success("商品删除成功");
            }
            return Result.error("商品删除失败");
        } catch (Exception e) {
            return Result.error("删除失败：" + e.getMessage());
        }
    }

    @PostMapping("/order/ship/{orderId}")
    public Result shipOrder(@PathVariable Integer orderId,
                            @RequestParam String shippingCompany,
                            @RequestParam String trackingNumber) {
        try {
            boolean success = orderService.shipOrder(orderId, shippingCompany, trackingNumber);
            if (success) {
                return Result.success("发货成功");
            }
            return Result.error("发货失败，订单状态不正确");
        } catch (Exception e) {
            return Result.error("发货失败：" + e.getMessage());
        }
    }
}
