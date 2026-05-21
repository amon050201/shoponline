package com.qzy.springbootlogin.service;

import com.qzy.springbootlogin.pojo.ExchangeOrder;
import com.qzy.springbootlogin.pojo.Result;

import java.util.List;
import java.math.BigDecimal;

/**
 * 交换订单服务接口
 */
public interface ExchangeOrderService {
    
    /**
     * 初始化表
     */
    void initTable();
    
    /**
     * 创建交换订单
     */
    Result<Void> createExchangeOrder(ExchangeOrder order);
    
    /**
     * 根据ID查询
     */
    ExchangeOrder findById(Integer id);
    
    /**
     * 查询我发起的订单
     */
    List<ExchangeOrder> findMyInitiatedOrders(Long userId);
    
    /**
     * 查询我接收的订单
     */
    List<ExchangeOrder> findMyReceivedOrders(Long userId);
    
    /**
     * 确认订单
     */
    Result<Void> confirmOrder(Integer orderId, Long userId);
    
    /**
     * 拒绝订单
     */
    Result<Void> rejectOrder(Integer orderId, Long userId);
    
    /**
     * 取消订单
     */
    Result<Void> cancelOrder(Integer orderId, Long userId);
    
    /**
     * 完成订单
     */
    Result<Void> completeOrder(Integer orderId, Long userId);
    
    /**
     * 支付差价
     */
    Result<Void> payDifference(Integer orderId, String paymentMethod, Long userId);

    /**
     * 获取商品AI估值
     */
    com.qzy.springbootlogin.ai.pojo.ValuationReport getExchangeValuation(Integer productId, String scenario);

    /**
     * 计算AI推荐差价
     */
    BigDecimal calculateRecommendedDifference(Integer initiatorProductId, Integer receiverProductId);

    /**
     * 获取带AI估值的订单详情
     */
    ExchangeOrder findByIdWithAiValuation(Integer id);

    ExchangeOrder findByOrderNo(String orderNo);
    int deleteOrder(Integer id);
    Double getAveragePriceDifference();
    int countUserExchanges(Long userId);
}
