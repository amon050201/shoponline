package com.qzy.springbootlogin.service.impl;

import com.qzy.springbootlogin.mapper.ProductMapper;
import com.qzy.springbootlogin.pojo.Product;
import com.qzy.springbootlogin.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 商品服务实现类
 */
@Service
public class ProductServiceImpl implements ProductService {
    
    @Autowired
    private ProductMapper productMapper;
    
    @Override
    @Cacheable(value = "products:all", unless = "#result == null || #result.isEmpty()")
    public List<Product> findAll() {
        return productMapper.findAll();
    }

    @Override
    @Cacheable(value = "product", key = "#id", unless = "#result == null")
    public Product findById(Integer id) {
        // 增加浏览量
        productMapper.increaseViewCount(id);
        return productMapper.findById(id);
    }

    @Override
    @Cacheable(value = "products:category", key = "#categoryId", unless = "#result == null || #result.isEmpty()")
    public List<Product> findByCategoryId(Integer categoryId) {
        return productMapper.findByCategoryId(categoryId);
    }
    
    @Override
    public List<Product> findByMerchantId(Integer merchantId) {
        return productMapper.findByMerchantId(merchantId);
    }
    
    @Override
    public List<Product> searchByName(String keyword) {
        return productMapper.searchByName(keyword);
    }

    @Override
    public List<Product> smartSearch(String keyword) {
        return productMapper.smartSearch(keyword);
    }
    
    @Override
    @CacheEvict(value = {"products:all", "products:category"}, allEntries = true)
    public int addProduct(Product product) {
        // 默认状态为上架
        if (product.getStatus() == null) {
            product.setStatus(1);
        }
        return productMapper.insert(product);
    }
    
    @Override
    @CacheEvict(value = {"product", "products:all", "products:category"}, allEntries = true)
    public int updateProduct(Product product) {
        return productMapper.update(product);
    }
    
    @Override
    @CacheEvict(value = {"product", "products:all", "products:category"}, allEntries = true)
    public int deleteProduct(Integer id) {
        return productMapper.delete(id);
    }
}
