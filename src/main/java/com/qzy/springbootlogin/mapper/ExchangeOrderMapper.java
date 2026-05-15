package com.qzy.springbootlogin.mapper;

import com.qzy.springbootlogin.pojo.ExchangeOrder;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.time.LocalDateTime;
import java.math.BigDecimal;

/**
 * 交换订单Mapper接口
 */
@Mapper
public interface ExchangeOrderMapper {
    
    /**
     * 创建交换订单表
     */
    @Update("CREATE TABLE IF NOT EXISTS exchange_order (" +
            "id INT PRIMARY KEY AUTO_INCREMENT, " +
            "order_no VARCHAR(50) NOT NULL UNIQUE, " +
            "initiator_id BIGINT NOT NULL, " +
            "receiver_id BIGINT NOT NULL, " +
            "initiator_product_id INT NOT NULL, " +
            "receiver_product_id INT NOT NULL, " +
            "price_difference DECIMAL(10,2) DEFAULT 0.00, " +
            "status TINYINT DEFAULT 0, " +
            "payment_status TINYINT DEFAULT 0, " +
            "payment_method VARCHAR(50), " +
            "payment_time DATETIME, " +
            "confirm_time DATETIME, " +
            "complete_time DATETIME, " +
            "initiator_address VARCHAR(500), " +
            "receiver_address VARCHAR(500), " +
            "remark VARCHAR(500), " +
            "ai_initiator_valuation DECIMAL(10,2) DEFAULT NULL, " +
            "ai_receiver_valuation DECIMAL(10,2) DEFAULT NULL, " +
            "recommended_difference DECIMAL(10,2) DEFAULT NULL, " +
            "fraud_risk_score DOUBLE DEFAULT 0.0, " +
            "created_time DATETIME DEFAULT CURRENT_TIMESTAMP, " +
            "updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, " +
            "FOREIGN KEY (initiator_id) REFERENCES users(id), " +
            "FOREIGN KEY (receiver_id) REFERENCES users(id), " +
            "FOREIGN KEY (initiator_product_id) REFERENCES product(id), " +
            "FOREIGN KEY (receiver_product_id) REFERENCES product(id)" +
            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4")
    void createTable();
    
    /**
     * 插入交换订单
     */
    @Insert("INSERT INTO exchange_order (order_no, initiator_id, receiver_id, initiator_product_id, receiver_product_id, " +
            "price_difference, status, payment_status, initiator_address, receiver_address, remark) " +
            "VALUES (#{orderNo}, #{initiatorId}, #{receiverId}, #{initiatorProductId}, #{receiverProductId}, " +
            "#{priceDifference}, #{status}, #{paymentStatus}, #{initiatorAddress}, #{receiverAddress}, #{remark})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ExchangeOrder exchangeOrder);
    
    /**
     * 根据ID查询
     */
    @Select("SELECT * FROM exchange_order WHERE id = #{id}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "orderNo", column = "order_no"),
        @Result(property = "initiatorId", column = "initiator_id"),
        @Result(property = "receiverId", column = "receiver_id"),
        @Result(property = "initiatorProductId", column = "initiator_product_id"),
        @Result(property = "receiverProductId", column = "receiver_product_id"),
        @Result(property = "priceDifference", column = "price_difference"),
        @Result(property = "status", column = "status"),
        @Result(property = "paymentStatus", column = "payment_status"),
        @Result(property = "paymentMethod", column = "payment_method"),
        @Result(property = "paymentTime", column = "payment_time"),
        @Result(property = "confirmTime", column = "confirm_time"),
        @Result(property = "completeTime", column = "complete_time"),
        @Result(property = "initiatorAddress", column = "initiator_address"),
        @Result(property = "receiverAddress", column = "receiver_address"),
        @Result(property = "remark", column = "remark"),
        @Result(property = "aiInitiatorValuation", column = "ai_initiator_valuation"),
        @Result(property = "aiReceiverValuation", column = "ai_receiver_valuation"),
        @Result(property = "recommendedDifference", column = "recommended_difference"),
        @Result(property = "fraudRiskScore", column = "fraud_risk_score"),
        @Result(property = "createdTime", column = "created_time"),
        @Result(property = "updatedTime", column = "updated_time")
    })
    ExchangeOrder findById(Integer id);
    
    /**
     * 根据订单号查询
     */
    @Select("SELECT * FROM exchange_order WHERE order_no = #{orderNo}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "orderNo", column = "order_no"),
        @Result(property = "initiatorId", column = "initiator_id"),
        @Result(property = "receiverId", column = "receiver_id"),
        @Result(property = "initiatorProductId", column = "initiator_product_id"),
        @Result(property = "receiverProductId", column = "receiver_product_id"),
        @Result(property = "priceDifference", column = "price_difference"),
        @Result(property = "status", column = "status"),
        @Result(property = "paymentStatus", column = "payment_status"),
        @Result(property = "paymentMethod", column = "payment_method"),
        @Result(property = "paymentTime", column = "payment_time"),
        @Result(property = "confirmTime", column = "confirm_time"),
        @Result(property = "completeTime", column = "complete_time"),
        @Result(property = "initiatorAddress", column = "initiator_address"),
        @Result(property = "receiverAddress", column = "receiver_address"),
        @Result(property = "remark", column = "remark"),
        @Result(property = "aiInitiatorValuation", column = "ai_initiator_valuation"),
        @Result(property = "aiReceiverValuation", column = "ai_receiver_valuation"),
        @Result(property = "recommendedDifference", column = "recommended_difference"),
        @Result(property = "fraudRiskScore", column = "fraud_risk_score"),
        @Result(property = "createdTime", column = "created_time"),
        @Result(property = "updatedTime", column = "updated_time")
    })
    ExchangeOrder findByOrderNo(String orderNo);
    
