package com.qzy.springbootlogin.service.impl;

import com.qzy.springbootlogin.ai.pojo.ValuationReport;
import com.qzy.springbootlogin.ai.service.FraudDetectionService;
import com.qzy.springbootlogin.ai.service.SmartValuationService;
import com.qzy.springbootlogin.mapper.ExchangeOrderMapper;
import com.qzy.springbootlogin.mapper.ProductMapper;
import com.qzy.springbootlogin.pojo.ExchangeOrder;
import com.qzy.springbootlogin.pojo.Product;
import com.qzy.springbootlogin.pojo.Result;
import com.qzy.springbootlogin.service.ExchangeOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 交换订单服务实现类
 */
@Service
public class ExchangeOrderServiceImpl implements ExchangeOrderService {

    private static final Logger log = LoggerFactory.getLogger(ExchangeOrderServiceImpl.class);

    @Autowired
    private ExchangeOrderMapper exchangeOrderMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired(required = false)
    private SmartValuationService valuationService;

    @Autowired(required = false)
    private FraudDetectionService fraudDetectionService;

    @Override
    public void initTable() {
        try {
            exchangeOrderMapper.createTable();
            System.out.println("✅ 交换订单表检查/创建成功");
        } catch (Exception e) {
            System.err.println("❌ 交换订单表创建失败: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public Result<Void> createExchangeOrder(ExchangeOrder order) {
        // 验证商品是否存在
        Product initiatorProduct = productMapper.findById(order.getInitiatorProductId());
        Product receiverProduct = productMapper.findById(order.getReceiverProductId());

        if (initiatorProduct == null || receiverProduct == null) {
            return Result.error("商品不存在");
        }

        // 验证商品归属（仅当商品有明确归属时校验）
        if (initiatorProduct.getMerchantId() != null
                && !initiatorProduct.getMerchantId().equals(order.getInitiatorId().intValue())) {
            return Result.error("发起者商品不属于您");
        }

        if (receiverProduct.getMerchantId() != null
                && !receiverProduct.getMerchantId().equals(order.getReceiverId().intValue())) {
            return Result.error("接收者商品不属于对方");
        }

        // 验证商品状态
        if (initiatorProduct.getStatus() != 1 || receiverProduct.getStatus() != 1) {
            return Result.error("商品已下架");
        }

        // 生成订单号
        String orderNo = generateOrderNo();
        order.setOrderNo(orderNo);
        order.setStatus(0); // 待确认
        order.setPaymentStatus(0); // 未支付
        order.setCreatedTime(LocalDateTime.now());

        // 计算差价：发起者商品价格 - 接收者商品价格
        BigDecimal difference = initiatorProduct.getPrice().subtract(receiverProduct.getPrice());
        order.setPriceDifference(difference);

        // AI自动估值（如果服务可用）
        if (valuationService != null) {
            try {
                String initiatorDesc = initiatorProduct.getDescription() != null ? initiatorProduct.getDescription() : initiatorProduct.getName();
                String receiverDesc = receiverProduct.getDescription() != null ? receiverProduct.getDescription() : receiverProduct.getName();

                ValuationReport initiatorReport = valuationService.valuateFromDescription(
                        initiatorDesc,
                        initiatorProduct.getBrand() != null ? initiatorProduct.getBrand() : "",
                        initiatorProduct.getModel() != null ? initiatorProduct.getModel() : "",
                        initiatorProduct.getProductCondition() != null ? initiatorProduct.getProductCondition() : "good"
                );
                ValuationReport receiverReport = valuationService.valuateFromDescription(
                        receiverDesc,
                        receiverProduct.getBrand() != null ? receiverProduct.getBrand() : "",
                        receiverProduct.getModel() != null ? receiverProduct.getModel() : "",
                        receiverProduct.getProductCondition() != null ? receiverProduct.getProductCondition() : "good"
                );

                order.setAiInitiatorValuation(initiatorReport.getEstimatedValue());
                order.setAiReceiverValuation(receiverReport.getEstimatedValue());

                // AI推荐差价 = 发起者估值 - 接收者估值
                BigDecimal recommendedDiff = initiatorReport.getEstimatedValue()
                        .subtract(receiverReport.getEstimatedValue());
                order.setRecommendedDifference(recommendedDiff);
            } catch (Exception e) {
                log.warn("AI估值失败，使用价格差价: {}", e.getMessage());
                order.setAiInitiatorValuation(initiatorProduct.getPrice());
                order.setAiReceiverValuation(receiverProduct.getPrice());
                order.setRecommendedDifference(difference);
            }
        }

        int result = exchangeOrderMapper.insert(order);
        if (result > 0) {
            // 保存AI估值到数据库
            if (order.getAiInitiatorValuation() != null) {
                exchangeOrderMapper.updateAiValuation(
                        order.getId(),
                        order.getAiInitiatorValuation(),
                        order.getAiReceiverValuation(),
                        order.getRecommendedDifference()
                );
            }
            return Result.success("交换请求已发送，等待对方确认");
        } else {
            return Result.error("创建交换订单失败");
        }
    }
    
    @Override
    public ExchangeOrder findById(Integer id) {
        return exchangeOrderMapper.findById(id);
    }
    
    @Override
    public List<ExchangeOrder> findMyInitiatedOrders(Long userId) {
        List<ExchangeOrder> orders = exchangeOrderMapper.findByInitiatorId(userId);
        // 加载商品信息
        for (ExchangeOrder order : orders) {
            if (order.getInitiatorProductId() != null) {
                order.setInitiatorProduct(productMapper.findById(order.getInitiatorProductId()));
            }
            if (order.getReceiverProductId() != null) {
                order.setReceiverProduct(productMapper.findById(order.getReceiverProductId()));
            }
        }
        return orders;
    }
    
    @Override
    public List<ExchangeOrder> findMyReceivedOrders(Long userId) {
        List<ExchangeOrder> orders = exchangeOrderMapper.findByReceiverId(userId);
        // 加载商品信息
        for (ExchangeOrder order : orders) {
            if (order.getInitiatorProductId() != null) {
                order.setInitiatorProduct(productMapper.findById(order.getInitiatorProductId()));
            }
            if (order.getReceiverProductId() != null) {
                order.setReceiverProduct(productMapper.findById(order.getReceiverProductId()));
            }
        }
        return orders;
    }
    
    @Override
    @Transactional
    public Result<Void> confirmOrder(Integer orderId, Long userId) {
        ExchangeOrder order = exchangeOrderMapper.findById(orderId);
        if (order == null) {
            return Result.error("订单不存在");
        }
        
        if (!order.getReceiverId().equals(userId)) {
            return Result.error("无权操作此订单");
        }
        
        if (order.getStatus() != 0) {
            return Result.error("订单状态不正确");
        }
        
        int result = exchangeOrderMapper.confirmOrder(orderId);
        if (result > 0) {
            return Result.success("订单已确认");
        } else {
            return Result.error("确认失败");
        }
    }
    
    @Override
    @Transactional
    public Result<Void> rejectOrder(Integer orderId, Long userId) {
        ExchangeOrder order = exchangeOrderMapper.findById(orderId);
        if (order == null) {
            return Result.error("订单不存在");
        }
        
        if (!order.getReceiverId().equals(userId)) {
            return Result.error("无权操作此订单");
        }
        
        if (order.getStatus() != 0) {
            return Result.error("订单状态不正确");
        }
        
        int result = exchangeOrderMapper.rejectOrder(orderId);
        if (result > 0) {
            return Result.success("已拒绝交换请求");
        } else {
            return Result.error("拒绝失败");
        }
    }
    
    @Override
    @Transactional
    public Result<Void> cancelOrder(Integer orderId, Long userId) {
        ExchangeOrder order = exchangeOrderMapper.findById(orderId);
        if (order == null) {
            return Result.error("订单不存在");
        }
        
        if (!order.getInitiatorId().equals(userId)) {
            return Result.error("无权操作此订单");
        }
        
        if (order.getStatus() != 0) {
            return Result.error("订单状态不正确，无法取消");
        }
        
        int result = exchangeOrderMapper.cancelOrder(orderId);
        if (result > 0) {
            return Result.success("订单已取消");
        } else {
            return Result.error("取消失败");
        }
    }
    
    @Override
    @Transactional
    public Result<Void> completeOrder(Integer orderId, Long userId) {
        ExchangeOrder order = exchangeOrderMapper.findById(orderId);
        if (order == null) {
            return Result.error("订单不存在");
        }
        
        // 双方都可以确认完成
        if (!order.getInitiatorId().equals(userId) && !order.getReceiverId().equals(userId)) {
            return Result.error("无权操作此订单");
        }
        
        if (order.getStatus() != 1) {
            return Result.error("订单未确认，无法完成");
        }
        
        // 如果有差价且未支付，不能完成
        if (order.getPriceDifference().compareTo(BigDecimal.ZERO) != 0 && order.getPaymentStatus() == 0) {
            return Result.error("请先支付差价");
        }
        
        int result = exchangeOrderMapper.completeOrder(orderId);
        if (result > 0) {
            // 更新商品状态为已售出（下架）
            Product initiatorProduct = productMapper.findById(order.getInitiatorProductId());
            if (initiatorProduct != null) {
                initiatorProduct.setStatus(0);
                productMapper.update(initiatorProduct);
            }

            Product receiverProduct = productMapper.findById(order.getReceiverProductId());
            if (receiverProduct != null) {
                receiverProduct.setStatus(0);
                productMapper.update(receiverProduct);
            }

            // 欺诈检测
            if (fraudDetectionService != null) {
                try {
                    var alert = fraudDetectionService.evaluateExchangeOrder(orderId);
                    if (alert != null) {
                        exchangeOrderMapper.updateFraudRiskScore(orderId, alert.getRiskScore());
                        log.warn("交换订单 {} 触发欺诈告警: {} (风险评分: {})",
                                orderId, alert.getDescription(), alert.getRiskScore());
                    }
                } catch (Exception e) {
                    log.warn("欺诈检测失败: {}", e.getMessage());
                }
            }

            return Result.success("交易已完成");
        } else {
            return Result.error("完成失败");
        }
    }
    
    @Override
    @Transactional
    public Result<Void> payDifference(Integer orderId, String paymentMethod, Long userId) {
        ExchangeOrder order = exchangeOrderMapper.findById(orderId);
        if (order == null) {
            return Result.error("订单不存在");
        }
        
        // 只有需要支付的一方可以支付
        boolean needPay = false;
        if (order.getPriceDifference().compareTo(BigDecimal.ZERO) > 0 && order.getInitiatorId().equals(userId)) {
            needPay = true; // 发起者需补钱
        } else if (order.getPriceDifference().compareTo(BigDecimal.ZERO) < 0 && order.getReceiverId().equals(userId)) {
            needPay = true; // 接收者需补钱
        }
        
        if (!needPay) {
            return Result.error("您无需支付差价");
        }
        
        if (order.getPaymentStatus() == 1) {
            return Result.error("差价已支付");
        }
        
        int result = exchangeOrderMapper.updatePayment(orderId, paymentMethod);
        if (result > 0) {
            return Result.success("支付成功");
        } else {
            return Result.error("支付失败");
        }
    }
    
    @Override
    public ValuationReport getExchangeValuation(Integer productId, String scenario) {
        Product product = productMapper.findById(productId);
        if (product == null) {
            ValuationReport empty = new ValuationReport();
            empty.setEstimatedValue(BigDecimal.ZERO);
            empty.setMarketAnalysis("商品不存在");
            return empty;
        }
        if (valuationService == null) {
            return buildFallbackValuation(product);
        }
        try {
            ValuationReport report = valuationService.valuateFromDescription(
                    product.getDescription() != null ? product.getDescription() : product.getName(),
                    product.getBrand() != null ? product.getBrand() : "",
                    product.getModel() != null ? product.getModel() : "",
                    product.getProductCondition() != null ? product.getProductCondition() : "good"
            );
            if (report.getEstimatedValue().compareTo(BigDecimal.ZERO) <= 0) {
                return buildFallbackValuation(product);
            }
            return report;
        } catch (Exception e) {
            return buildFallbackValuation(product);
        }
    }

    private ValuationReport buildFallbackValuation(Product product) {
        ValuationReport report = new ValuationReport();
        BigDecimal price = product.getPrice() != null ? product.getPrice() : BigDecimal.ZERO;
        report.setEstimatedValue(price.multiply(BigDecimal.valueOf(0.85)).setScale(2, java.math.RoundingMode.HALF_UP));
        report.setMarketRangeLow(price.multiply(BigDecimal.valueOf(0.7)).setScale(2, java.math.RoundingMode.HALF_UP));
        report.setMarketRangeHigh(price.setScale(2, java.math.RoundingMode.HALF_UP));
        report.setCondition("good");
        report.setMarketAnalysis("基于原价" + price + "的统计估值（折旧系数0.85），AI大模型暂未返回有效估值");
        report.setRecommendation("建议参考同品类商品近期成交价");
        return report;
    }

    @Override
    public BigDecimal calculateRecommendedDifference(Integer initiatorProductId, Integer receiverProductId) {
        if (valuationService == null) {
            Product initiatorProduct = productMapper.findById(initiatorProductId);
            Product receiverProduct = productMapper.findById(receiverProductId);
            if (initiatorProduct == null || receiverProduct == null) {
                return BigDecimal.ZERO;
            }
            return initiatorProduct.getPrice().subtract(receiverProduct.getPrice());
        }

        ValuationReport initiatorReport = getExchangeValuation(initiatorProductId, "exchange");
        ValuationReport receiverReport = getExchangeValuation(receiverProductId, "exchange");
        return initiatorReport.getEstimatedValue().subtract(receiverReport.getEstimatedValue());
    }

    @Override
    public ExchangeOrder findByIdWithAiValuation(Integer id) {
        ExchangeOrder order = exchangeOrderMapper.findById(id);
        if (order != null) {
            if (order.getInitiatorProductId() != null) {
                order.setInitiatorProduct(productMapper.findById(order.getInitiatorProductId()));
            }
            if (order.getReceiverProductId() != null) {
                order.setReceiverProduct(productMapper.findById(order.getReceiverProductId()));
            }
        }
        return order;
    }

    @Override
    public ExchangeOrder findByOrderNo(String orderNo) {
        return exchangeOrderMapper.findByOrderNo(orderNo);
    }

    @Override
    public int deleteOrder(Integer id) {
        return exchangeOrderMapper.delete(id);
    }

    @Override
    public Double getAveragePriceDifference() {
        return exchangeOrderMapper.getAveragePriceDifference();
    }

    @Override
    public int countUserExchanges(Long userId) {
        return exchangeOrderMapper.countByInitiatorSince(userId, LocalDateTime.now().minusYears(10));
    }

    /**
     * 生成订单号
     */
    private String generateOrderNo() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String timestamp = LocalDateTime.now().format(formatter);
        Random random = new Random();
        int randomNum = random.nextInt(9000) + 1000;
        return "EX" + timestamp + randomNum;
    }
}
