package com.qzy.springbootlogin.ai.service.impl;

import com.qzy.springbootlogin.ai.pojo.ProductDocument;
import com.qzy.springbootlogin.ai.service.ProductSearchService;
import com.qzy.springbootlogin.mapper.ProductMapper;
import com.qzy.springbootlogin.pojo.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@ConditionalOnProperty(name = "spring.elasticsearch.enabled", havingValue = "false", matchIfMissing = true)
public class ProductSearchFallbackServiceImpl implements ProductSearchService {

    private static final Logger log = LoggerFactory.getLogger(ProductSearchFallbackServiceImpl.class);

    @Autowired
    private ProductMapper productMapper;

    @Override
    public List<ProductDocument> search(String keyword, int page, int size) {
        log.debug("Using fallback search (Elasticsearch disabled) for keyword: {}", keyword);
        // 使用数据库模糊搜索作为降级方案
        List<Product> products = productMapper.searchByName(keyword);
        
        // 转换为 ProductDocument
        List<ProductDocument> documents = products.stream()
                .skip((long) page * size)
                .limit(size)
                .map(this::toDocument)
                .collect(Collectors.toList());
        
        return documents;
    }

    @Override
    public void indexAll() {
        log.info("Elasticsearch is disabled, skipping indexAll operation");
    }

    @Override
    public void index(Product product) {
        log.debug("Elasticsearch is disabled, skipping index operation for product: {}", product != null ? product.getId() : "null");
    }

    @Override
    public void delete(Integer id) {
        log.debug("Elasticsearch is disabled, skipping delete operation for id: {}", id);
    }

    private ProductDocument toDocument(Product p) {
        ProductDocument doc = new ProductDocument();
        doc.setId(p.getId());
        doc.setName(p.getName());
        doc.setBrand(p.getBrand());
        doc.setCategoryName(p.getCategoryName());
        doc.setDescription(p.getDescription());
        doc.setImageUrl(p.getImageUrl());
        doc.setPrice(p.getPrice());
        doc.setOriginalPrice(p.getOriginalPrice());
        doc.setStock(p.getStock());
        doc.setSalesCount(p.getSalesCount());
        doc.setViewCount(p.getViewCount());
        doc.setCategoryId(p.getCategoryId());
        doc.setMerchantId(p.getMerchantId());
        return doc;
    }
}
