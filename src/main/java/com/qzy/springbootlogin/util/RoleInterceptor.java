package com.qzy.springbootlogin.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 角色权限拦截器
 */
@Component
public class RoleInterceptor implements HandlerInterceptor {
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        
        // 获取当前用户ID和角色
        Long userId = (Long) session.getAttribute("userId");
        Integer roleType = (Integer) session.getAttribute("roleType");
        
        if (userId == null) {
            // 未登录，重定向到登录页
            response.sendRedirect("/login");
            return false;
        }
        
        // 获取请求路径
        String uri = request.getRequestURI();
        
        // 管理员专属路径
        if (uri.startsWith("/admin/")) {
            if (roleType == null || roleType != 2) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "需要管理员权限");
                return false;
            }
        }
        
        // 商家专属路径
        if (uri.startsWith("/merchant/")) {
            if (roleType == null || (roleType != 1 && roleType != 2)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "需要商家权限");
                return false;
            }
        }
        
        return true;
    }
}
