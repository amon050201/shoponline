package com.qzy.springbootlogin.config;

import com.qzy.springbootlogin.util.RoleInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web配置类 - 注册拦截器
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Autowired
    private RoleInterceptor roleInterceptor;
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册角色权限拦截器
        registry.addInterceptor(roleInterceptor)
                .addPathPatterns("/admin/**", "/merchant/**")  // 拦截管理员和商家路径
                .excludePathPatterns("/login", "/register", "/captcha", "/css/**", "/js/**", "/images/**",
                        "/api/payment/callback");
    }
}
