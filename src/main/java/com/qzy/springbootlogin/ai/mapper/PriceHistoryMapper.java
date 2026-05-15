package com.qzy.springbootlogin.ai.mapper;

import com.qzy.springbootlogin.ai.pojo.PriceHistory;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface PriceHistoryMapper {

    @Insert("INSERT INTO price_history (product_id, price, recorded_at) VALUES (#{productId}, #{price}, #{recordedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(PriceHistory history);

    @Select("SELECT * FROM price_history WHERE product_id = #{productId} ORDER BY recorded_at ASC")
    List<PriceHistory> findByProductId(@Param("productId") Integer productId);

    @Select("SELECT * FROM price_history WHERE product_id = #{productId} AND recorded_at >= #{since} ORDER BY recorded_at ASC")
    List<PriceHistory> findByProductIdAndDateRange(@Param("productId") Integer productId, @Param("since") LocalDateTime since);

    @Select("SELECT * FROM price_history WHERE product_id = #{productId} ORDER BY recorded_at DESC LIMIT 1")
    PriceHistory getLatestByProductId(@Param("productId") Integer productId);

    @Select("SELECT COUNT(*) FROM price_history WHERE product_id = #{productId}")
    int countByProductId(@Param("productId") Integer productId);
}
