package com.github.jiangwangyang.demo.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.context.request.async.DeferredResultProcessingInterceptor;

import java.util.Map;

/**
 * 处理DeferredResult超时
 * 返回false表示已处理，Spring不再做默认动作
 * 由于ControllerAdvice只能处理非异步异常，所以需要在AsyncSupportConfigurer中注册
 */
@RestControllerAdvice
@Slf4j
public class DeferredAdvice implements DeferredResultProcessingInterceptor {

    @Override
    public <T> boolean handleTimeout(NativeWebRequest request, DeferredResult<T> result) {
        log.warn("Deferred Result Timeout!");
        result.setErrorResult(Map.of("error", "timeout"));
        return false;
    }

}
