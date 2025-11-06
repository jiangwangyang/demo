package com.github.jiangwangyang.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class ExecutorConfig {

    @Bean("executor")
    public ExecutorService executor() {
        return new ThreadPoolExecutor(
                Runtime.getRuntime().availableProcessors(),
                Runtime.getRuntime().availableProcessors() << 4,
                60L,
                TimeUnit.SECONDS,
                new SynchronousQueue<>(),
                Thread.ofVirtual().name("executor", 0).factory(),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }
}
