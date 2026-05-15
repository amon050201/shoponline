package com.qzy.springbootlogin.service.impl;

import com.qzy.springbootlogin.pojo.Result;
import com.qzy.springbootlogin.pojo.User;
import com.qzy.springbootlogin.mapper.UserMapper;
import com.qzy.springbootlogin.service.UserService;
import com.qzy.springbootlogin.util.JwtUtil;
import com.qzy.springbootlogin.util.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * 用户服务实现类 - Service层
 * 负责业务逻辑处理
 */
@Service
public class UserServiceImpl implements UserService {
    
    private static final int MIN_PASSWORD_LENGTH = 6;
    private static final int MAX_PASSWORD_LENGTH = 50;
    private static final int MIN_USERNAME_LENGTH = 3;
    private static final int MAX_USERNAME_LENGTH = 20;
    
    @Autowired
    private UserMapper userMapper;
    
    @Override
    public Result<User> login(String username, String password) {
        // 参数校验
        if (username == null || username.trim().isEmpty() ||
            password == null || password.trim().isEmpty()) {
            return Result.error("用户名和密码不能为空");
        }

        String trimmedUsername = username.trim();

        // 查找用户
        User user = userMapper.findByUsername(trimmedUsername);

        if (user != null && PasswordUtil.matches(password, user.getPasswordHash())) {
            // 生成JWT Token
            String token = JwtUtil.generateToken(trimmedUsername);

            // 返回完整的用户信息，用于设置session
            User safeUser = new User(user.getUsername(), null);
            safeUser.setId(user.getId());
            safeUser.setRoleType(user.getRoleType());
            safeUser.setEmail(user.getEmail());
            safeUser.setPhone(user.getPhone());

            return Result.success("登录成功", safeUser);
        } else if (user != null && password.equals(user.getPasswordHash())) {
            // 兼容旧版明文密码：自动升级为BCrypt
            user.setPasswordHash(PasswordUtil.hash(password));
            userMapper.update(user);

            String token = JwtUtil.generateToken(trimmedUsername);
            User safeUser = new User(user.getUsername(), null);
            safeUser.setId(user.getId());
            safeUser.setRoleType(user.getRoleType());
            safeUser.setEmail(user.getEmail());
            safeUser.setPhone(user.getPhone());
            return Result.success("登录成功", safeUser);
        } else {
            return Result.error("用户名或密码错误，请重新登录");
        }
    }
    
    @Override
    public Result<Void> register(User user) {
        if (user == null) {
            return Result.error("用户信息不能为空");
        }
        
        // 参数校验
        String validationError = validateRegistration(
            user.getUsername(), 
            user.getPasswordHash(), 
            user.getPasswordHash() // 这里假设confirmPassword已经在外层验证过
        );
        
        if (validationError != null) {
            return Result.error(validationError);
        }
        
        String trimmedUsername = user.getUsername().trim();
        user.setUsername(trimmedUsername);
        
        // 检查用户名是否已存在
        User existingUser = userMapper.findByUsername(trimmedUsername);
        if (existingUser != null) {
            return Result.error("用户名已存在");
        }

        // 对密码进行BCrypt哈希
        user.setPasswordHash(PasswordUtil.hash(user.getPasswordHash()));

        // 保存用户
        boolean saved = userMapper.save(user);

        if (saved) {
            System.out.println("用户注册成功: " + trimmedUsername);
            return Result.success("注册成功！请登录。");
        } else {
            return Result.error("注册失败，请稍后重试");
        }
    }
    
    @Override
    public String validateRegistration(String username, String password, String confirmPassword) {
        // 验证用户名
        if (username == null || username.trim().isEmpty()) {
            return "用户名不能为空";
        }
        
        String trimmedUsername = username.trim();
        if (trimmedUsername.length() < MIN_USERNAME_LENGTH || 
            trimmedUsername.length() > MAX_USERNAME_LENGTH) {
            return "用户名长度必须在" + MIN_USERNAME_LENGTH + "-" + MAX_USERNAME_LENGTH + "个字符之间";
        }
        
        // 验证密码
        if (password == null || password.length() < MIN_PASSWORD_LENGTH) {
            return "密码长度不能少于" + MIN_PASSWORD_LENGTH + "位";
        }
        
        if (password.length() > MAX_PASSWORD_LENGTH) {
            return "密码长度不能超过" + MAX_PASSWORD_LENGTH + "位";
        }
        
        // 验证确认密码
        if (!password.equals(confirmPassword)) {
            return "两次输入的密码不一致";
        }
        
        return null; // 验证通过
    }
    
    @Override
    public List<User> list() {
        return userMapper.list();
    }
    
    @Override
    public Result<Void> addUser(User user) {
        if (user == null) {
            return Result.error("用户信息不能为空");
        }
        
        // 参数校验
        String validationError = validateAddUser(
            user.getUsername(),
            user.getPasswordHash(),
            user.getPasswordHash(), // 假设在Controller层已经验证了confirmPassword
            user.getEmail(),
            user.getPhone()
        );
        
        if (validationError != null) {
            return Result.error(validationError);
        }
        
        String trimmedUsername = user.getUsername().trim();
        user.setUsername(trimmedUsername);
        
        // 检查用户名是否已存在
        User existingUser = userMapper.findByUsername(trimmedUsername);
        if (existingUser != null) {
            return Result.error("用户名已存在");
        }

        // 对密码进行BCrypt哈希
        user.setPasswordHash(PasswordUtil.hash(user.getPasswordHash()));

        // 保存用户
        boolean saved = userMapper.save(user);

        if (saved) {
            System.out.println("管理员添加用户成功: " + trimmedUsername);
            return Result.success("用户添加成功");
        } else {
            return Result.error("添加用户失败，请稍后重试");
        }
    }
    
