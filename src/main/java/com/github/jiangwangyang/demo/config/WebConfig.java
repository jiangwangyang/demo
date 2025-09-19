package com.github.jiangwangyang.demo.config;

import com.github.jiangwangyang.demo.common.DeferredAdvice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private DeferredAdvice deferredAdvice;

    @Override
    public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
        configurer.registerDeferredResultInterceptors(deferredAdvice);
    }
}
