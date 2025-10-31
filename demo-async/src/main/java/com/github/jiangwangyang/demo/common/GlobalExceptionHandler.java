package com.github.jiangwangyang.demo.common;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.io.IOException;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 全局异常处理 返回类型要全面
     */
    @ExceptionHandler(Throwable.class)
    public Object handleThrowable(Throwable t, HttpServletResponse response) {
        log.error("全局异常", t);
        if (response.getContentType() != null && !response.getContentType().toLowerCase().contains("application/json")) {
            return "{\"error\": \"全局异常\"}";
        }
        return Map.of("error", "全局异常");
    }

    /**
     * 404处理
     * SpringBoot3.2版本后才有
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public Map<String, String> handleNoHandlerFoundException(NoResourceFoundException e) {
        log.info("NoResourceFoundException：{}", e.getMessage());
        return Map.of("404", e.getMessage());
    }

    /**
     * 线程池拒绝异常处理
     */
    @ExceptionHandler(TaskRejectedException.class)
    public Map<String, String> handleTaskRejectedException(TaskRejectedException e) {
        log.warn("TaskRejectedException：{}", e.getMessage());
        return Map.of("rejected", e.getMessage());
    }

    /**
     * 流式返回消息时，如果客户端已断开连接，会抛出IOException
     */
    @ExceptionHandler(IOException.class)
    public void handleIOException(IOException e) {
        log.warn("IOException：{}", e.getMessage());
    }

    /**
     * @deprecated 不应设置响应超时时间 超时时间应由请求方控制 响应方应通过限制资源使用来避免超时
     * 异步接口超时异常处理
     * 超时处理不应返回数据 只应打印日志 提醒业务线程等
     */
    @Deprecated
    @ExceptionHandler(AsyncRequestTimeoutException.class)
    public void handleAsyncRequestTimeoutException(AsyncRequestTimeoutException e) {
        log.warn("AsyncRequestTimeoutException：{}", e.getMessage());
    }

}
