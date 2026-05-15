package com.qzy.springbootlogin.mapper;

import com.qzy.springbootlogin.pojo.Cart;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 购物车Mapper接口
 */
@Mapper
public interface CartMapper {
    
    /**
     * 查询用户购物车
     */
    List<Cart> findByUserId(Integer userId);
    
    /**
     * 根据ID查询购物车项
     */
    Cart findById(Integer id);
    
    /**
     * 查询用户购物车中指定商品
     */
    Cart findByUserIdAndProductId(@Param("userId") Integer userId, @Param("productId") Integer productId);
    
    /**
     * 添加到购物车
     */
    int insert(Cart cart);
    
    /**
     * 更新购物车项
     */
    int update(Cart cart);
    
    /**
     * 更新数量
     */
    int updateQuantity(@Param("id") Integer id, @Param("quantity") Integer quantity);
    
    /**
     * 更新选中状态
     */
    int updateSelected(@Param("id") Integer id, @Param("selected") Integer selected);
    
    /**
     * 删除购物车项
     */
    int delete(Integer id);
    
    /**
     * 删除用户购物车中指定商品
     */
    int deleteByUserIdAndProductId(@Param("userId") Integer userId, @Param("productId") Integer productId);
    
    /**
     * 清空用户购物车
     */
    int clearByUserId(Integer userId);
}
