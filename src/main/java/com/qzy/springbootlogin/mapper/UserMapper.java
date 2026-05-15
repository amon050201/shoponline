package com.qzy.springbootlogin.mapper;

import com.qzy.springbootlogin.pojo.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 用户数据访问接口 - MyBatis Mapper层
 */
@Mapper
public interface UserMapper {
    
    /**
     * 根据用户名查询用户
     * @param username 用户名
     * @return 用户对象，不存在返回null
     */
    @Select("SELECT id, username, password_hash as passwordHash, email, phone, role_type as roleType, status, created_at as createdAt, updated_at as updatedAt FROM users WHERE username = #{username}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "username", column = "username"),
        @Result(property = "passwordHash", column = "passwordHash"),
        @Result(property = "email", column = "email"),
        @Result(property = "phone", column = "phone"),
        @Result(property = "roleType", column = "roleType"),
        @Result(property = "status", column = "status"),
        @Result(property = "createdAt", column = "createdAt"),
        @Result(property = "updatedAt", column = "updatedAt")
    })
    User findByUsername(@Param("username") String username);
    
    /**
     * 保存用户
     * @param user 用户对象
     * @return 是否保存成功
     */
    @Insert("INSERT INTO users (username, password_hash, email, phone, role_type, status) VALUES (#{username}, #{passwordHash}, #{email}, #{phone}, #{roleType}, #{status})")
    boolean save(User user);
    
    /**
     * 验证用户登录
     * @param username 用户名
     * @param password 密码
     * @return 验证通过返回用户对象，否则返回null
     */
    @Select("SELECT id, username, password_hash as passwordHash, email, phone, role_type as roleType, status, created_at as createdAt, updated_at as updatedAt FROM users WHERE username = #{username} AND password_hash = #{passwordHash}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "username", column = "username"),
        @Result(property = "passwordHash", column = "passwordHash"),
        @Result(property = "email", column = "email"),
        @Result(property = "phone", column = "phone"),
        @Result(property = "roleType", column = "roleType"),
        @Result(property = "status", column = "status"),
        @Result(property = "createdAt", column = "createdAt"),
        @Result(property = "updatedAt", column = "updatedAt")
    })
    User validateLogin(@Param("username") String username, @Param("passwordHash") String passwordHash);
    
    /**
     * 查询所有用户
     * @return 用户列表
     */
    @Select("SELECT id, username, password_hash as passwordHash, email, phone, role_type as roleType, status, created_at as createdAt, updated_at as updatedAt FROM users ORDER BY id DESC")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "username", column = "username"),
        @Result(property = "passwordHash", column = "passwordHash"),
        @Result(property = "email", column = "email"),
        @Result(property = "phone", column = "phone"),
        @Result(property = "roleType", column = "roleType"),
        @Result(property = "status", column = "status"),
        @Result(property = "createdAt", column = "createdAt"),
        @Result(property = "updatedAt", column = "updatedAt")
    })
    List<User> list();
    
    /**
     * 根据ID删除用户
     * @param id 用户ID
     * @return 是否删除成功
     */
    @Delete("DELETE FROM users WHERE id = #{id}")
    boolean deleteById(@Param("id") Long id);
    
    /**
     * 根据ID查询用户
     * @param id 用户ID
     * @return 用户对象
     */
    @Select("SELECT id, username, password_hash as passwordHash, email, phone, role_type as roleType, status, created_at as createdAt, updated_at as updatedAt FROM users WHERE id = #{id}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "username", column = "username"),
        @Result(property = "passwordHash", column = "passwordHash"),
        @Result(property = "email", column = "email"),
        @Result(property = "phone", column = "phone"),
        @Result(property = "roleType", column = "roleType"),
        @Result(property = "status", column = "status"),
        @Result(property = "createdAt", column = "createdAt"),
        @Result(property = "updatedAt", column = "updatedAt")
    })
    User findById(@Param("id") Long id);
    
    /**
     * 更新用户信息
     * @param user 用户对象
     * @return 是否更新成功
     */
    @Update("UPDATE users SET username = #{username}, password_hash = #{passwordHash}, email = #{email}, phone = #{phone}, role_type = #{roleType}, status = #{status} WHERE id = #{id}")
    boolean update(User user);
    
    /**
     * 更新用户角色
     * @param userId 用户ID
     * @param roleType 角色类型
     * @return 是否更新成功
     */
    @Update("UPDATE users SET role_type = #{roleType} WHERE id = #{userId}")
    boolean updateUserRole(@Param("userId") Long userId, @Param("roleType") Integer roleType);
}
