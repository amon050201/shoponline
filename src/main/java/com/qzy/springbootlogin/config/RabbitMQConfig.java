package com.qzy.springbootlogin.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    /** 订单交换机 (Topic) */
    public static final String ORDER_EXCHANGE = "order.exchange";

    /** 订单创建队列 — 异步扣库存 */
    public static final String ORDER_CREATE_QUEUE = "order.create.queue";
    /** 订单支付队列 — 更新销量 */
    public static final String ORDER_PAY_QUEUE = "order.pay.queue";
    /** 订单取消队列 — 恢复库存 */
    public static final String ORDER_CANCEL_QUEUE = "order.cancel.queue";

    public static final String ROUTING_KEY_CREATE = "order.create";
    public static final String ROUTING_KEY_PAY = "order.pay";
    public static final String ROUTING_KEY_CANCEL = "order.cancel";

    @Bean
    public TopicExchange orderExchange() {
        return new TopicExchange(ORDER_EXCHANGE, true, false);
    }

    @Bean
    public Queue orderCreateQueue() {
        return QueueBuilder.durable(ORDER_CREATE_QUEUE).build();
    }

    @Bean
    public Queue orderPayQueue() {
        return QueueBuilder.durable(ORDER_PAY_QUEUE).build();
    }

    @Bean
    public Queue orderCancelQueue() {
        return QueueBuilder.durable(ORDER_CANCEL_QUEUE).build();
    }

    @Bean
    public Binding orderCreateBinding() {
        return BindingBuilder.bind(orderCreateQueue())
                .to(orderExchange()).with(ROUTING_KEY_CREATE);
    }

    @Bean
    public Binding orderPayBinding() {
        return BindingBuilder.bind(orderPayQueue())
                .to(orderExchange()).with(ROUTING_KEY_PAY);
    }

    @Bean
    public Binding orderCancelBinding() {
        return BindingBuilder.bind(orderCancelQueue())
                .to(orderExchange()).with(ROUTING_KEY_CANCEL);
    }
}
