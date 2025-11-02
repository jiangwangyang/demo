package com.github.jiangwangyang.demo.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.charset.StandardCharsets;

/**
 * 设置默认响应编码为UTF-8
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    /**
     * 添加拦截器到注册表中
     * 配置一个拦截器，用于设置所有请求的响应字符编码默认为UTF-8
     * @param registry 拦截器注册表，用于管理和注册拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HandlerInterceptor() {
            @Override
            public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
                response.setCharacterEncoding(StandardCharsets.UTF_8.name());
                return true;
            }
        }).addPathPatterns("/**");
    }
}
