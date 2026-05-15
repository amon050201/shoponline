package com.qzy.springbootlogin.service;

import com.qzy.springbootlogin.pojo.Result;
import com.qzy.springbootlogin.pojo.User;
import java.util.List;

/**
 * 用户服务接口 - Service层
 */
public interface UserService {
    
    /**
     * 用户登录
     * @param username 用户名
     * @param password 密码
     * @return 登录结果，包含token等信息
     */
    Result<User> login(String username, String password);
    
    /**
     * 用户注册
     * @param user 用户对象
     * @return 注册结果
     */
    Result<Void> register(User user);
    
    /**
     * 验证注册信息
     * @param username 用户名
     * @param password 密码
     * @param confirmPassword 确认密码
     * @return 验证结果，成功返回null，失败返回错误信息
     */
    String validateRegistration(String username, String password, String confirmPassword);
    
    /**
     * 获取所有用户列表
     * @return 用户列表
     */
    List<User> list();
    
    /**
     * 添加新用户（后台管理）
     * @param user 用户对象
     * @return 添加结果
     */
    Result<Void> addUser(User user);
    
    /**
     * 验证添加用户信息
     * @param username 用户名
     * @param password 密码
     * @param confirmPassword 确认密码
     * @param email 邮箱
     * @param phone 手机号
     * @return 验证结果，成功返回null，失败返回错误信息
     */
    String validateAddUser(String username, String password, String confirmPassword, String email, String phone);
    
    /**
     * 删除用户
     * @param id 用户ID
     * @return 删除结果
     */
    Result<Void> deleteUser(Long id);
    
    /**
     * 更新用户信息
     * @param user 用户对象
     * @return 更新结果
     */
    Result<Void> updateUser(User user);
    
    /**
     * 根据ID获取用户
     * @param id 用户ID
     * @return 用户对象
     */
    User getUserById(Long id);
    
    /**
     * 更新用户角色
     * @param userId 用户ID
     * @param roleType 角色类型
     * @return 是否更新成功
     */
    boolean updateUserRole(Long userId, Integer roleType);
}
