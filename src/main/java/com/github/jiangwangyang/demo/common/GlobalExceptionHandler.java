package com.github.jiangwangyang.demo.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(Throwable.class)
    public Map<String, String> handleThrowable(Throwable t) {
        log.error("全局异常", t);
        return Map.of("error", t.getMessage());
    }

}
