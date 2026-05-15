package com.qzy.springbootlogin.mapper;

import com.qzy.springbootlogin.pojo.Order;
import com.qzy.springbootlogin.pojo.OrderItem;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.time.LocalDateTime;

/**
 * 订单Mapper接口
 */
@Mapper
public interface OrderMapper {
    
    /**
     * 查询所有订单
     */
    List<Order> findAll();
    
    /**
     * 根据用户ID查询订单
     */
    List<Order> findByUserId(Integer userId);
    
    /**
     * 根据订单号查询订单
     */
    Order findByOrderNo(String orderNo);
    
    /**
     * 根据ID查询订单
     */
    Order findById(Integer id);
    
    /**
     * 添加订单
     */
    int insert(Order order);
    
    /**
     * 更新订单
     */
    int update(Order order);
    
    /**
     * 更新订单状态
     */
    int updateStatus(@Param("id") Integer id, @Param("status") Integer status);
    
    /**
     * 删除订单
     */
    int delete(Integer id);
    
    // ==================== 订单项相关 ====================
    
    /**
     * 根据订单ID查询订单项
     */
    List<OrderItem> findItemsByOrderId(Integer orderId);
    
    /**
     * 添加订单项
     */
    int insertOrderItem(OrderItem orderItem);
    
    /**
     * 批量添加订单项
     */
    int batchInsertOrderItems(@Param("items") List<OrderItem> items);

    /**
     * 发货 - 更新物流信息和状态
     */
    @Update("UPDATE orders SET status = 2, shipping_company = #{shippingCompany}, " +
            "tracking_number = #{trackingNumber}, delivery_time = NOW(), " +
            "updated_time = NOW() WHERE id = #{id} AND status = 1")
    int shipOrder(@Param("id") Integer id,
                  @Param("shippingCompany") String shippingCompany,
                  @Param("trackingNumber") String trackingNumber);

    /**
     * 确认收货 - 更新状态为已完成
     */
    @Update("UPDATE orders SET status = 3, updated_time = NOW() WHERE id = #{id} AND status = 2")
    int confirmReceived(Integer id);

    /**
     * 统计用户最近N分钟内的订单数
     */
    @Select("SELECT COUNT(*) FROM orders WHERE user_id = #{userId} AND created_time >= DATE_SUB(NOW(), INTERVAL #{minutes} MINUTE)")
    int countRecentOrders(@Param("userId") Long userId, @Param("minutes") int minutes);

    /**
     * 统计用户自某时间以来的总订单数
     */
    @Select("SELECT COUNT(*) FROM orders WHERE user_id = #{userId} AND created_time >= #{since}")
    int countByUserIdSince(@Param("userId") Long userId, @Param("since") LocalDateTime since);

    /**
     * 统计用户自某时间以来的取消订单数
     */
    @Select("SELECT COUNT(*) FROM orders WHERE user_id = #{userId} AND status = 4 AND created_time >= #{since}")
    int countCancelledByUserIdSince(@Param("userId") Long userId, @Param("since") LocalDateTime since);
}
