package com.github.jiangwangyang.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HelloController {

    @GetMapping("/hello")
    public Map<String, String> hello() {
        return Map.of("data", "你好");
    }

    @GetMapping("/exception")
    public Map<String, String> exception() {
        throw new RuntimeException("异常");
    }

    @GetMapping("/terminate")
    public Map<String, String> terminate() {
        return Map.of("data", "_terminate".repeat(1 << 16));
    }

}
