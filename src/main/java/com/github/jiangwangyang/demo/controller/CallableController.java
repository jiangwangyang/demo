package com.github.jiangwangyang.demo.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.concurrent.Callable;

@RestController
@Slf4j
public class CallableController {

    @GetMapping("/callable")
    public Callable<Map<String, String>> callable() {
        return () -> {
            log.info("callable 执行");
            return Map.of("data", "callable");
        };
    }

    /**
     * 不支持自定义超时时间
     * 只能在AsyncSupportConfigurer中设置全局超时时间
     * 需要在ControllerAdvice中处理超时异常（超时捕获时不一定能够返回数据，需要额外判断）
     * 不推荐在AsyncSupportConfigurer中添加Interceptors配置超时处理
     */
    @GetMapping("/callable-timeout")
    public Callable<Map<String, String>> callableTimeout() {
        return () -> {
            log.info("callable timeout 执行");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ignored) {
            }
            return Map.of("data", "callable timeout");
        };
    }

    /**
     * 异常可以直接被ControllerAdvice捕获
     */
    @GetMapping("/callable-error")
    public Callable<Map<String, String>> callableError() {
        return () -> {
            log.info("callable error 执行");
            throw new RuntimeException("callable error");
        };
    }

}
