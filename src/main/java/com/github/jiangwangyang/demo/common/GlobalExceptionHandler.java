package com.github.jiangwangyang.demo.common;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;

import java.util.Map;
import java.util.Optional;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 全局异常处理
     */
    @ExceptionHandler(Throwable.class)
    public Map<String, String> handleThrowable(Throwable t) {
        log.error("全局异常", t);
        return Map.of("error", Optional.ofNullable(t.getMessage()).orElse(""));
    }

    /**
     * 线程池拒绝异常处理
     */
    @ExceptionHandler(TaskRejectedException.class)
    public Map<String, String> handleTaskRejectedException(TaskRejectedException e) {
        log.error("任务被拒绝异常", e);
        return Map.of("rejected", "线程池已满");
    }

    /**
     * 超时异常处理
     * Callable超时异常，此时response可能已经commit，不一定能够返回数据
     * WebAsyncTask超时异常，此时response可能已经commit，不一定能够返回数据
     * DeferredResult超时异常，此时可以返回数据
     */
    @ExceptionHandler(AsyncRequestTimeoutException.class)
    public Object handleAsyncRequestTimeoutException(HttpServletResponse response) {
        log.warn("异步请求超时异常");
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
