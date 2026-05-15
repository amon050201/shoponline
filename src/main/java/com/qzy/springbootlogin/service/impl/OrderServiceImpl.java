package com.qzy.springbootlogin.service.impl;

import com.qzy.springbootlogin.ai.service.MessageProducer;
import com.qzy.springbootlogin.mapper.OrderMapper;
import com.qzy.springbootlogin.mapper.ProductMapper;
import com.qzy.springbootlogin.pojo.Order;
import com.qzy.springbootlogin.pojo.OrderItem;
import com.qzy.springbootlogin.pojo.Product;
import com.qzy.springbootlogin.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private MessageProducer messageProducer;

    @Override
    public List<Order> findAll() {
        return orderMapper.findAll();
    }

    @Override
    public List<Order> findByUserId(Integer userId) {
        return orderMapper.findByUserId(userId);
    }

    @Override
    public Order findByOrderNo(String orderNo) {
        return orderMapper.findByOrderNo(orderNo);
    }

    @Override
    public Order findById(Integer id) {
        Order order = orderMapper.findById(id);
        if (order != null) {
            List<OrderItem> items = orderMapper.findItemsByOrderId(id);
            order.setOrderItems(items);
        }
        return order;
    }

    @Override
    @Transactional
    public Order createOrder(Order order, List<OrderItem> items) {
        String orderNo = generateOrderNo();
        order.setOrderNo(orderNo);
        order.setStatus(0);
        order.setCreatedTime(LocalDateTime.now());

        orderMapper.insert(order);

        for (OrderItem item : items) {
            item.setOrderId(order.getId());
            item.setTotalPrice(item.getPrice().multiply(java.math.BigDecimal.valueOf(item.getQuantity())));
            orderMapper.insertOrderItem(item);

            Product product = productMapper.findById(item.getProductId());
            if (product != null && product.getStock() < item.getQuantity()) {
                throw new RuntimeException("商品 " + item.getProductName() + " 库存不足");
            }
        }

        messageProducer.sendOrderCreate(order.getId());

        return order;
    }

    @Override
    public int updateOrderStatus(Integer id, Integer status) {
        int result = orderMapper.updateStatus(id, status);
        if (result > 0 && status == 4) {
            messageProducer.sendOrderCancel(id);
        }
        return result;
    }

    @Override
    public int deleteOrder(Integer id) {
        return orderMapper.delete(id);
    }

    @Override
    @Transactional
    public boolean shipOrder(Integer orderId, String shippingCompany, String trackingNumber) {
        return orderMapper.shipOrder(orderId, shippingCompany, trackingNumber) > 0;
    }

    @Override
    @Transactional
    public boolean confirmReceived(Integer orderId) {
        return orderMapper.confirmReceived(orderId) > 0;
    }

    @Override
    @Transactional
    public boolean payOrder(Integer orderId, String paymentMethod) {
        Order order = orderMapper.findById(orderId);
        if (order == null) {
            return false;
        }

        order.setStatus(1);
        order.setPaymentMethod(paymentMethod);
        order.setPaymentTime(LocalDateTime.now());

        int result = orderMapper.update(order);

        if (result > 0) {
            messageProducer.sendOrderPay(orderId);
        }

        return result > 0;
    }

    @Override
    @Transactional
    public boolean payOrder(String orderNo, String transactionId) {
        Order order = orderMapper.findByOrderNo(orderNo);
        if (order == null || order.getStatus() != 0) {
            return false;
        }
        order.setStatus(1);
        order.setTransactionId(transactionId);
        order.setPaymentTime(LocalDateTime.now());
        int result = orderMapper.update(order);
        if (result > 0) {
            messageProducer.sendOrderPay(order.getId());
        }
        return result > 0;
    }

    @Override
    public boolean updatePaymentInfo(Integer orderId, String transactionId, String paymentMethod) {
        Order order = orderMapper.findById(orderId);
        if (order == null) return false;
        order.setTransactionId(transactionId);
        order.setPaymentMethod(paymentMethod);
        return orderMapper.update(order) > 0;
    }

    private String generateOrderNo() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String dateTimeStr = LocalDateTime.now().format(formatter);
        Random random = new Random();
        int randomNum = random.nextInt(9000) + 1000;
        return dateTimeStr + randomNum;
    }
}
