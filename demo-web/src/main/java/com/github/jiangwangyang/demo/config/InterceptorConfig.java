package com.github.jiangwangyang.demo.config;

import com.github.jiangwangyang.demo.common.interceptor.ExecutionTimeHandlerInterceptor;
import com.github.jiangwangyang.demo.common.interceptor.LogHandlerInterceptor;
import com.github.jiangwangyang.demo.common.interceptor.Utf8HandlerInterceptor;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 设置请求拦截器
 * 1. 设置所有请求的响应字符编码默认为UTF-8
 * 2. 记录请求日志
 * 3. 记录执行时间过长的接口
 */
@Configuration
@Import({Utf8HandlerInterceptor.class, LogHandlerInterceptor.class, ExecutionTimeHandlerInterceptor.class})
@AllArgsConstructor
public class InterceptorConfig implements WebMvcConfigurer {

    private final Utf8HandlerInterceptor utf8HandlerInterceptor;
    private final LogHandlerInterceptor logHandlerInterceptor;
    private final ExecutionTimeHandlerInterceptor executionTimeHandlerInterceptor;

    /**
     * 添加拦截器到注册表中
     * @param registry 拦截器注册表，用于管理和注册拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(utf8HandlerInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/error");
        registry.addInterceptor(logHandlerInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/error");
        registry.addInterceptor(executionTimeHandlerInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/error");
    }
}
