package com.github.jiangwangyang.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.Map;

/**
 * DeferredResult 控制器
 * 需要处理超时
 * 写数据不会发生错误，无需处理
 */
@RestController
public class DeferredController {

    /**
     * 正常返回
     */
    @GetMapping("/deferred")
    public DeferredResult<Map<String, String>> deferred() {
        DeferredResult<Map<String, String>> deferredResult = new DeferredResult<>(1000L);
        new Thread(() -> deferredResult.setResult(Map.of("data", "deferred"))).start();
        return deferredResult;
    }

    /**
     * 超时返回
     * 必须编写DeferredAdvice处理超时，并且在AsyncSupportConfigurer中注册，否则超时不会被处理
     */
    @GetMapping("/deferred-timeout")
    public DeferredResult<Map<String, String>> deferredTimeout() {
        return new DeferredResult<>(1000L);
    }

}
