package com.qzy.springbootlogin.service;

import com.qzy.springbootlogin.pojo.Product;

import java.util.List;

/**
 * 商品服务接口
 */
public interface ProductService {
    
    /**
     * 查询所有商品
     */
    List<Product> findAll();
    
    /**
     * 根据ID查询商品
     */
    Product findById(Integer id);
    
    /**
     * 根据分类ID查询商品
     */
    List<Product> findByCategoryId(Integer categoryId);
    
    /**
     * 根据商家ID查询商品
     */
    List<Product> findByMerchantId(Integer merchantId);
    
    /**
     * 搜索商品
     */
    List<Product> searchByName(String keyword);

    /**
     * 智能搜索：多字段匹配 + 相关性排序
     */
    List<Product> smartSearch(String keyword);
    
    /**
     * 添加商品
     */
    int addProduct(Product product);
    
    /**
     * 更新商品
     */
    int updateProduct(Product product);
    
    /**
     * 删除商品
     */
    int deleteProduct(Integer id);
}
