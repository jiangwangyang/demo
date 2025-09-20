package com.github.jiangwangyang.demo.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.net.URI;

/**
 * 自定义错误控制器
 * 必须实现ErrorController
 * 所有没有映射的错误请求都将被这个控制器处理
 */
@Controller
@Slf4j
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public ResponseEntity<?> error(HttpServletRequest request, HttpServletResponse response) {
        HttpStatus httpStatus = HttpStatus.valueOf(response.getStatus());
        ProblemDetail problemDetail = ProblemDetail.forStatus(httpStatus);
        problemDetail.setTitle(httpStatus.getReasonPhrase());
        problemDetail.setInstance(URI.create(request.getAttribute(RequestDispatcher.FORWARD_REQUEST_URI).toString()));
        if (httpStatus.is5xxServerError()) {
            log.error("5xxServerError: {}", problemDetail);
        } else if (httpStatus.is4xxClientError()) {
            log.debug("4xxClientError: {}", problemDetail);
        } else {
            log.error("UnknowError: {}", problemDetail);
        }
        return ResponseEntity.status(response.getStatus()).body(problemDetail);
    }

}