    /**
     * 查询我发起的交换订单
     */
    @Select("SELECT * FROM exchange_order WHERE initiator_id = #{userId} ORDER BY created_time DESC")
    List<ExchangeOrder> findByInitiatorId(Long userId);
    
    /**
     * 查询我接收的交换订单
     */
    @Select("SELECT * FROM exchange_order WHERE receiver_id = #{userId} ORDER BY created_time DESC")
    List<ExchangeOrder> findByReceiverId(Long userId);
    
    /**
     * 更新订单状态
     */
    @Update("UPDATE exchange_order SET status = #{status}, updated_time = NOW() WHERE id = #{id}")
    int updateStatus(@Param("id") Integer id, @Param("status") Integer status);
    
    /**
     * 确认订单
     */
    @Update("UPDATE exchange_order SET status = 1, confirm_time = NOW(), updated_time = NOW() WHERE id = #{id}")
    int confirmOrder(Integer id);
    
    /**
     * 完成订单
     */
    @Update("UPDATE exchange_order SET status = 2, complete_time = NOW(), updated_time = NOW() WHERE id = #{id}")
    int completeOrder(Integer id);
    
    /**
     * 取消订单
     */
    @Update("UPDATE exchange_order SET status = 3, updated_time = NOW() WHERE id = #{id}")
    int cancelOrder(Integer id);
    
    /**
     * 拒绝订单
     */
    @Update("UPDATE exchange_order SET status = 4, updated_time = NOW() WHERE id = #{id}")
    int rejectOrder(Integer id);
    
    /**
     * 更新支付信息
     */
    @Update("UPDATE exchange_order SET payment_status = 1, payment_method = #{paymentMethod}, " +
            "payment_time = NOW(), updated_time = NOW() WHERE id = #{id}")
    int updatePayment(@Param("id") Integer id, @Param("paymentMethod") String paymentMethod);
    
    /**
     * 更新AI估值字段
     */
    @Update("UPDATE exchange_order SET ai_initiator_valuation = #{initiatorVal}, " +
            "ai_receiver_valuation = #{receiverVal}, " +
            "recommended_difference = #{recommendedDiff}, " +
            "updated_time = NOW() WHERE id = #{id}")
    int updateAiValuation(@Param("id") Integer id,
                          @Param("initiatorVal") BigDecimal initiatorVal,
                          @Param("receiverVal") BigDecimal receiverVal,
                          @Param("recommendedDiff") BigDecimal recommendedDiff);

    /**
     * 更新欺诈风险评分
     */
    @Update("UPDATE exchange_order SET fraud_risk_score = #{riskScore}, updated_time = NOW() WHERE id = #{id}")
    int updateFraudRiskScore(@Param("id") Integer id, @Param("riskScore") Double riskScore);

    /**
     * 删除订单
     */
    @Delete("DELETE FROM exchange_order WHERE id = #{id}")
    int delete(Integer id);

    /**
     * 获取平均价格差（已完成的交换订单）
     */
    @Select("SELECT AVG(ABS(price_difference)) FROM exchange_order WHERE status >= 1 AND price_difference IS NOT NULL")
    Double getAveragePriceDifference();

    /**
     * 统计用户在一段时间内发起的交换订单数
     */
    @Select("SELECT COUNT(*) FROM exchange_order WHERE initiator_id = #{userId} AND created_time >= #{since}")
    int countByInitiatorSince(@Param("userId") Long userId, @Param("since") LocalDateTime since);
}
