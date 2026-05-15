package com.qzy.springbootlogin.controller;

import com.qzy.springbootlogin.ai.pojo.ProductDocument;
import com.qzy.springbootlogin.ai.service.ProductSearchService;
import com.qzy.springbootlogin.pojo.Product;
import com.qzy.springbootlogin.pojo.Result;
import com.qzy.springbootlogin.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 商品控制器
 */
@Controller
@RequestMapping("/product")
public class ProductController {
    
    @Autowired
    private ProductService productService;

    @Autowired(required = false)
    private ProductSearchService productSearchService;
    
    /**
     * 商品列表页面
     */
    @GetMapping("/list")
    public String list(Model model) {
        List<Product> products = productService.findAll();
        model.addAttribute("products", products);
        return "pages/productlist";
    }
    
    /**
     * 商品详情页面
     */
    @GetMapping("/detail/{id}")
    public String detail(@PathVariable Integer id, Model model) {
        Product product = productService.findById(id);
        model.addAttribute("product", product);
        return "pages/productdetail";
    }
    
    /**
     * 根据分类查询商品
     */
    @GetMapping("/category/{categoryId}")
    public String getByCategory(@PathVariable Integer categoryId, Model model) {
        List<Product> products = productService.findByCategoryId(categoryId);
        model.addAttribute("products", products);
        model.addAttribute("categoryId", categoryId);
        return "pages/productlist";
    }
    
    /**
     * 搜索商品
     */
    @GetMapping("/search")
    public String search(@RequestParam String keyword, @RequestParam(defaultValue = "false") boolean smart,
                         @RequestParam(defaultValue = "sql") String engine, Model model) {
        List<Product> products;
        if ("es".equals(engine) && productSearchService != null) {
            List<ProductDocument> docs = productSearchService.search(keyword, 0, 50);
            List<Integer> ids = docs.stream().map(ProductDocument::getId).collect(Collectors.toList());
            products = ids.stream().map(id -> productService.findById(id)).filter(p -> p != null).collect(Collectors.toList());
        } else if (smart) {
            products = productService.smartSearch(keyword);
        } else {
            products = productService.searchByName(keyword);
        }
        model.addAttribute("products", products);
        model.addAttribute("keyword", keyword);
        return "pages/productlist";
    }

    /**
     * 智能搜索API
     */
    @GetMapping("/api/search")
    @ResponseBody
    public Result smartSearchApi(@RequestParam String keyword,
                                 @RequestParam(defaultValue = "sql") String engine) {
        List<Product> products;
        if ("es".equals(engine) && productSearchService != null) {
            List<ProductDocument> docs = productSearchService.search(keyword, 0, 50);
            List<Integer> ids = docs.stream().map(ProductDocument::getId).collect(Collectors.toList());
            products = ids.stream().map(id -> productService.findById(id)).filter(p -> p != null).collect(Collectors.toList());
        } else {
            products = productService.smartSearch(keyword);
        }
        return Result.success("搜索成功", products);
    }
    
    /**
     * 添加商品（管理员功能）
     */
    @PostMapping("/add")
    @ResponseBody
    public Result add(@RequestBody Product product) {
        try {
            int result = productService.addProduct(product);
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
     * 更新商品（管理员功能）
     */
    @PostMapping("/update")
    @ResponseBody
    public Result update(@RequestBody Product product) {
        try {
            int result = productService.updateProduct(product);
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
     * 删除商品（管理员功能）
     */
    @DeleteMapping("/delete/{id}")
    @ResponseBody
    public Result delete(@PathVariable Integer id) {
        try {
            int result = productService.deleteProduct(id);
            if (result > 0) {
                return Result.success("删除成功");
            } else {
                return Result.error("删除失败");
            }
        } catch (Exception e) {
            return Result.error("删除失败：" + e.getMessage());
        }
    }
}
