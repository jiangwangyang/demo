package com.github.jiangwangyang.web.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.HandlerInterceptor;

import java.nio.charset.StandardCharsets;

/**
 * 设置所有请求的响应字符编码默认为UTF-8
 */
public class Utf8HandlerInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        return true;
    }
}