    @Override
    public String validateAddUser(String username, String password, String confirmPassword, String email, String phone) {
        // 验证用户名
        if (username == null || username.trim().isEmpty()) {
            return "用户名不能为空";
        }
        
        String trimmedUsername = username.trim();
        if (trimmedUsername.length() < MIN_USERNAME_LENGTH || 
            trimmedUsername.length() > MAX_USERNAME_LENGTH) {
            return "用户名长度必须在" + MIN_USERNAME_LENGTH + "-" + MAX_USERNAME_LENGTH + "个字符之间";
        }
        
        // 验证密码
        if (password == null || password.length() < MIN_PASSWORD_LENGTH) {
            return "密码长度不能少于" + MIN_PASSWORD_LENGTH + "位";
        }
        
        if (password.length() > MAX_PASSWORD_LENGTH) {
            return "密码长度不能超过" + MAX_PASSWORD_LENGTH + "位";
        }
        
        // 验证确认密码
        if (!password.equals(confirmPassword)) {
            return "两次输入的密码不一致";
        }
        
        // 验证邮箱（如果提供）
        if (email != null && !email.trim().isEmpty()) {
            String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
            if (!email.trim().matches(emailRegex)) {
                return "邮箱格式不正确";
            }
        }
        
        // 验证手机号（如果提供）
        if (phone != null && !phone.trim().isEmpty()) {
            String phoneRegex = "^1[3-9]\\d{9}$";
            if (!phone.trim().matches(phoneRegex)) {
                return "手机号格式不正确（应为11位数字）";
            }
        }
        
        return null; // 验证通过
    }
    
    @Override
    public Result<Void> deleteUser(Long id) {
        if (id == null) {
            return Result.error("用户ID不能为空");
        }
        
        // 查询用户是否存在
        User user = userMapper.findById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        // 保护：不允许删除 admin 账号
        if ("admin".equals(user.getUsername())) {
            return Result.error("不能删除默认管理员账号");
        }
        
        boolean deleted = userMapper.deleteById(id);
        if (deleted) {
            System.out.println("管理员删除用户成功: ID = " + id);
            return Result.success("用户删除成功");
        } else {
            return Result.error("删除用户失败");
        }
    }
    
    @Override
    public Result<Void> updateUser(User user) {
        if (user == null || user.getId() == null) {
            return Result.error("用户信息不能为空");
        }
        
        // 查询原用户
        User existingUser = userMapper.findById(user.getId());
        if (existingUser == null) {
            return Result.error("用户不存在");
        }
        
        // 验证用户名
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            return Result.error("用户名不能为空");
        }
        
        String trimmedUsername = user.getUsername().trim();
        if (trimmedUsername.length() < MIN_USERNAME_LENGTH || 
            trimmedUsername.length() > MAX_USERNAME_LENGTH) {
            return Result.error("用户名长度必须在" + MIN_USERNAME_LENGTH + "-" + MAX_USERNAME_LENGTH + "个字符之间");
        }
        
        // 如果用户名被修改，检查新用户名是否已存在
        if (!trimmedUsername.equals(existingUser.getUsername())) {
            User duplicateUser = userMapper.findByUsername(trimmedUsername);
            if (duplicateUser != null) {
                return Result.error("用户名已存在");
            }
        }
        
        // 如果修改了密码，验证密码强度并哈希
        if (user.getPasswordHash() != null && !user.getPasswordHash().isEmpty()) {
            if (user.getPasswordHash().length() < MIN_PASSWORD_LENGTH) {
                return Result.error("密码长度不能少于" + MIN_PASSWORD_LENGTH + "位");
            }
            if (user.getPasswordHash().length() > MAX_PASSWORD_LENGTH) {
                return Result.error("密码长度不能超过" + MAX_PASSWORD_LENGTH + "位");
            }
            user.setPasswordHash(PasswordUtil.hash(user.getPasswordHash()));
        } else {
            // 未修改密码，保留原密码
            user.setPasswordHash(existingUser.getPasswordHash());
        }
        
        // 验证邮箱
        if (user.getEmail() != null && !user.getEmail().trim().isEmpty()) {
            String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
            if (!user.getEmail().trim().matches(emailRegex)) {
                return Result.error("邮箱格式不正确");
            }
        }
        
        // 验证手机号
        if (user.getPhone() != null && !user.getPhone().trim().isEmpty()) {
            String phoneRegex = "^1[3-9]\\d{9}$";
            if (!user.getPhone().trim().matches(phoneRegex)) {
                return Result.error("手机号格式不正确（应为11位数字）");
            }
        }
        
        // 更新用户
        boolean updated = userMapper.update(user);
        if (updated) {
            System.out.println("管理员更新用户成功: ID = " + user.getId());
            return Result.success("用户更新成功");
        } else {
            return Result.error("更新用户失败");
        }
    }
    
    @Override
    public User getUserById(Long id) {
        if (id == null) {
            return null;
        }
        return userMapper.findById(id);
    }
    
    @Override
    public boolean updateUserRole(Long userId, Integer roleType) {
        if (userId == null || roleType == null) {
            return false;
        }
        return userMapper.updateUserRole(userId, roleType);
    }
}
