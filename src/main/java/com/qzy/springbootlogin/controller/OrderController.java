package com.qzy.springbootlogin.controller;

import com.qzy.springbootlogin.pojo.Cart;
import com.qzy.springbootlogin.pojo.Order;
import com.qzy.springbootlogin.pojo.OrderItem;
import com.qzy.springbootlogin.pojo.Result;
import com.qzy.springbootlogin.service.CartService;
import com.qzy.springbootlogin.service.OrderService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 订单控制器
 */
@Controller
@RequestMapping("/order")
public class OrderController {
    
    @Autowired
    private OrderService orderService;
    
    @Autowired
    private CartService cartService;
    
    /**
     * 订单列表页面
     */
    @GetMapping("/list")
    public String list(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }
        
        List<Order> orders = orderService.findByUserId(userId.intValue());
        model.addAttribute("orders", orders);
        return "pages/orderlist";
    }
    
    /**
     * 订单详情页面
     */
    @GetMapping("/detail/{id}")
    public String detail(@PathVariable Integer id, Model model) {
        Order order = orderService.findById(id);
        model.addAttribute("order", order);
        return "pages/orderdetail";
    }
    
    /**
     * 确认订单页面（从购物车结算）
     */
    @PostMapping("/confirm")
    public String confirm(@RequestParam(defaultValue = "[]") String cartIdsJson,
                          @RequestBody(required = false) List<Integer> requestBodyCartIds,
                          HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        List<Integer> cartIds = requestBodyCartIds;
        if (cartIds == null && cartIdsJson != null && !cartIdsJson.isEmpty()) {
            try {
                com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                cartIds = mapper.readValue(cartIdsJson,
                    new com.fasterxml.jackson.core.type.TypeReference<List<Integer>>() {});
            } catch (Exception e) { /* ignore */ }
        }
        if (cartIds == null) cartIds = new ArrayList<>();

        List<Cart> carts = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (Integer cartId : cartIds) {
            Cart cart = cartService.getCartById(cartId);
            if (cart != null && cart.getUserId().equals(userId.intValue())) {
                carts.add(cart);
                totalAmount = totalAmount.add(
                    cart.getProduct().getPrice().multiply(BigDecimal.valueOf(cart.getQuantity()))
                );
            }
        }

        model.addAttribute("carts", carts);
        model.addAttribute("totalAmount", totalAmount);
        return "pages/orderconfirm";
    }
    
    /**
     * 创建订单
     */
    @PostMapping("/create")
    @ResponseBody
    public Result create(@RequestBody OrderRequest request, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return Result.error("请先登录");
        }
        
        try {
            // 创建订单
            Order order = new Order();
            order.setUserId(userId.intValue());
            order.setTotalAmount(request.getTotalAmount());
            order.setActualAmount(request.getActualAmount());
            order.setShippingFee(request.getShippingFee() != null ? request.getShippingFee() : BigDecimal.ZERO);
            order.setDiscountAmount(request.getDiscountAmount() != null ? request.getDiscountAmount() : BigDecimal.ZERO);
            order.setShippingAddress(request.getShippingAddress());
            order.setReceiverName(request.getReceiverName());
            order.setReceiverPhone(request.getReceiverPhone());
            order.setRemark(request.getRemark());
            
            // 创建订单项
            List<OrderItem> items = new ArrayList<>();
            for (OrderItemRequest itemReq : request.getItems()) {
                OrderItem item = new OrderItem();
                item.setProductId(itemReq.getProductId());
                item.setProductName(itemReq.getProductName());
                item.setProductImage(itemReq.getProductImage());
                item.setPrice(itemReq.getPrice());
                item.setQuantity(itemReq.getQuantity());
                items.add(item);
            }
            
            Order createdOrder = orderService.createOrder(order, items);
            
            // 清空购物车中已购买的商品
            if (request.getCartIds() != null) {
                for (Integer cartId : request.getCartIds()) {
                    cartService.removeFromCart(cartId);
                }
            }
            
            return Result.success("订单创建成功", createdOrder);
        } catch (Exception e) {
            return Result.error("订单创建失败：" + e.getMessage());
        }
    }
    
    /**
     * 支付订单
     */
    @PostMapping("/pay/{orderId}")
    @ResponseBody
    public Result pay(@PathVariable Integer orderId, @RequestParam String paymentMethod) {
        try {
            boolean success = orderService.payOrder(orderId, paymentMethod);
            if (success) {
                return Result.success("支付成功");
            } else {
                return Result.error("支付失败");
            }
        } catch (Exception e) {
            return Result.error("支付失败：" + e.getMessage());
        }
    }
    
    /**
     * 取消订单
     */
    @PostMapping("/cancel/{orderId}")
    @ResponseBody
    public Result cancel(@PathVariable Integer orderId) {
        try {
            int result = orderService.updateOrderStatus(orderId, 4); // 4-已取消
            if (result > 0) {
                return Result.success("订单已取消");
            } else {
                return Result.error("取消失败");
            }
        } catch (Exception e) {
            return Result.error("取消失败：" + e.getMessage());
        }
    }
    
    /**
     * 发货
     */
    @PostMapping("/ship/{orderId}")
    @ResponseBody
    public Result ship(@PathVariable Integer orderId,
                       @RequestParam String shippingCompany,
                       @RequestParam String trackingNumber,
                       HttpSession session) {
        Integer roleType = (Integer) session.getAttribute("roleType");
        if (roleType == null || roleType < 1) {
            return Result.error("需要商家或管理员权限");
        }
        try {
            boolean success = orderService.shipOrder(orderId, shippingCompany, trackingNumber);
            if (success) {
                return Result.success("发货成功");
            } else {
                return Result.error("发货失败，订单状态不正确");
            }
        } catch (Exception e) {
            return Result.error("发货失败：" + e.getMessage());
        }
    }

    /**
     * 确认收货
     */
    @PostMapping("/receive/{orderId}")
    @ResponseBody
    public Result receive(@PathVariable Integer orderId, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return Result.error("请先登录");
        }
        try {
            boolean success = orderService.confirmReceived(orderId);
            if (success) {
                return Result.success("已确认收货");
            } else {
                return Result.error("确认收货失败，订单状态不正确");
            }
        } catch (Exception e) {
            return Result.error("确认收货失败：" + e.getMessage());
        }
    }

    // 内部类：订单请求
    static class OrderRequest {
        private BigDecimal totalAmount;
        private BigDecimal actualAmount;
        private BigDecimal shippingFee;
        private BigDecimal discountAmount;
        private String shippingAddress;
        private String receiverName;
        private String receiverPhone;
        private String remark;
        private List<OrderItemRequest> items;
        private List<Integer> cartIds;

        public BigDecimal getTotalAmount() { return totalAmount; }
        public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
        public BigDecimal getActualAmount() { return actualAmount; }
        public void setActualAmount(BigDecimal actualAmount) { this.actualAmount = actualAmount; }
        public BigDecimal getShippingFee() { return shippingFee; }
        public void setShippingFee(BigDecimal shippingFee) { this.shippingFee = shippingFee; }
        public BigDecimal getDiscountAmount() { return discountAmount; }
        public void setDiscountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; }
        public String getShippingAddress() { return shippingAddress; }
        public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }
        public String getReceiverName() { return receiverName; }
        public void setReceiverName(String receiverName) { this.receiverName = receiverName; }
        public String getReceiverPhone() { return receiverPhone; }
        public void setReceiverPhone(String receiverPhone) { this.receiverPhone = receiverPhone; }
        public String getRemark() { return remark; }
        public void setRemark(String remark) { this.remark = remark; }
        public List<OrderItemRequest> getItems() { return items; }
        public void setItems(List<OrderItemRequest> items) { this.items = items; }
        public List<Integer> getCartIds() { return cartIds; }
        public void setCartIds(List<Integer> cartIds) { this.cartIds = cartIds; }
    }
    
    // 内部类：订单项请求
    static class OrderItemRequest {
        private Integer productId;
        private String productName;
        private String productImage;
        private BigDecimal price;
        private Integer quantity;

        public Integer getProductId() { return productId; }
        public void setProductId(Integer productId) { this.productId = productId; }
        public String getProductName() { return productName; }
        public void setProductName(String productName) { this.productName = productName; }
        public String getProductImage() { return productImage; }
        public void setProductImage(String productImage) { this.productImage = productImage; }
        public BigDecimal getPrice() { return price; }
        public void setPrice(BigDecimal price) { this.price = price; }
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
    }
}
