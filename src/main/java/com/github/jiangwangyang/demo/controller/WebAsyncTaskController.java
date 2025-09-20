package com.github.jiangwangyang.demo.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.WebAsyncTask;

import java.util.Map;

/**
 * 不推荐使用WebAsyncTask，存在超时问题
 * 项目启动第一次请求超时大概率无法正常返回数据
 */
@Deprecated
@RestController
@Slf4j
public class WebAsyncTaskController {

    @GetMapping("/webAsyncTask")
    public WebAsyncTask<Map<String, String>> webAsyncTask() {
        return new WebAsyncTask<>(100, () -> Map.of("data", "webAsyncTask"));
    }

    /**
     * 在ControllerAdvice中处理超时异常（超时捕获时不一定能够返回数据，需要额外判断）
     * 项目启动第一次请求超时大概率无法正常返回数据
     */
    @GetMapping("/webAsyncTask/timeout")
    public WebAsyncTask<Map<String, String>> webAsyncTaskTimeout() {
        return new WebAsyncTask<>(100, () -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {
            }
            return Map.of("data", "webAsyncTask超时");
        });
    }

    /**
     * 设置onTimeout回调方法处理超时
     * 项目启动第一次请求超时大概率无法正常返回数据
     */
    @GetMapping("/webAsyncTask/onTimeout")
    public WebAsyncTask<Map<String, String>> webAsyncTaskOnTimeout() {
        WebAsyncTask<Map<String, String>> webAsyncTask = new WebAsyncTask<>(100, () -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {
            }
            return Map.of("data", "webAsyncTask超时");
        });
        webAsyncTask.onTimeout(() -> {
            log.warn("webAsyncTask超时");
            return Map.of("timeout", "webAsyncTask超时");
        });
        return webAsyncTask;
    }

    /**
     * 异常可以直接被ControllerAdvice捕获
     */
    @GetMapping("/webAsyncTask/error")
    public WebAsyncTask<Map<String, String>> webAsyncTaskError() {
        return new WebAsyncTask<>(100, () -> {
            throw new RuntimeException("webAsyncTask异常");
        });
    }

}
