package com.github.jiangwangyang.demo.config;

import com.github.jiangwangyang.demo.common.DeferredAdvice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private DeferredAdvice deferredAdvice;

    @Bean
    public ThreadPoolTaskExecutor mvcAsyncExecutor() {
        ThreadPoolTaskExecutor ex = new ThreadPoolTaskExecutor();
        ex.setCorePoolSize(1);
        ex.setMaxPoolSize(1);
        ex.setQueueCapacity(0);
        ex.setThreadNamePrefix("mvc-async-");
        ex.initialize();
        return ex;
    }

    @Override
    public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
        // 设置SpringMVC的异步线程池
        configurer.setTaskExecutor(mvcAsyncExecutor());
        // 设置全局异步超时时间
        configurer.setDefaultTimeout(1000);
        // 设置DeferredResult的超时处理
        configurer.registerDeferredResultInterceptors(deferredAdvice);
    }

}
