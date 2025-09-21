package com.github.jiangwangyang.demo.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.concurrent.Callable;

/**
 * 这些异步方法都存在超时问题
 * 如果在超时的瞬间返回数据，则会导致该异步方法和超时处理同时返回数据导致结果不确定性
 * 注意：在某些版本会因超时数据冲突导致异常
 * 注意：在超时时，执行异步方法的线程会被interrupt
 */
@RestController
@Slf4j
public class CallableController {

    @GetMapping("/callable")
    public Callable<Map<String, String>> callable() {
        return () -> Map.of("data", "/callable");
    }

    /**
     * 异常可以直接被ControllerAdvice捕获
     * 也可以在AsyncSupportConfigurer中添加Interceptor处理
     */
    @GetMapping("/callable/error")
    public Callable<Map<String, String>> callableError() {
        return () -> {
            throw new RuntimeException("/callable/error");
        };
    }

    /**
     * 不支持自定义超时时间
     * 只能在AsyncSupportConfigurer中设置全局超时时间
     * 超时异常可以直接被ControllerAdvice捕获
     */
    @GetMapping("/callable/timeout")
    public Callable<Map<String, String>> callableTimeout() {
        return () -> {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                log.warn(e.getMessage());
                Thread.sleep(5000);
            }
            return Map.of("data", "/callable/timeout");
        };
    }

}
