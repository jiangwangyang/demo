package com.github.jiangwangyang.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.VirtualThreadTaskExecutor;

@Configuration
public class ExecutorConfig {
    @Bean("virtualExecutor")
    public AsyncTaskExecutor virtualExecutor() {
        return new VirtualThreadTaskExecutor("virtual-");
    }
}
