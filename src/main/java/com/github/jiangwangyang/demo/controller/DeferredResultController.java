package com.github.jiangwangyang.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.Map;

/**
 * 推荐使用DeferredResult
 * 可以全局捕获超时异常进行处理，并且可以返回数据
 * 只需往其中写数据即可，因此不会发生异常
 */
@RestController
public class DeferredResultController {

    @GetMapping("/deferred")
    public DeferredResult<Map<String, String>> deferredResult() {
        DeferredResult<Map<String, String>> deferredResult = new DeferredResult<>(100L);
        new Thread(() -> deferredResult.setResult(Map.of("data", "deferred"))).start();
        return deferredResult;
    }

    /**
     * 超时返回
     * 需要在ControllerAdvice中处理超时异常（超时捕获时可以返回数据）
     * 也可以在AsyncSupportConfigurer中添加Interceptors配置超时处理
     */
    @GetMapping("/deferred/timeout")
    public DeferredResult<Map<String, String>> deferredResultTimeout() {
        DeferredResult<Map<String, String>> deferredResult = new DeferredResult<>(100L);
        new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {
            }
            deferredResult.setResult(Map.of("data", "deferred超时"));
        }).start();
        return deferredResult;
    }

}
