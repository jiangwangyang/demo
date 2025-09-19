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
            log.info("callable");
            return Map.of("data", "callable");
        };
    }

    /**
     * 不支持自定义超时时间
     * 只能在AsyncSupportConfigurer中设置全局超时时间
     * 超时异常为AsyncRequestTimeoutException，可以直接被ControllerAdvice捕获
     * 但捕获后不要返回消息，否则会额外报IllegalStateException
     * 并且也不要设置CallableProcessingInterceptor，否则会额外报HttpMessageNotWritableException
     */
    @GetMapping("/callable-timeout")
    public Callable<Map<String, String>> callableTimeout() {
        return () -> {
            log.info("callable timeout");
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
            log.info("callable error");
            throw new RuntimeException("callable error");
        };
    }

}
