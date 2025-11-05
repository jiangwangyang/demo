package com.github.jiangwangyang.demo.controller;

import com.github.jiangwangyang.web.response.Response;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/exception")
public class ExceptionController {

    @RequestMapping
    public Response<?> exception() {
        throw new RuntimeException("exception");
    }
}
