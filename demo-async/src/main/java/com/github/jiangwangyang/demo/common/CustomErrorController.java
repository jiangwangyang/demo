package com.github.jiangwangyang.demo.common;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.net.URI;
import java.util.Optional;

/**
 * @deprecated 建议只使用ControllerAdvice
 * 自定义错误控制器
 * 必须实现ErrorController接口
 * 所有没有映射的错误请求都将被这个控制器处理
 */
@Deprecated
@Controller
@Slf4j
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public ProblemDetail error(HttpServletRequest request, HttpServletResponse response) {
        HttpStatus httpStatus = HttpStatus.valueOf(response.getStatus());
        String instance = Optional
                .ofNullable(request.getAttribute(RequestDispatcher.FORWARD_REQUEST_URI))
                .map(Object::toString)
                .orElse(request.getRequestURI());
        ProblemDetail problemDetail = ProblemDetail.forStatus(httpStatus);
        problemDetail.setTitle(httpStatus.getReasonPhrase());
        problemDetail.setInstance(URI.create(instance));
        if (httpStatus.is5xxServerError()) {
            log.error("5xxServerError: {}", problemDetail);
        }
        return problemDetail;
    }

}
