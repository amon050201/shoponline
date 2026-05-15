package com.qzy.springbootlogin.controller;

import com.qzy.springbootlogin.pojo.Result;
import com.qzy.springbootlogin.pojo.User;
import com.qzy.springbootlogin.service.UserService;
import com.qzy.springbootlogin.util.JwtUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

/**
 * 登录控制器 - Controller层
 * 职责：接收请求、调用Service层、响应结果
 */
@Controller
public class LoginController {
    
    @Autowired
    private UserService userService;
    
    @GetMapping({"/", "/index"})
    public String indexPage(HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");
        Integer roleType = (Integer) session.getAttribute("roleType");
        if (username != null) {
            model.addAttribute("username", username);
            model.addAttribute("roleType", roleType);
        }
        return "pages/index";
    }
    
    @GetMapping("/login")
    public String loginPage() {
        return "pages/login";
    }
    
    @GetMapping("/register")
    public String registerPage() {
        return "pages/register";
    }
    
    @PostMapping("/login")
    public String login(@RequestParam String username, 
                       @RequestParam String password,
                       @RequestParam String captcha,
                       HttpSession session,
                       Model model) {
        // 验证验证码
        String sessionCaptcha = (String) session.getAttribute("captcha_code");
        if (sessionCaptcha == null || sessionCaptcha.isEmpty()) {
            session.setAttribute("message", "验证码已过期，请刷新页面重试");
            session.setAttribute("messageType", "error");
            return "redirect:/login";
        }
        
        if (captcha == null || !captcha.trim().toLowerCase().equals(sessionCaptcha)) {
            session.setAttribute("message", "验证码错误");
            session.setAttribute("messageType", "error");
            return "redirect:/login";
        }
        
        // 清除已使用的验证码
        session.removeAttribute("captcha_code");
        
        // 调用Service层处理登录业务
        Result<User> result = userService.login(username, password);
        
        if (result.isSuccess()) {
            // 登录成功
            User user = result.getData();
            String token = JwtUtil.generateToken(user.getUsername());
            
            session.setAttribute("username", user.getUsername());
            session.setAttribute("userId", user.getId());  // 添加userId，供购物系统使用
            session.setAttribute("roleType", user.getRoleType());  // 保存用户角色
            session.setAttribute("token", token);
            session.setAttribute("message", result.getMessage());
            session.setAttribute("messageType", "success");

            return "redirect:/";
        } else {
            // 登录失败
            session.setAttribute("message", result.getMessage());
            session.setAttribute("messageType", "error");
            return "redirect:/login";
        }
    }
    
    @PostMapping("/register")
    public String register(@RequestParam String username,
                          @RequestParam String password,
                          @RequestParam String confirmPassword,
                          @RequestParam String captcha,
                          @RequestParam(defaultValue = "0") Integer roleType,
                          @RequestParam(required = false) String email,
                          @RequestParam(required = false) String phone,
                          HttpSession session) {
        // 验证验证码
        String sessionCaptcha = (String) session.getAttribute("captcha_code");
        if (sessionCaptcha == null || sessionCaptcha.isEmpty()) {
            session.setAttribute("message", "验证码已过期，请刷新页面重试");
            session.setAttribute("messageType", "error");
            return "redirect:/register";
        }
        
        if (captcha == null || !captcha.trim().toLowerCase().equals(sessionCaptcha)) {
            session.setAttribute("message", "验证码错误");
            session.setAttribute("messageType", "error");
            return "redirect:/register";
        }
        
        // 清除已使用的验证码
        session.removeAttribute("captcha_code");
        
        User user = new User();
        user.setUsername(username);
        user.setPasswordHash(password);
        user.setRoleType(roleType); // 设置角色类型
        user.setStatus(1); // 默认启用
        user.setEmail(email);
        user.setPhone(phone);
        
        // 调用Service层处理注册业务
        Result<Void> result = userService.register(user);
        
        if (result.isSuccess()) {
            session.setAttribute("message", result.getMessage());
            session.setAttribute("messageType", "success");
            return "redirect:/login";
        } else {
            session.setAttribute("message", result.getMessage());
            session.setAttribute("messageType", "error");
            return "redirect:/register";
        }
    }
    
