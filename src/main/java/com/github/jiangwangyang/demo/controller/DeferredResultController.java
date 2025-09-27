package com.github.jiangwangyang.demo.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * @deprecated 不推荐使用除流式接口以外的任何异步响应
 */
@Deprecated
@RestController
@Slf4j
public class DeferredResultController {

    @GetMapping("/deferredResult")
    public DeferredResult<Map<String, String>> deferredResult() {
        DeferredResult<Map<String, String>> deferredResult = new DeferredResult<>(100L);
        CompletableFuture.runAsync(() -> deferredResult.setResult(Map.of("data", "/deferredResult")));
        return deferredResult;
    }

    /**
     * 支持自定义异常处理
     */
    @GetMapping("/deferredResult/error")
    public DeferredResult<Map<String, String>> deferredResultError() {
        DeferredResult<Map<String, String>> deferredResult = new DeferredResult<>(100L);
        CompletableFuture.runAsync(() -> deferredResult.setErrorResult(new RuntimeException("/deferredResult/error")));
        return deferredResult;
    }

    /**
     * @deprecated 不应设置响应超时时间 超时时间应由请求方控制 响应方应通过限制资源使用来避免超时
     * 支持自定义超时时间，也支持自定义超时回调
     * 超时处理不应返回数据 只应打印日志 提醒业务线程等
     */
    @Deprecated
    @GetMapping("/deferredResult/timeout")
    public DeferredResult<Map<String, String>> deferredResultTimeout() {
        DeferredResult<Map<String, String>> deferredResult = new DeferredResult<>(100L);
        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(5000);
                deferredResult.setResult(Map.of("data", "/deferredResult/timeout"));
            } catch (InterruptedException e) {
                deferredResult.setResult(null);
            }
        });
        deferredResult.onTimeout(thread::interrupt);
        thread.start();
        return deferredResult;
    }

}
