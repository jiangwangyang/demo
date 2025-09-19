package com.github.jiangwangyang.demo.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;

import java.util.Map;
import java.util.Optional;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(Throwable.class)
    public Map<String, String> handleThrowable(Throwable t) {
        log.error("全局异常", t);
        return Map.of("error", Optional.ofNullable(t.getMessage()).orElse(""));
    }

    /**
     * 捕获Callable超时异常
     */
    @ExceptionHandler(AsyncRequestTimeoutException.class)
    public void handleAsyncRequestTimeoutException() {
        log.warn("异步请求超时异常");
    }

}
