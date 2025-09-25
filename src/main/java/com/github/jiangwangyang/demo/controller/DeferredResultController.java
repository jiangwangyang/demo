package com.github.jiangwangyang.demo.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.Map;

/**
 * @deprecated 不应使用除流式接口以外的任何异步响应
 * 这些异步方法都存在超时问题
 * 如果在超时的瞬间返回数据，则会导致该异步方法和超时处理同时返回数据导致结果不确定性
 * DeferredResult只需在最后设置数据，异步执行则要由程序员自己控制
 */
@Deprecated
@RestController
@Slf4j
public class DeferredResultController {

    @GetMapping("/deferred")
    public DeferredResult<Map<String, String>> deferredResult() {
        DeferredResult<Map<String, String>> deferredResult = new DeferredResult<>(100L);
        new Thread(() -> deferredResult.setResult(Map.of("data", "/deferred"))).start();
        return deferredResult;
    }

    /**
     * 直接setErrorResult设置异常 可以被ControllerAdvice捕获
     */
    @GetMapping("/deferred/error")
    public DeferredResult<Map<String, String>> deferredResultError() {
        DeferredResult<Map<String, String>> deferredResult = new DeferredResult<>(100L);
        new Thread(() -> deferredResult.setErrorResult(new RuntimeException("/deferred/error"))).start();
        return deferredResult;
    }

    /**
     * 超时异常可以直接被ControllerAdvice捕获
     * 也可以在AsyncSupportConfigurer中添加Interceptor处理
     */
    @GetMapping("/deferred/timeout")
    public DeferredResult<Map<String, String>> deferredResultTimeout() {
        DeferredResult<Map<String, String>> deferredResult = new DeferredResult<>(100L);
        new Thread(() -> {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            deferredResult.setResult(Map.of("data", "/deferred/timeout"));
        }).start();
        return deferredResult;
    }

    /**
     * 超时优先由onTimeout回调方法处理
     */
    @GetMapping("/deferred/onTimeout")
    public DeferredResult<Map<String, String>> deferredResultOnTimeout() {
        DeferredResult<Map<String, String>> deferredResult = new DeferredResult<>(100L);
        deferredResult.onTimeout(() -> {
            log.warn("超时：/deferred/onTimeout");
            deferredResult.setResult(Map.of("timeout", "/deferred/onTimeout"));
        });
        new Thread(() -> {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            deferredResult.setResult(Map.of("data", "/deferred/onTimeout"));
        }).start();
        return deferredResult;
    }

    /**
     * 超时bug示例
     * 超时回调被执行，但返回大概率是未超时的结果
     */
    @GetMapping("/deferred/bug")
    public DeferredResult<Map<String, String>> deferredResultBug() {
        DeferredResult<Map<String, String>> deferredResult = new DeferredResult<>(100L);
        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                log.warn(e.getMessage());
            }
            deferredResult.setResult(Map.of("data", "/deferred/bug"));
        });
        deferredResult.onTimeout(() -> {
            thread.interrupt();
            log.warn("超时：/deferred/bug");
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignored) {
            }
            deferredResult.setResult(Map.of("timeout", "/deferred/bug"));
        });
        thread.start();
        return deferredResult;
    }

    @GetMapping("/deferred/terminate")
    public DeferredResult<Map<String, String>> deferredResultTerminate() {
        DeferredResult<Map<String, String>> deferredResult = new DeferredResult<>(100L);
        new Thread(() -> {
            try {
                Thread.sleep(80);
            } catch (InterruptedException ignored) {
            }
            deferredResult.setResult(Map.of("data", "/deferred/terminate"));
        }).start();
        return deferredResult;
    }

}
