package com.qzy.springbootlogin.ai.service;

import com.qzy.springbootlogin.ai.pojo.ProductDocument;
import com.qzy.springbootlogin.pojo.Product;

import java.util.List;

public interface ProductSearchService {

    List<ProductDocument> search(String keyword, int page, int size);

    void indexAll();

    void index(Product product);

    void delete(Integer id);
}
