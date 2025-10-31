package com.github.jiangwangyang.demo.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.WebAsyncTask;

import java.util.Map;

/**
 * @deprecated 不推荐使用除流式接口以外的任何异步响应
 */
@Deprecated
@RestController
@Slf4j
public class WebAsyncTaskController {

    @GetMapping("/webAsyncTask")
    public WebAsyncTask<Map<String, String>> webAsyncTask() {
        return new WebAsyncTask<>(100, () -> Map.of("data", "/webAsyncTask"));
    }

    /**
     * 支持自定义异常处理
     */
    @GetMapping("/webAsyncTask/error")
    public WebAsyncTask<Map<String, String>> webAsyncTaskError() {
        return new WebAsyncTask<>(100, () -> {
            throw new RuntimeException("/webAsyncTask/error");
        });
    }

    /**
     * @deprecated 不应设置响应超时时间 超时时间应由请求方控制 响应方应通过限制资源使用来避免超时
     * 支持自定义超时时间，也支持自定义超时回调
     * 超时处理不应返回数据 只应打印日志 提醒业务线程等
     */
    @Deprecated
    @GetMapping("/webAsyncTask/timeout")
    public WebAsyncTask<Map<String, String>> webAsyncTaskTimeout() {
        return new WebAsyncTask<>(100, () -> {
            try {
                Thread.sleep(5000);
                return Map.of("data", "/webAsyncTask/timeout");
            } catch (InterruptedException e) {
                return null;
            }
        });
    }

}
