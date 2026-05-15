package com.qzy.springbootlogin.service;

import com.qzy.springbootlogin.pojo.Order;
import com.qzy.springbootlogin.pojo.OrderItem;

import java.util.List;

/**
 * 订单服务接口
 */
public interface OrderService {
    
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
     * 创建订单
     */
    Order createOrder(Order order, List<OrderItem> items);
    
    /**
     * 更新订单状态
     */
    int updateOrderStatus(Integer id, Integer status);
    
    /**
     * 删除订单
     */
    int deleteOrder(Integer id);
    
    /**
     * 支付订单
     */
    boolean payOrder(Integer orderId, String paymentMethod);

    /**
     * 支付回调 — 通过订单号和交易号确认支付
     */
    boolean payOrder(String orderNo, String transactionId);

    /**
     * 更新支付信息（交易号 + 支付方式）
     */
    boolean updatePaymentInfo(Integer orderId, String transactionId, String paymentMethod);

    /**
     * 发货
     */
    boolean shipOrder(Integer orderId, String shippingCompany, String trackingNumber);

    /**
     * 确认收货
     */
    boolean confirmReceived(Integer orderId);
}
