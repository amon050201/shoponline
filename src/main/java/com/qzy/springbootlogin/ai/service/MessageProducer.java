package com.qzy.springbootlogin.ai.service;

import com.qzy.springbootlogin.config.RabbitMQConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MessageProducer {

    private static final Logger log = LoggerFactory.getLogger(MessageProducer.class);

    @Autowired(required = false)
    private RabbitTemplate rabbitTemplate;

    /**
     * 发送订单创建消息 — 异步扣库存
     */
    public void sendOrderCreate(Integer orderId) {
        if (rabbitTemplate == null) {
            log.warn("[MQ] RabbitMQ not available, skipping order.create: orderId={}", orderId);
            return;
        }
        try {
            rabbitTemplate.convertAndSend(RabbitMQConfig.ORDER_EXCHANGE,
                    RabbitMQConfig.ROUTING_KEY_CREATE, orderId);
            log.info("[MQ] 发送订单创建消息: orderId={}", orderId);
        } catch (Exception e) {
            log.warn("[MQ] Failed to send order.create: orderId={}, error={}", orderId, e.getMessage());
        }
    }

    /**
     * 发送订单支付消息 — 更新销量
     */
    public void sendOrderPay(Integer orderId) {
        if (rabbitTemplate == null) {
            log.warn("[MQ] RabbitMQ not available, skipping order.pay: orderId={}", orderId);
            return;
        }
        try {
            rabbitTemplate.convertAndSend(RabbitMQConfig.ORDER_EXCHANGE,
                    RabbitMQConfig.ROUTING_KEY_PAY, orderId);
            log.info("[MQ] 发送订单支付消息: orderId={}", orderId);
        } catch (Exception e) {
            log.warn("[MQ] Failed to send order.pay: orderId={}, error={}", orderId, e.getMessage());
        }
    }

    /**
     * 发送订单取消消息 — 恢复库存
     */
    public void sendOrderCancel(Integer orderId) {
        if (rabbitTemplate == null) {
            log.warn("[MQ] RabbitMQ not available, skipping order.cancel: orderId={}", orderId);
            return;
        }
        try {
            rabbitTemplate.convertAndSend(RabbitMQConfig.ORDER_EXCHANGE,
                    RabbitMQConfig.ROUTING_KEY_CANCEL, orderId);
            log.info("[MQ] 发送订单取消消息: orderId={}", orderId);
        } catch (Exception e) {
            log.warn("[MQ] Failed to send order.cancel: orderId={}, error={}", orderId, e.getMessage());
        }
    }
}
