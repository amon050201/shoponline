package com.qzy.springbootlogin.util;

import com.qzy.springbootlogin.mapper.OperationLogMapper;
import com.qzy.springbootlogin.pojo.OperationLog;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;

@Aspect
@Component
public class OperationLogAspect {

    @Autowired
    private OperationLogMapper logMapper;

    @Around("@annotation(adminOp)")
    public Object logOperation(ProceedingJoinPoint joinPoint, AdminOperation adminOp) throws Throwable {
        long start = System.currentTimeMillis();

        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attrs != null ? attrs.getRequest() : null;
        HttpSession session = request != null ? request.getSession(false) : null;

        OperationLog log = new OperationLog();
        log.setMethod(joinPoint.getSignature().toShortString());

        if (session != null) {
            Long userId = (Long) session.getAttribute("userId");
            String username = (String) session.getAttribute("username");
            log.setUserId(userId != null ? userId : 0L);
            log.setUsername(username != null ? username : "unknown");
        } else {
            log.setUserId(0L);
            log.setUsername("unknown");
        }

        log.setModule(adminOp.module());
        log.setOperation(adminOp.value().isEmpty() ? joinPoint.getSignature().getName() : adminOp.value());

        Object[] args = joinPoint.getArgs();
        String params = "";
        try {
            params = Arrays.toString(args);
            if (params.length() > 500) {
                params = params.substring(0, 500) + "...";
            }
        } catch (Exception e) {
            params = "[参数序列化失败]";
        }
        log.setParams(params);

        if (request != null) {
            String ip = request.getHeader("X-Forwarded-For");
            if (ip == null || ip.isEmpty()) {
                ip = request.getHeader("X-Real-IP");
            }
            if (ip == null || ip.isEmpty()) {
                ip = request.getRemoteAddr();
            }
            log.setIp(ip);
        }

        String resultMsg = "成功";
        try {
            Object result = joinPoint.proceed();
            log.setDuration(System.currentTimeMillis() - start);
            if (result != null && result.toString().length() <= 200) {
                resultMsg = result.toString();
            }
            log.setResult(resultMsg);
            logMapper.insert(log);
            return result;
        } catch (Throwable t) {
            log.setDuration(System.currentTimeMillis() - start);
            log.setResult("异常: " + t.getClass().getSimpleName() + " - " + t.getMessage());
            logMapper.insert(log);
            throw t;
        }
    }
}
