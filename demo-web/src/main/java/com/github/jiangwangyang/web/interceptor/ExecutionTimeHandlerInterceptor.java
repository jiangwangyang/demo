package com.github.jiangwangyang.web.interceptor;

import jakarta.annotation.Nonnull;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 接口执行时间拦截器 记录执行时间过长的接口
 */
@Slf4j
public class ExecutionTimeHandlerInterceptor implements HandlerInterceptor {

    private static final int EXECUTION_TIMEOUT_MILLIS = 1000;

    @Override
    public boolean preHandle(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response, @Nonnull Object handler) {
        request.setAttribute("startTime", System.currentTimeMillis());
        return true;
    }

    @Override
    public void afterCompletion(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response, @Nonnull Object handler, Exception ex) {
        long startTime = (Long) request.getAttribute("startTime");
        long executeTime = System.currentTimeMillis() - startTime;
        if (executeTime > EXECUTION_TIMEOUT_MILLIS) {
            log.warn("接口执行时间过长 {} {} {}ms", request.getMethod(), request.getRequestURI(), executeTime);
        }
    }
}
