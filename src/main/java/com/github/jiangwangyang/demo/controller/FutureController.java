package com.github.jiangwangyang.demo.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

/**
 * @deprecated 不推荐使用除流式接口以外的任何异步响应
 */
@Deprecated
@RestController
@Slf4j
public class FutureController {

    @GetMapping("/future")
    public Future<Map<String, String>> future() {
        return CompletableFuture.supplyAsync(() -> Map.of("data", "/future"));
    }

    /**
     * 不支持自定义异常处理，只能使用全局异常处理
     */
    @GetMapping("/future/error")
    public Future<Map<String, String>> futureError() {
        return CompletableFuture.supplyAsync(() -> {
            throw new RuntimeException("/future/error");
        });
    }

    /**
     * @deprecated 不应设置响应超时时间 超时时间应由请求方控制 响应方应通过限制资源使用来避免超时
     * 不支持自定义超时时间，只能使用全局异步超时时间
     * 无法设置自定义超时回调，从而无法提醒执行线程超时
     * 超时处理不应返回数据 只应打印日志 提醒业务线程等
     */
    @Deprecated
    @GetMapping("/future/timeout")
    public Future<Map<String, String>> futureTimeout() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(5000);
                return Map.of("data", "/future/timeout");
            } catch (InterruptedException e) {
                return null;
            }
        });
    }

}
