package com.qzy.springbootlogin.module.customer.controller;

import com.qzy.springbootlogin.ai.pojo.ProductDocument;
import com.qzy.springbootlogin.ai.service.ProductSearchService;
import com.qzy.springbootlogin.pojo.Product;
import com.qzy.springbootlogin.pojo.Result;
import com.qzy.springbootlogin.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/customer")
public class CustomerProductController {

    @Autowired
    private ProductService productService;

    @Autowired(required = false)
    private ProductSearchService productSearchService;

    @GetMapping("/products")
    public Result listProducts(@RequestParam(defaultValue = "1") int page,
                               @RequestParam(defaultValue = "20") int size,
                               @RequestParam(required = false) Integer categoryId) {
        List<Product> products;
        if (categoryId != null) {
            products = productService.findByCategoryId(categoryId);
        } else {
            products = productService.findAll();
        }
        int total = products.size();
        int fromIndex = Math.min((page - 1) * size, total);
        int toIndex = Math.min(fromIndex + size, total);
        List<Product> pageData = products.subList(fromIndex, toIndex);
        java.util.Map<String, Object> data = new java.util.HashMap<>();
        data.put("list", pageData);
        data.put("total", total);
        data.put("page", page);
        data.put("size", size);
        data.put("totalPages", (int) Math.ceil((double) total / size));
        return Result.success("获取成功", data);
    }

    @GetMapping("/products/{id}")
    public Result getProduct(@PathVariable Integer id) {
        Product product = productService.findById(id);
        if (product == null) {
            return Result.error("商品不存在");
        }
        return Result.success("获取成功", product);
    }

    @GetMapping("/products/search")
    public Result searchProducts(@RequestParam String keyword,
                                 @RequestParam(defaultValue = "false") boolean smart,
                                 @RequestParam(defaultValue = "sql") String engine) {
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
        return Result.success("搜索成功", products);
    }

    @GetMapping("/products/category/{categoryId}")
    public Result getByCategory(@PathVariable Integer categoryId) {
        List<Product> products = productService.findByCategoryId(categoryId);
        return Result.success("获取成功", products);
    }
}
