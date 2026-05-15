package com.qzy.springbootlogin.service.impl;

import com.qzy.springbootlogin.mapper.CartMapper;
import com.qzy.springbootlogin.pojo.Cart;
import com.qzy.springbootlogin.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 购物车服务实现类
 */
@Service
public class CartServiceImpl implements CartService {
    
    @Autowired
    private CartMapper cartMapper;
    
    @Override
    public List<Cart> getCartByUserId(Integer userId) {
        return cartMapper.findByUserId(userId);
    }
    
    @Override
    public Cart getCartById(Integer id) {
        return cartMapper.findById(id);
    }
    
    @Override
    public int addToCart(Cart cart) {
        // 检查是否已存在该商品
        Cart existCart = cartMapper.findByUserIdAndProductId(cart.getUserId(), cart.getProductId());
        
        if (existCart != null) {
            // 如果已存在，更新数量
            int newQuantity = existCart.getQuantity() + cart.getQuantity();
            return cartMapper.updateQuantity(existCart.getId(), newQuantity);
        } else {
            // 如果不存在，新增
            if (cart.getSelected() == null) {
                cart.setSelected(1); // 默认选中
            }
            return cartMapper.insert(cart);
        }
    }
    
    @Override
    public int updateQuantity(Integer id, Integer quantity) {
        if (quantity <= 0) {
            return cartMapper.delete(id);
        }
        return cartMapper.updateQuantity(id, quantity);
    }
    
    @Override
    public int updateSelected(Integer id, Integer selected) {
        return cartMapper.updateSelected(id, selected);
    }
    
    @Override
    public int removeFromCart(Integer id) {
        return cartMapper.delete(id);
    }
    
    @Override
    public int clearCart(Integer userId) {
        return cartMapper.clearByUserId(userId);
    }
}
