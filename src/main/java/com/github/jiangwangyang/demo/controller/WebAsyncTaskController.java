package com.github.jiangwangyang.demo.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.WebAsyncTask;

import java.util.Map;

@RestController
@Slf4j
public class WebAsyncTaskController {

    @GetMapping("/async")
    public WebAsyncTask<Map<String, String>> webAsyncTask() {
        return new WebAsyncTask<>(1000, () -> Map.of("data", "webAsyncTask"));
    }

    /**
     * 需要在ControllerAdvice中处理超时异常（超时捕获时可以返回数据）
     */
    @GetMapping("/async/timeout")
    public WebAsyncTask<Map<String, String>> webAsyncTaskTimeout() {
        return new WebAsyncTask<>(1000, () -> {
            log.info("webAsyncTaskTimeout执行");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ignored) {
            }
            return Map.of("data", "webAsyncTaskTimeout");
        });
    }

    /**
     * 异常可以直接被ControllerAdvice捕获
     */
    @GetMapping("/async/error")
    public WebAsyncTask<Map<String, String>> webAsyncTaskError() {
        return new WebAsyncTask<>(1000, () -> {
            log.info("webAsyncTaskError执行");
            throw new RuntimeException("webAsyncTaskError");
        });
    }

}
