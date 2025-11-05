package com.github.jiangwangyang.web.exception;

import com.github.jiangwangyang.web.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;

/**
 * 全局异常处理 其中额外处理客户端异常
 * @see DefaultHandlerExceptionResolver
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(Throwable.class)
    public Response<?> handleThrowable(Throwable t) {
        if (t instanceof ErrorResponse errorResponse && errorResponse.getStatusCode().is4xxClientError()) {
            HttpStatus httpStatus = HttpStatus.valueOf(errorResponse.getStatusCode().value());
            log.warn("客户端异常 {}", httpStatus.getReasonPhrase());
            return Response.fail(httpStatus.getReasonPhrase());
        }
        if (t instanceof TypeMismatchException || t instanceof HttpMessageNotReadableException) {
            log.warn("客户端异常 {}", t.getMessage());
            return Response.fail(t.getMessage());
        }
        log.error("服务器异常", t);
        return Response.fail("服务器异常 " + t.getMessage());
    }
}
