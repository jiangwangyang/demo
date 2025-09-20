package com.github.jiangwangyang.demo.controller;

import com.github.jiangwangyang.demo.common.SseEmitterWrapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@Slf4j
public class SseEmitterController {

    @GetMapping("/sse")
    public SseEmitter sseEmitter(HttpServletResponse response) {
        response.setCharacterEncoding("utf-8");
        SseEmitterWrapper sse = new SseEmitterWrapper(100L);
        sse.onCompletion(() -> log.info("onCompletion complete: {}", sse.isComplete()));
        new Thread(() -> {
            sse.send("你好");
            sse.send("你好");
            sse.complete();
            sse.complete();
            log.info("complete: {}", sse.isComplete());
        }).start();
        return sse;
    }

    @GetMapping("/sse/timeout")
    public SseEmitter sseTimeout(HttpServletResponse response) {
        response.setCharacterEncoding("utf-8");
        SseEmitterWrapper sse = new SseEmitterWrapper(100L);
        sse.onCompletion(() -> log.info("onCompletion complete: {}", sse.isComplete()));
        sse.onTimeout(() -> log.info("onTimeout complete: {}", sse.isComplete()));
        new Thread(() -> {
            sse.send("你好");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {
            }
            log.info("complete: {}", sse.isComplete());
            sse.send("你好");
            sse.send("你好");
            sse.complete();
            sse.complete();
            sse.completeWithError(new RuntimeException("sse异常"));
            sse.completeWithError(new RuntimeException("sse异常"));
        }).start();
        return sse;
    }

    @GetMapping("/sse/error")
    public SseEmitter sseError(HttpServletResponse response) {
        response.setCharacterEncoding("utf-8");
        SseEmitterWrapper sse = new SseEmitterWrapper(100L);
        sse.onCompletion(() -> log.info("onCompletion complete: {}", sse.isComplete()));
        new Thread(() -> {
            sse.send("你好");
            sse.completeWithError(new RuntimeException("sse异常"));
            log.info("complete: {}", sse.isComplete());
            sse.send("你好");
            sse.send("你好");
            sse.complete();
            sse.complete();
            sse.completeWithError(new RuntimeException("sse异常"));
            sse.completeWithError(new RuntimeException("sse异常"));
        }).start();
        return sse;
    }

}
