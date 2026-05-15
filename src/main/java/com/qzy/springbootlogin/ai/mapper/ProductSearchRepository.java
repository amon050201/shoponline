package com.qzy.springbootlogin.ai.mapper;

import com.qzy.springbootlogin.ai.pojo.ProductDocument;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@ConditionalOnProperty(name = "spring.elasticsearch.enabled", havingValue = "true", matchIfMissing = false)
public interface ProductSearchRepository extends ElasticsearchRepository<ProductDocument, Integer> {

    List<ProductDocument> findByNameContaining(String name);
}
