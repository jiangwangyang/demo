package com.github.jiangwangyang.demo.controller;

import com.github.jiangwangyang.web.exception.BusinessException;
import com.github.jiangwangyang.web.record.Recordable;
import com.github.jiangwangyang.web.response.Response;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/exception")
@Recordable
public class ExceptionController {

    @RequestMapping("/runtime")
    public Response<?> exception() {
        throw new RuntimeException("runtime exception");
    }

    @RequestMapping("/business")
    public Response<?> businessException() {
        throw new BusinessException("business exception");
    }
}
