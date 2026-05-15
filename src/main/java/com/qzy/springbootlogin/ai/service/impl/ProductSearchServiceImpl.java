package com.qzy.springbootlogin.ai.service.impl;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import com.qzy.springbootlogin.ai.mapper.ProductSearchRepository;
import com.qzy.springbootlogin.ai.pojo.ProductDocument;
import com.qzy.springbootlogin.ai.service.ProductSearchService;
import com.qzy.springbootlogin.mapper.ProductMapper;
import com.qzy.springbootlogin.pojo.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@ConditionalOnProperty(name = "spring.elasticsearch.enabled", havingValue = "true", matchIfMissing = false)
public class ProductSearchServiceImpl implements ProductSearchService {

    private static final Logger log = LoggerFactory.getLogger(ProductSearchServiceImpl.class);

    @Autowired(required = false)
    private ProductSearchRepository productSearchRepository;

    @Autowired
    private ProductMapper productMapper;

    @Autowired(required = false)
    private ElasticsearchOperations elasticsearchOperations;

    @Override
    public List<ProductDocument> search(String keyword, int page, int size) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return new ArrayList<>();
        }
        Query query = NativeQuery.builder()
                .withQuery(QueryBuilders.bool(b -> b
                        .should(QueryBuilders.match(m -> m.field("name").query(keyword).boost(3.0f)))
                        .should(QueryBuilders.match(m -> m.field("brand").query(keyword).boost(2.0f)))
                        .should(QueryBuilders.match(m -> m.field("categoryName").query(keyword).boost(1.5f)))
                        .should(QueryBuilders.match(m -> m.field("description").query(keyword).boost(1.0f)))
                        .minimumShouldMatch("1")
                ))
                .withPageable(org.springframework.data.domain.PageRequest.of(page, size))
                .build();

        SearchHits<ProductDocument> hits = elasticsearchOperations.search(query, ProductDocument.class);
        return hits.getSearchHits().stream()
                .map(h -> {
                    ProductDocument doc = h.getContent();
                    doc.setSalesCount((int) h.getScore()); // store relevance score
                    return doc;
                })
                .collect(Collectors.toList());
    }

    @Override
    public void indexAll() {
        List<Product> products = productMapper.findAll();
        if (products.isEmpty()) {
            log.info("No products to index");
            return;
        }
        List<ProductDocument> documents = products.stream().map(this::toDocument).toList();
        productSearchRepository.saveAll(documents);
        log.info("Indexed {} products to Elasticsearch", documents.size());
    }

    @Override
    public void index(Product product) {
        if (product == null) return;
        productSearchRepository.save(toDocument(product));
    }

    @Override
    public void delete(Integer id) {
        if (id == null) return;
        productSearchRepository.deleteById(id);
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
