package com.qzy.springbootlogin.util;

import jakarta.servlet.*;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class EncodingFilter implements Filter {
    private static final String UTF8 = "UTF-8";
    private static final String TEXT_HTML_UTF8 = "text/html; charset=UTF-8";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
            throws IOException, ServletException {
        request.setCharacterEncoding(UTF8);
        response.setCharacterEncoding(UTF8);

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}
