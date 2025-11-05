package com.github.jiangwangyang.demo.controller;

import com.github.jiangwangyang.web.exception.BusinessException;
import com.github.jiangwangyang.web.response.Response;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/exception/business")
public class BusinessExceptionController {

    @RequestMapping
    public Response<?> businessException() {
        throw new BusinessException("business exception");
    }
}
