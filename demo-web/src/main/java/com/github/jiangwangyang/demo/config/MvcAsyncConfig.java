package com.github.jiangwangyang.demo.config;

import com.github.jiangwangyang.demo.common.util.ResponseWriteUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.VirtualThreadTaskExecutor;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 设置mvc异步线程池
 * 注意：响应不应设置超时时间 超时时间应由请求方设置
 * 注意：响应的时间应由业务控制 如一个请求只处理适量数据 从而保证响应迅速
 * @see ResponseWriteUtil
 * @deprecated Servlet容器建议使用ResponseWriteUtil替代异步响应
 */
@Deprecated
public class MvcAsyncConfig implements WebMvcConfigurer {

    /**
     * 配置mvc异步线程池
     * @return mvc异步线程池
     */
    @Bean("mvcAsyncExecutor")
    public AsyncTaskExecutor mvcAsyncExecutor() {
        return new VirtualThreadTaskExecutor("mvc-async-");
    }

    /**
     * 配置异步支持的相关参数 设置用于处理异步请求的线程池执行器
     * 注意：异步线程池执行器不应设置超时时间
     * @param configurer 异步支持配置器，用于配置异步相关参数
     */
    @Override
    public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
        configurer.setTaskExecutor(mvcAsyncExecutor());
    }
}
