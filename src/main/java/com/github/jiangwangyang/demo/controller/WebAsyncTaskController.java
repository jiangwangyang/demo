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
@RestController
@Slf4j
public class WebAsyncTaskController {

    @GetMapping("/webAsyncTask")
    public WebAsyncTask<Map<String, String>> webAsyncTask() {
        return new WebAsyncTask<>(1000, () -> Map.of("data", "webAsyncTask"));
    }

    /**
     * 需要设置onTimeout回调方法
     * 也可以在ControllerAdvice中处理超时异常（超时捕获时不一定能够返回数据，需要额外判断）
     * 项目启动第一次请求超时大概率无法正常返回数据
     */
    @GetMapping("/webAsyncTask/timeout")
    public WebAsyncTask<Map<String, String>> webAsyncTaskTimeout() {
        return new WebAsyncTask<>(1000, () -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ignored) {
            }
            return Map.of("data", "webAsyncTask超时");
        });
    }

    /**
     * 异常可以直接被ControllerAdvice捕获
     */
    @GetMapping("/webAsyncTask/error")
    public WebAsyncTask<Map<String, String>> webAsyncTaskError() {
        return new WebAsyncTask<>(1000, () -> {
            throw new RuntimeException("webAsyncTask异常");
        });
    }

}
