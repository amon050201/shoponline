package com.qzy.springbootlogin.ai.service;

import com.qzy.springbootlogin.config.RabbitMQConfig;
import com.qzy.springbootlogin.mapper.OrderMapper;
import com.qzy.springbootlogin.mapper.ProductMapper;
import com.qzy.springbootlogin.pojo.OrderItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderMessageConsumer {

    private static final Logger log = LoggerFactory.getLogger(OrderMessageConsumer.class);

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private ProductMapper productMapper;

    /**
     * 消费订单创建消息 — 异步扣减库存
     */
    @RabbitListener(queues = RabbitMQConfig.ORDER_CREATE_QUEUE)
    public void handleOrderCreate(Integer orderId) {
        log.info("[MQ] 消费订单创建消息: orderId={}", orderId);
        try {
            List<OrderItem> items = orderMapper.findItemsByOrderId(orderId);
            for (OrderItem item : items) {
                productMapper.decreaseStock(item.getProductId(), item.getQuantity());
                log.debug("[MQ] 扣减库存: productId={}, quantity={}", item.getProductId(), item.getQuantity());
            }
        } catch (Exception e) {
            log.error("[MQ] 扣减库存失败: orderId={}", orderId, e);
        }
    }

    /**
     * 消费订单支付消息 — 更新销量
     */
    @RabbitListener(queues = RabbitMQConfig.ORDER_PAY_QUEUE)
    public void handleOrderPay(Integer orderId) {
        log.info("[MQ] 消费订单支付消息: orderId={}", orderId);
        try {
            List<OrderItem> items = orderMapper.findItemsByOrderId(orderId);
            for (OrderItem item : items) {
                productMapper.increaseSalesCount(item.getProductId(), item.getQuantity());
                log.debug("[MQ] 增加销量: productId={}, quantity={}", item.getProductId(), item.getQuantity());
            }
        } catch (Exception e) {
            log.error("[MQ] 更新销量失败: orderId={}", orderId, e);
        }
    }

    /**
     * 消费订单取消消息 — 恢复库存
     */
    @RabbitListener(queues = RabbitMQConfig.ORDER_CANCEL_QUEUE)
    public void handleOrderCancel(Integer orderId) {
        log.info("[MQ] 消费订单取消消息: orderId={}", orderId);
        try {
            List<OrderItem> items = orderMapper.findItemsByOrderId(orderId);
            for (OrderItem item : items) {
                productMapper.increaseStock(item.getProductId(), item.getQuantity());
                log.debug("[MQ] 恢复库存: productId={}, quantity={}", item.getProductId(), item.getQuantity());
            }
        } catch (Exception e) {
            log.error("[MQ] 恢复库存失败: orderId={}", orderId, e);
        }
    }
}
