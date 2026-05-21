package com.qzy.springbootlogin.controller;

import com.qzy.springbootlogin.pojo.Product;
import com.qzy.springbootlogin.pojo.Result;
import com.qzy.springbootlogin.service.ProductService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商家控制器 - 管理商品
 */
@Controller
@RequestMapping("/merchant")
public class MerchantController {
    
    @Autowired
    private ProductService productService;
    
    /**
     * 商家中心首页
     */
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }
        
        // 获取商家发布的商品
        List<Product> products = productService.findByMerchantId(userId.intValue());
        model.addAttribute("products", products);
        model.addAttribute("username", session.getAttribute("username"));
        model.addAttribute("roleType", session.getAttribute("roleType"));
        
        return "pages/merchant/dashboard";
    }
    
    /**
     * 添加商品页面
     */
    @GetMapping("/product/add")
    public String addProductPage(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }
        
        model.addAttribute("username", session.getAttribute("username"));
        model.addAttribute("roleType", session.getAttribute("roleType"));
        return "pages/merchant/add-product";
    }
    
    /**
     * 添加商品
     */
    @PostMapping("/product/add")
    @ResponseBody
    public Result addProduct(@RequestBody Product product, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return Result.error("请先登录");
        }
        
        try {
            product.setMerchantId(userId.intValue());
            int result = productService.addProduct(product);
            if (result > 0) {
                var data = new java.util.HashMap<String, Object>();
                data.put("id", product.getId());
                return Result.success("商品添加成功", data);
            } else {
                return Result.error("商品添加失败");
            }
        } catch (Exception e) {
            return Result.error("添加失败：" + e.getMessage());
        }
    }
    
    /**
     * 编辑商品页面
     */
    @GetMapping("/product/edit/{id}")
    public String editProductPage(@PathVariable Integer id, HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }
        
        Product product = productService.findById(id);
        if (product == null || !product.getMerchantId().equals(userId.intValue())) {
            return "redirect:/merchant/dashboard";
        }
        
        model.addAttribute("product", product);
        model.addAttribute("username", session.getAttribute("username"));
        model.addAttribute("roleType", session.getAttribute("roleType"));
        return "pages/merchant/edit-product";
    }
    
    /**
     * 更新商品
     */
    @PostMapping("/product/update")
    @ResponseBody
    public Result updateProduct(@RequestBody Product product, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return Result.error("请先登录");
        }
        
        try {
            // 验证商品归属
            Product existingProduct = productService.findById(product.getId());
            if (existingProduct == null || !existingProduct.getMerchantId().equals(userId.intValue())) {
                return Result.error("无权修改此商品");
            }
            
            int result = productService.updateProduct(product);
            if (result > 0) {
                return Result.success("商品更新成功");
            } else {
                return Result.error("商品更新失败");
            }
        } catch (Exception e) {
            return Result.error("更新失败：" + e.getMessage());
        }
    }
    
    /**
     * 删除商品
     */
    @DeleteMapping("/product/delete/{id}")
    @ResponseBody
    public Result deleteProduct(@PathVariable Integer id, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return Result.error("请先登录");
        }
        
        try {
            // 验证商品归属
            Product existingProduct = productService.findById(id);
            if (existingProduct == null || !existingProduct.getMerchantId().equals(userId.intValue())) {
                return Result.error("无权删除此商品");
            }
            
            int result = productService.deleteProduct(id);
            if (result > 0) {
                return Result.success("商品删除成功");
            } else {
                return Result.error("商品删除失败");
            }
        } catch (Exception e) {
            return Result.error("删除失败：" + e.getMessage());
        }
    }
}
