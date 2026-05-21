package com.qzy.springbootlogin.service;

import com.qzy.springbootlogin.pojo.Order;
import com.qzy.springbootlogin.pojo.OrderItem;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@DisplayName("OrderService")
class OrderServiceTest {

    private static final int TEST_USER = 1;
    private static final int PROD_A = 1;
    private static final int PROD_B = 7;

    @Autowired
    private OrderService orderService;

    private Order buildOrder() {
        Order o = new Order();
        o.setUserId(TEST_USER);
        o.setTotalAmount(new BigDecimal("8999.00"));
        o.setActualAmount(new BigDecimal("8999.00"));
        o.setShippingFee(BigDecimal.ZERO);
        o.setDiscountAmount(BigDecimal.ZERO);
        o.setShippingAddress("123 Test Street");
        o.setReceiverName("Tester");
        o.setReceiverPhone("13800138000");
        return o;
    }

    private List<OrderItem> buildItems() {
        List<OrderItem> items = new ArrayList<>();
        OrderItem item = new OrderItem();
        item.setProductId(PROD_A);
        item.setProductName("iPhone 15 Pro");
        item.setPrice(new BigDecimal("8999.00"));
        item.setQuantity(1);
        items.add(item);
        return items;
    }

    @Nested
    @DisplayName("createOrder")
    class Create {

        @Test
        @DisplayName("creates order with order number and items")
        void createsSuccessfully() {
            Order created = orderService.createOrder(buildOrder(), buildItems());
            assertNotNull(created.getId());
            assertNotNull(created.getOrderNo());
            assertEquals(0, (int) created.getStatus());

            Order found = orderService.findById(created.getId());
            assertNotNull(found.getOrderItems());
            assertFalse(found.getOrderItems().isEmpty());
        }

        @Test
        @DisplayName("throws when stock insufficient")
        void failsOnInsufficientStock() {
            OrderItem item = new OrderItem();
            item.setProductId(PROD_A);
            item.setProductName("iPhone 15 Pro");
            item.setPrice(new BigDecimal("8999.00"));
            item.setQuantity(99999);
            List<OrderItem> items = new ArrayList<>();
            items.add(item);

            assertThrows(RuntimeException.class,
                    () -> orderService.createOrder(buildOrder(), items));
        }

        @Test
        @DisplayName("generates unique order numbers")
        void uniqueOrderNumbers() {
            Order o1 = orderService.createOrder(buildOrder(), buildItems());
            Order o2 = orderService.createOrder(buildOrder(), buildItems());
            assertNotEquals(o1.getOrderNo(), o2.getOrderNo());
        }
    }

    @Nested
    @DisplayName("query")
    class Query {

        @Test
        @DisplayName("findById returns order with items")
        void findById() {
            Order created = orderService.createOrder(buildOrder(), buildItems());
            Order found = orderService.findById(created.getId());
            assertEquals(created.getOrderNo(), found.getOrderNo());
        }

        @Test
        @DisplayName("findByUserId returns user orders")
        void findByUserId() {
            orderService.createOrder(buildOrder(), buildItems());
            assertFalse(orderService.findByUserId(TEST_USER).isEmpty());
        }

        @Test
        @DisplayName("findByOrderNo returns correct order")
        void findByOrderNo() {
            Order created = orderService.createOrder(buildOrder(), buildItems());
            Order found = orderService.findByOrderNo(created.getOrderNo());
            assertNotNull(found);
            assertEquals(TEST_USER, (int) found.getUserId());
        }
    }

    @Nested
    @DisplayName("payOrder")
    class Pay {

        @Test
        @DisplayName("payOrder by id sets status=1")
        void payById() {
            Order created = orderService.createOrder(buildOrder(), buildItems());
            assertTrue(orderService.payOrder(created.getId(), "alipay"));

            Order paid = orderService.findById(created.getId());
            assertEquals(1, (int) paid.getStatus());
            assertEquals("alipay", paid.getPaymentMethod());
            assertNotNull(paid.getPaymentTime());
        }

        @Test
        @DisplayName("payOrder by orderNo sets transactionId")
        void payByOrderNo() {
            Order created = orderService.createOrder(buildOrder(), buildItems());
            assertTrue(orderService.payOrder(created.getOrderNo(), "TXN_TEST_001"));

            Order paid = orderService.findById(created.getId());
            assertEquals(1, (int) paid.getStatus());
            assertEquals("TXN_TEST_001", paid.getTransactionId());
        }

        @Test
        @DisplayName("payOrder returns false for nonexistent")
        void payNonexistent() {
            assertFalse(orderService.payOrder(99999, "alipay"));
            assertFalse(orderService.payOrder("NO_SUCH_ORDER", "TXN_001"));
        }
    }

    @Nested
    @DisplayName("status transitions")
    class StatusTransitions {

        @Test
        @DisplayName("update order status")
        void updateStatus() {
            Order created = orderService.createOrder(buildOrder(), buildItems());
            assertEquals(1, orderService.updateOrderStatus(created.getId(), 2));
            assertEquals(2, (int) orderService.findById(created.getId()).getStatus());
        }

        @Test
        @DisplayName("ship order (requires paid status=1)")
        void ship() {
            Order created = orderService.createOrder(buildOrder(), buildItems());
            orderService.payOrder(created.getId(), "alipay");
            assertTrue(orderService.shipOrder(created.getId(), "SF Express", "SF1234567890"));
        }

        @Test
        @DisplayName("confirm received (requires shipped status=2)")
        void confirmReceived() {
            Order created = orderService.createOrder(buildOrder(), buildItems());
            orderService.payOrder(created.getId(), "alipay");
            orderService.shipOrder(created.getId(), "SF Express", "SF1234567890");
            assertTrue(orderService.confirmReceived(created.getId()));
        }
    }

    @Nested
    @DisplayName("delete")
    class Delete {

        @Test
        @DisplayName("delete order throws when items exist")
        void deleteOrderWithItemsThrows() {
            Order created = orderService.createOrder(buildOrder(), buildItems());
            assertThrows(Exception.class,
                    () -> orderService.deleteOrder(created.getId()));
        }
    }
}
