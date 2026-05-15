package com.qzy.springbootlogin.service;

import com.qzy.springbootlogin.pojo.Cart;

import java.util.List;

/**
 * 购物车服务接口
 */
public interface CartService {
    
    /**
     * 查询用户购物车
     */
    List<Cart> getCartByUserId(Integer userId);
    
    /**
     * 根据ID查询购物车项
     */
    Cart getCartById(Integer id);
    
    /**
     * 添加到购物车
     */
    int addToCart(Cart cart);
    
    /**
     * 更新购物车项数量
     */
    int updateQuantity(Integer id, Integer quantity);
    
    /**
     * 更新选中状态
     */
    int updateSelected(Integer id, Integer selected);
    
    /**
     * 删除购物车项
     */
    int removeFromCart(Integer id);
    
    /**
     * 清空用户购物车
     */
    int clearCart(Integer userId);
}
