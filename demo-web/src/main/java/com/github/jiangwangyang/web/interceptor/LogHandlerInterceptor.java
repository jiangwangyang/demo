package com.github.jiangwangyang.web.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 日志拦截器 记录请求日志
 */
@Slf4j
public class LogHandlerInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
        log.info("收到请求 {} {}", request.getMethod(), request.getRequestURI());
        return true;
    }
}