    /**
     * 小程序登录接口 - JSON API
     * POST /api/auth/login
     * Body: { "username": "xxx", "password": "xxx" }
     */
    @PostMapping("/api/auth/login")
    @ResponseBody
    public Result<Map<String, Object>> apiLogin(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");

        if (username == null || password == null) {
            return Result.error("用户名和密码不能为空");
        }

        Result<User> result = userService.login(username, password);
        if (result.isSuccess()) {
            User user = result.getData();
            String token = JwtUtil.generateToken(user.getUsername(), user.getId(), user.getRoleType());
            Map<String, Object> data = new java.util.HashMap<>();
            data.put("token", token);
            data.put("username", user.getUsername());
            data.put("userId", user.getId());
            data.put("roleType", user.getRoleType());
            return Result.success("登录成功", data);
        }
        return Result.error(result.getMessage());
    }

    /**
     * 小程序注册接口 - JSON API
     * POST /api/auth/register
     * Body: { "username": "xxx", "password": "xxx", "roleType": 0 }
     */
    @PostMapping("/api/auth/register")
    @ResponseBody
    public Result<Void> apiRegister(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");
        String roleTypeStr = body.get("roleType");
        String email = body.get("email");
        String phone = body.get("phone");

        if (username == null || password == null) {
            return Result.error("用户名和密码不能为空");
        }

        User user = new User();
        user.setUsername(username);
        user.setPasswordHash(password);
        user.setRoleType(roleTypeStr != null ? Integer.parseInt(roleTypeStr) : 0);
        user.setStatus(1);
        if (email != null) user.setEmail(email);
        if (phone != null) user.setPhone(phone);

        return userService.register(user);
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
    
    /**
     * 用户列表页面
     */
    @GetMapping("/userlist")
    public String userList(HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "redirect:/login";
        }
        model.addAttribute("username", username);
        model.addAttribute("users", userService.list());
        return "pages/userlist";
    }
    
