package com.github.jiangwangyang.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.VirtualThreadTaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * 配置异步线程池 用于@Async注解使用
 * @deprecated 推荐使用CompletableFuture+自定义线程池
 */
@Deprecated
@EnableAsync
public class AsyncConfig {
    @Bean("taskExecutor")
    public AsyncTaskExecutor taskExecutor() {
        return new VirtualThreadTaskExecutor("task-async-");
    }
}
