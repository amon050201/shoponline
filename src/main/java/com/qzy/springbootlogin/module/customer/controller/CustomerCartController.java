package com.qzy.springbootlogin.module.customer.controller;

import com.qzy.springbootlogin.pojo.Cart;
import com.qzy.springbootlogin.pojo.Result;
import com.qzy.springbootlogin.service.CartService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customer/cart")
public class CustomerCartController {

    @Autowired
    private CartService cartService;

    @GetMapping
    public Result getCart(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return Result.error("请先登录");
        }
        List<Cart> carts = cartService.getCartByUserId(userId.intValue());
        return Result.success("获取成功", carts);
    }

    @PostMapping("/add")
    public Result addToCart(@RequestBody Cart cart, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return Result.error("请先登录");
        }
        try {
            cart.setUserId(userId.intValue());
            int result = cartService.addToCart(cart);
            if (result > 0) {
                return Result.success("添加成功");
            }
            return Result.error("添加失败");
        } catch (Exception e) {
            return Result.error("添加失败：" + e.getMessage());
        }
    }

    @PostMapping("/update-quantity")
    public Result updateQuantity(@RequestParam Integer id, @RequestParam Integer quantity) {
        try {
            int result = cartService.updateQuantity(id, quantity);
            if (result > 0) {
                return Result.success("更新成功");
            }
            return Result.error("更新失败");
        } catch (Exception e) {
            return Result.error("更新失败：" + e.getMessage());
        }
    }

    @PostMapping("/update-selected")
    public Result updateSelected(@RequestParam Integer id, @RequestParam Integer selected) {
        try {
            int result = cartService.updateSelected(id, selected);
            if (result > 0) {
                return Result.success("更新成功");
            }
            return Result.error("更新失败");
        } catch (Exception e) {
            return Result.error("更新失败：" + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public Result deleteFromCart(@PathVariable Integer id) {
        try {
            int result = cartService.removeFromCart(id);
            if (result > 0) {
                return Result.success("删除成功");
            }
            return Result.error("删除失败");
        } catch (Exception e) {
            return Result.error("删除失败：" + e.getMessage());
        }
    }

    @DeleteMapping("/clear")
    public Result clearCart(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return Result.error("请先登录");
        }
        try {
            cartService.clearCart(userId.intValue());
            return Result.success("已清空");
        } catch (Exception e) {
            return Result.error("清空失败：" + e.getMessage());
        }
    }
}
