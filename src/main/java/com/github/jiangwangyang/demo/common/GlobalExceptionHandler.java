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
import java.util.Optional;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 404处理
     * 3.2版本后才有
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public Map<String, String> handleNoHandlerFoundException(NoResourceFoundException e) {
        log.info("NoResourceFoundException：{}", e.getMessage());
        return Map.of("404", e.getMessage());
    }

    /**
     * 全局异常处理
     */
    @ExceptionHandler(Throwable.class)
    public Object handleThrowable(Throwable t, HttpServletResponse response) {
        log.error("全局异常！", t);
        if (response.isCommitted()) {
            log.warn("response已commit，无法返回数据");
            return null;
        }
        if (response.getContentType() != null && !response.getContentType().toLowerCase().contains("application/json")) {
            return "{\"error\": \"" + Optional.ofNullable(t.getMessage()).orElse("") + "\"}";
        }
        return Map.of("error", Optional.ofNullable(t.getMessage()).orElse(""));
    }

    /**
     * 线程池拒绝异常处理
     */
    @ExceptionHandler(TaskRejectedException.class)
    public Map<String, String> handleTaskRejectedException(TaskRejectedException e) {
        log.error("TaskRejectedException：{}", e.getMessage());
        return Map.of("rejected", "线程池已满");
    }

    /**
     * 流式返回消息时，如果客户端已断开连接，会抛出CompletionException
     */
    @ExceptionHandler(IOException.class)
    public void handleIOException(IOException e) {
        log.warn("IOException：{}", e.getMessage());
    }

    /**
     * 超时异常处理
     * 异步任务超时后仍可能返回数据 因此需要做额外判断
     */
    @ExceptionHandler(AsyncRequestTimeoutException.class)
    public Object handleAsyncRequestTimeoutException(AsyncRequestTimeoutException e, HttpServletResponse response) {
        log.warn("AsyncRequestTimeoutException：{}", e.getMessage());
        if (response.isCommitted()) {
            log.warn("response已commit，无法返回数据");
            return null;
        }
        if (response.getContentType() != null && !response.getContentType().toLowerCase().contains("application/json")) {
            return "{\"timeout\": \"异步请求超时\"}";
        }
        return Map.of("timeout", "异步请求超时");
    }

}
