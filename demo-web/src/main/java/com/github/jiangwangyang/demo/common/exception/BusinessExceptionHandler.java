package com.github.jiangwangyang.demo.common.exception;

import com.github.jiangwangyang.demo.common.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 业务异常处理
 */
@RestControllerAdvice
@Slf4j
public class BusinessExceptionHandler {

    /**
     * 业务异常处理
     */
    @ExceptionHandler(BusinessException.class)
    public Response<?> handleBusinessException(BusinessException e) {
        Logger log = LoggerFactory.getLogger(e.getStackTrace()[0].getClassName());
        log.warn(e.getMessage());
        return Response.fail(e.getMessage());
    }
}
