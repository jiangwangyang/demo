package com.github.jiangwangyang.web.exception;

import com.github.jiangwangyang.web.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 业务异常处理
 */
@RestControllerAdvice
@Slf4j
public class BusinessExceptionHandler {
    @ExceptionHandler(BusinessException.class)
    public Response<?> handleBusinessException(BusinessException e) {
        log.warn("业务异常 {}", e.getMessage());
        System.out.println("\n" + e + "\n");
        return Response.fail(e.getMessage());
    }
}
