package com.qzy.springbootlogin.controller;

import com.qzy.springbootlogin.pojo.Cart;
import com.qzy.springbootlogin.pojo.Result;
import com.qzy.springbootlogin.service.CartService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 购物车控制器
 */
@Controller
@RequestMapping("/cart")
public class CartController {
    
    @Autowired
    private CartService cartService;
    
    /**
     * 购物车页面
     */
    @GetMapping("/view")
    public String view(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }
        
        List<Cart> carts = cartService.getCartByUserId(userId.intValue());
        model.addAttribute("carts", carts);
        return "pages/cart";
    }
    
    /**
     * 获取购物车数据（AJAX）
     */
    @GetMapping("/data")
    @ResponseBody
    public Result getCartData(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return Result.error("请先登录");
        }
        
        List<Cart> carts = cartService.getCartByUserId(userId.intValue());
        return Result.success("获取成功", carts);
    }
    
    /**
     * 添加到购物车
     */
    @PostMapping("/add")
    @ResponseBody
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
            } else {
                return Result.error("添加失败");
            }
        } catch (Exception e) {
            return Result.error("添加失败：" + e.getMessage());
        }
    }
    
    /**
     * 更新购物车项数量
     */
    @PostMapping("/updateQuantity")
    @ResponseBody
    public Result updateQuantity(@RequestParam Integer id, @RequestParam Integer quantity) {
        try {
            int result = cartService.updateQuantity(id, quantity);
            if (result > 0) {
                return Result.success("更新成功");
            } else {
                return Result.error("更新失败");
            }
        } catch (Exception e) {
            return Result.error("更新失败：" + e.getMessage());
        }
    }
    
    /**
     * 更新选中状态
     */
    @PostMapping("/updateSelected")
    @ResponseBody
    public Result updateSelected(@RequestParam Integer id, @RequestParam Integer selected) {
        try {
            int result = cartService.updateSelected(id, selected);
            if (result > 0) {
                return Result.success("更新成功");
            } else {
                return Result.error("更新失败");
            }
        } catch (Exception e) {
            return Result.error("更新失败：" + e.getMessage());
        }
    }
    
    /**
     * 删除购物车项
     */
    @DeleteMapping("/delete/{id}")
    @ResponseBody
    public Result deleteFromCart(@PathVariable Integer id) {
        try {
            int result = cartService.removeFromCart(id);
            if (result > 0) {
                return Result.success("删除成功");
            } else {
                return Result.error("删除失败");
            }
        } catch (Exception e) {
            return Result.error("删除失败：" + e.getMessage());
        }
    }
    
    /**
     * 清空购物车
     */
    @DeleteMapping("/clear")
    @ResponseBody
    public Result clearCart(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return Result.error("请先登录");
        }
        
        try {
            int result = cartService.clearCart(userId.intValue());
            if (result > 0) {
                return Result.success("清空成功");
            } else {
                return Result.success("购物车为空");
            }
        } catch (Exception e) {
            return Result.error("清空失败：" + e.getMessage());
        }
    }
}
