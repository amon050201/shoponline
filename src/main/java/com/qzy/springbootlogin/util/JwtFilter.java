package com.qzy.springbootlogin.util;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
public class JwtFilter implements Filter {

    private static final List<String> API_PATHS = Arrays.asList("/api/", "/admin/api/", "/exchange/", "/order/", "/address/");

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String requestURI = httpRequest.getRequestURI();

        // 公开路径直接放行
        if (isPublicPath(requestURI)) {
            chain.doFilter(request, response);
            return;
        }

        // 对API/订单/交换路径：尝试从Bearer token恢复用户身份
        if (isApiOrDataPath(requestURI)) {
            String authHeader = httpRequest.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                if (JwtUtil.validateToken(token)) {
                    HttpSession session = httpRequest.getSession(true);
                    if (session.getAttribute("token") == null) {
                        session.setAttribute("token", token);
                        session.setAttribute("username", JwtUtil.getUsernameFromToken(token));
                        session.setAttribute("userId", JwtUtil.getUserIdFromToken(token));
                        session.setAttribute("roleType", JwtUtil.getRoleTypeFromToken(token));
                    }
                    chain.doFilter(request, response);
                    return;
                }
            }
            // 即使没有token也放行，由控制器自行处理
            chain.doFilter(request, response);
            return;
        }

        // 检查是否已通过session认证（web页面）
        if (isSessionAuthenticated(httpRequest)) {
            chain.doFilter(request, response);
            return;
        }

        // 重定向到登录页
        httpResponse.sendRedirect("/login");
    }

    @Override
    public void destroy() {
    }

    private boolean isPublicPath(String uri) {
        return uri.equals("/") ||
               uri.equals("/index") ||
               uri.startsWith("/login") ||
               uri.startsWith("/register") ||
               uri.startsWith("/captcha") ||
               uri.startsWith("/css/") ||
               uri.startsWith("/js/") ||
               uri.startsWith("/images/");
    }

    private boolean isApiOrDataPath(String uri) {
        for (String prefix : API_PATHS) {
            if (uri.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }

    private boolean isSessionAuthenticated(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }
        String token = (String) session.getAttribute("token");
        if (token == null) {
            return false;
        }
        if (!JwtUtil.validateToken(token)) {
            session.invalidate();
            return false;
        }
        return true;
    }
}
