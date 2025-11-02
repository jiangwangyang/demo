package com.github.jiangwangyang.demo.common.exception;

import com.github.jiangwangyang.demo.common.response.Response;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;

/**
 * 错误控制器
 * 没有捕获的异常会经HandlerExceptionResolver处理后转发到该控制器
 * 用于处理客户端异常和未被捕获的服务端异常
 * @see DefaultHandlerExceptionResolver
 */
@RestController
@Slf4j
public class ExceptionController implements ErrorController {

    @RequestMapping("/error")
    public Response<?> error(HttpServletRequest request, HttpServletResponse response) {
        HttpStatus httpStatus = HttpStatus.valueOf(response.getStatus());
        if (httpStatus.is4xxClientError()) {
            log.info("4xxClientError: {}", httpStatus.getReasonPhrase());
            return Response.fail(httpStatus.getReasonPhrase());
        }
        if (request.getAttribute(RequestDispatcher.ERROR_EXCEPTION) instanceof Exception e) {
            log.error("服务器异常 {}", e.getMessage());
            return Response.fail("服务器异常 " + e.getMessage());
        }
        log.error("服务器异常");
        return Response.fail("服务器异常");
    }
}