    /**
     * 添加用户页面
     */
    @GetMapping("/adduser")
    public String addUserPage(HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "redirect:/login";
        }
        model.addAttribute("username", username);
        return "pages/adduser";
    }
    
    /**
     * 添加用户处理
     */
    @PostMapping("/addUser")
    public String addUser(@RequestParam String username,
                         @RequestParam String password,
                         @RequestParam String confirmPassword,
                         @RequestParam(required = false) String email,
                         @RequestParam(required = false) String phone,
                         HttpSession session,
                         Model model) {
        String currentUser = (String) session.getAttribute("username");
        if (currentUser == null) {
            return "redirect:/login";
        }
        
        User user = new User();
        user.setUsername(username);
        user.setPasswordHash(password);
        user.setEmail(email);
        user.setPhone(phone);
        
        Result<Void> result = userService.addUser(user);
        
        if (result.isSuccess()) {
            model.addAttribute("success", result.getMessage());
            model.addAttribute("username", currentUser);
            return "pages/adduser";
        } else {
            model.addAttribute("error", result.getMessage());
            model.addAttribute("username", currentUser);
            model.addAttribute("formData", user);
            return "pages/adduser";
        }
    }
    
    /**
     * 删除用户
     */
    @PostMapping("/deleteUser")
    public String deleteUser(@RequestParam Long id, HttpSession session) {
        String currentUser = (String) session.getAttribute("username");
        if (currentUser == null) {
            return "redirect:/login";
        }
        
        Result<Void> result = userService.deleteUser(id);
        return "redirect:/userlist";
    }
    
    /**
     * 编辑用户页面
     */
    @GetMapping("/edituser")
    public String editUserPage(@RequestParam Long id, HttpSession session, Model model) {
        String currentUser = (String) session.getAttribute("username");
        if (currentUser == null) {
            return "redirect:/login";
        }
        
        User user = userService.getUserById(id);
        if (user == null) {
            return "redirect:/userlist";
        }
        
        model.addAttribute("username", currentUser);
        model.addAttribute("user", user);
        return "pages/edituser";
    }
    
    /**
     * 编辑用户处理
     */
    @PostMapping("/updateUser")
    public String updateUser(@RequestParam Long id,
                            @RequestParam String username,
                            @RequestParam(required = false) String password,
                            @RequestParam(required = false) String email,
                            @RequestParam(required = false) String phone,
                            HttpSession session,
                            Model model) {
        String currentUser = (String) session.getAttribute("username");
        if (currentUser == null) {
            return "redirect:/login";
        }
        
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setPasswordHash(password);
        user.setEmail(email);
        user.setPhone(phone);
        
        Result<Void> result = userService.updateUser(user);
        
        if (result.isSuccess()) {
            model.addAttribute("success", result.getMessage());
        } else {
            model.addAttribute("error", result.getMessage());
        }
        
        model.addAttribute("username", currentUser);
        model.addAttribute("user", user);
        return "pages/edituser";
    }
    
    /**
     * 系统测试页面
     */
    @GetMapping("/test")
    public String testPage(@RequestParam(required = false) Boolean runAll, HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "redirect:/login";
        }
        model.addAttribute("username", username);
        
        int successCount = 0;
        int errorCount = 0;
        int warningCount = 0;
        
        // 1. 基础环境测试
        java.util.List<java.util.Map<String, String>> envTests = new java.util.ArrayList<>();
        
        // Java版本测试
        String javaVersion = System.getProperty("java.version");
        envTests.add(createTestResult("Java 环境", javaVersion, "success", "Java版本: " + javaVersion));
        if (javaVersion.startsWith("21") || javaVersion.startsWith("17") || javaVersion.startsWith("11")) {
            successCount++;
        } else {
            warningCount++;
        }
        
        // 操作系统测试
        String osName = System.getProperty("os.name");
        envTests.add(createTestResult("操作系统", osName, "success", "当前运行在: " + osName));
        successCount++;
        
        // 内存测试
        long maxMemory = Runtime.getRuntime().maxMemory() / (1024 * 1024);
        long totalMemory = Runtime.getRuntime().totalMemory() / (1024 * 1024);
        long freeMemory = Runtime.getRuntime().freeMemory() / (1024 * 1024);
        envTests.add(createTestResult("内存状态", 
            String.valueOf(maxMemory), 
            "success", 
            String.format("最大: %dMB | 已分配: %dMB | 空闲: %dMB", maxMemory, totalMemory, freeMemory)));
        successCount++;
        
        model.addAttribute("envTests", envTests);
        
        // 2. 数据库测试
        java.util.List<java.util.Map<String, String>> dbTests = new java.util.ArrayList<>();
        
        try {
            // 数据库连接测试
            java.util.List<User> users = userService.list();
            dbTests.add(createTestResult("数据库连接", "成功", "success", "已连接 MySQL 数据库"));
            successCount++;
            
            // 用户数量测试
            int userCount = users.size();
            dbTests.add(createTestResult("用户数据", String.valueOf(userCount), "success", 
                String.format("数据库中共有 %d 个用户", userCount)));
            successCount++;
            
            // admin账号测试
            User admin = null;
            for (User u : users) {
                if ("admin".equals(u.getUsername())) {
                    admin = u;
                    break;
                }
            }
            if (admin != null) {
                dbTests.add(createTestResult("默认管理员", "存在", "success", "admin账号已初始化"));
                successCount++;
            } else {
                dbTests.add(createTestResult("默认管理员", "缺失", "warning", "admin账号未找到"));
                warningCount++;
            }
        } catch (Exception e) {
            dbTests.add(createTestResult("数据库连接", "失败", "error", e.getMessage()));
            errorCount++;
        }
        
        model.addAttribute("dbTests", dbTests);
        
        // 3. JWT 功能测试
        java.util.List<java.util.Map<String, String>> jwtTests = new java.util.ArrayList<>();
        
        try {
            // 生成 Token 测试
            String testToken = com.qzy.springbootlogin.util.JwtUtil.generateToken("testuser");
            if (testToken != null && !testToken.isEmpty()) {
                jwtTests.add(createTestResult("Token 生成", "成功", "success", "Token 已生成"));
                successCount++;
            } else {
                jwtTests.add(createTestResult("Token 生成", "失败", "error", "Token 生成为空"));
                errorCount++;
            }
            
            // Token 验证测试
            boolean isValid = com.qzy.springbootlogin.util.JwtUtil.validateToken(testToken);
            if (isValid) {
                jwtTests.add(createTestResult("Token 验证", "成功", "success", "Token 验证通过"));
                successCount++;
            } else {
                jwtTests.add(createTestResult("Token 验证", "失败", "error", "Token 验证失败"));
                errorCount++;
            }
            
            // 提取用户名测试
            String extractedUsername = com.qzy.springbootlogin.util.JwtUtil.getUsernameFromToken(testToken);
            if ("testuser".equals(extractedUsername)) {
                jwtTests.add(createTestResult("用户名提取", "成功", "success", "成功提取用户名: testuser"));
                successCount++;
            } else {
                jwtTests.add(createTestResult("用户名提取", "失败", "error", "用户名提取失败"));
                errorCount++;
            }
        } catch (Exception e) {
            jwtTests.add(createTestResult("JWT 功能", "异常", "error", e.getMessage()));
            errorCount++;
        }
        
        model.addAttribute("jwtTests", jwtTests);
        
        // 4. 用户功能测试
        java.util.List<java.util.Map<String, String>> userTests = new java.util.ArrayList<>();
        
        try {
            // 用户登录功能测试
            Result<User> loginResult = userService.login("admin", "123456");
            if (loginResult.isSuccess()) {
                userTests.add(createTestResult("用户登录", "成功", "success", "admin 登录功能正常"));
                successCount++;
            } else {
                userTests.add(createTestResult("用户登录", "失败", "error", loginResult.getMessage()));
                errorCount++;
            }
            
            // 错误登录测试
            Result<User> failedLogin = userService.login("admin", "wrongpassword");
            if (!failedLogin.isSuccess()) {
                userTests.add(createTestResult("错误拦截", "成功", "success", "错误密码被正确拦截"));
                successCount++;
            } else {
                userTests.add(createTestResult("错误拦截", "失败", "error", "错误密码未被拦截"));
                errorCount++;
            }
            
            // 用户注册校验测试
            String validationError = userService.validateRegistration("test", "123", "123");
            if (validationError != null) {
                userTests.add(createTestResult("注册校验", "成功", "success", "弱密码被正确拦截"));
                successCount++;
            } else {
                userTests.add(createTestResult("注册校验", "失败", "error", "弱密码未被拦截"));
                errorCount++;
            }
        } catch (Exception e) {
            userTests.add(createTestResult("用户功能", "异常", "error", e.getMessage()));
            errorCount++;
        }
        
        model.addAttribute("userTests", userTests);
        
        // 汇总统计
        model.addAttribute("successCount", successCount);
        model.addAttribute("errorCount", errorCount);
        model.addAttribute("warningCount", warningCount);
        model.addAttribute("totalCount", successCount + errorCount + warningCount);
        
        return "pages/test";
    }
    
    /**
     * 创建测试结果辅助方法
     */
    private java.util.Map<String, String> createTestResult(String name, String result, String status, String detail) {
        java.util.Map<String, String> testMap = new java.util.HashMap<>();
        testMap.put("name", name);
        testMap.put("result", result);
        testMap.put("status", status);
        testMap.put("statusText", "success".equals(status) ? "✅ 通过" : ("error".equals(status) ? "❌ 失败" : "⚠️ 警告"));
        testMap.put("detail", detail);
        return testMap;
    }
}
