package com.github.jiangwangyang.demo.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.WebAsyncTask;

import java.util.Map;

/**
 * 这些异步方法都存在超时问题
 * 如果在超时的瞬间返回数据，则会导致该异步方法和超时处理同时返回数据造成冲突
 * 注意：在超时时，执行异步方法的线程会被interrupt
 */
@RestController
@Slf4j
public class WebAsyncTaskController {

    @GetMapping("/webAsyncTask")
    public WebAsyncTask<Map<String, String>> webAsyncTask() {
        return new WebAsyncTask<>(100, () -> Map.of("data", "webAsyncTask"));
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

    /**
     * 超时异常可以直接被ControllerAdvice捕获
     */
    @GetMapping("/webAsyncTask/timeout")
    public WebAsyncTask<Map<String, String>> webAsyncTaskTimeout() {
        return new WebAsyncTask<>(100, () -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                log.warn(e.getMessage());
                Thread.sleep(1000);
            }
            return Map.of("data", "webAsyncTask超时");
        });
    }

    /**
     * 超时优先由onTimeout回调方法处理
     */
    @GetMapping("/webAsyncTask/onTimeout")
    public WebAsyncTask<Map<String, String>> webAsyncTaskOnTimeout() {
        WebAsyncTask<Map<String, String>> webAsyncTask = new WebAsyncTask<>(100, () -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                log.warn(e.getMessage());
                Thread.sleep(1000);
            }
            return Map.of("data", "webAsyncTask超时");
        });
        webAsyncTask.onTimeout(() -> {
            log.warn("webAsyncTask超时");
            return Map.of("timeout", "webAsyncTask超时");
        });
        return webAsyncTask;
    }

}
