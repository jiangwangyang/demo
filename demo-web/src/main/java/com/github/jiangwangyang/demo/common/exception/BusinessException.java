package com.github.jiangwangyang.demo.common.exception;

import org.springframework.core.NestedRuntimeException;

/**
 * 业务异常
 */
public class BusinessException extends NestedRuntimeException {

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
