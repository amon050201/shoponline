package com.qzy.springbootlogin.mapper;

import com.qzy.springbootlogin.pojo.Product;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 商品Mapper接口
 */
@Mapper
public interface ProductMapper {
    
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
     * 智能搜索：多字段匹配 + 相关性评分排序
     */
    List<Product> smartSearch(String keyword);
    
    /**
     * 添加商品
     */
    int insert(Product product);
    
    /**
     * 更新商品
     */
    int update(Product product);
    
    /**
     * 删除商品
     */
    int delete(Integer id);
    
    /**
     * 更新库存
     */
    int updateStock(@Param("id") Integer id, @Param("stock") Integer stock);
    
    /**
     * 增加销量
     */
    int increaseSalesCount(@Param("id") Integer id, @Param("count") Integer count);

    /**
     * 减少库存 (扣减)
     */
    int decreaseStock(@Param("id") Integer id, @Param("quantity") Integer quantity);

    /**
     * 增加库存 (恢复)
     */
    int increaseStock(@Param("id") Integer id, @Param("quantity") Integer quantity);
    
    /**
     * 增加浏览量
     */
    int increaseViewCount(Integer id);

    /**
     * 获取分类商品平均价格
     */
    @Select("SELECT AVG(price) FROM product WHERE category_id = #{categoryId} AND status = 1")
    java.math.BigDecimal getAveragePriceByCategory(@Param("categoryId") Integer categoryId);

    /**
     * 获取分类商品最高价格
     */
    @Select("SELECT MAX(price) FROM product WHERE category_id = #{categoryId} AND status = 1")
    java.math.BigDecimal getMaxPriceByCategory(@Param("categoryId") Integer categoryId);

    /**
     * 获取分类商品最低价格
     */
    @Select("SELECT MIN(price) FROM product WHERE category_id = #{categoryId} AND status = 1")
    java.math.BigDecimal getMinPriceByCategory(@Param("categoryId") Integer categoryId);
}
