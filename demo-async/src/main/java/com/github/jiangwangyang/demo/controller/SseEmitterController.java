package com.github.jiangwangyang.demo.controller;

import com.github.jiangwangyang.demo.common.SseEmitterWrapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @deprecated 建议只使用FLux
 */
@Deprecated
@RestController
@Slf4j
public class SseEmitterController {

    @GetMapping("/sse")
    public SseEmitter sseEmitter(HttpServletResponse response) {
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        SseEmitterWrapper sse = new SseEmitterWrapper(100L);
        new Thread(() -> {
            sse.send(Map.of("data", "你好"), MediaType.APPLICATION_JSON);
            sse.send(Map.of("data", "你好"), MediaType.APPLICATION_JSON);
            sse.complete();
            sse.send(Map.of("data", "你好"), MediaType.APPLICATION_JSON);
            sse.send(Map.of("data", "你好"), MediaType.APPLICATION_JSON);
            sse.complete();
            sse.complete();
            sse.completeWithError(new RuntimeException("sse异常"));
            sse.completeWithError(new RuntimeException("sse异常"));
        }).start();
        return sse;
    }

    @GetMapping("/sse/timeout")
    public SseEmitter sseTimeout(HttpServletResponse response) {
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        SseEmitterWrapper sse = new SseEmitterWrapper(100L);
        new Thread(() -> {
            sse.send(Map.of("data", "/sse/timeout"), MediaType.APPLICATION_JSON);
            sse.send(Map.of("data", "/sse/timeout"), MediaType.APPLICATION_JSON);
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            sse.send(Map.of("data", "/sse/timeout"), MediaType.APPLICATION_JSON);
            sse.send(Map.of("data", "/sse/timeout"), MediaType.APPLICATION_JSON);
            sse.complete();
            sse.complete();
            sse.completeWithError(new RuntimeException("sse异常"));
            sse.completeWithError(new RuntimeException("sse异常"));
        }).start();
        return sse;
    }

    @GetMapping("/sse/error")
    public SseEmitter sseError(HttpServletResponse response) {
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        SseEmitterWrapper sse = new SseEmitterWrapper(100L);
        new Thread(() -> {
            sse.send(Map.of("data", "/sse/error"), MediaType.APPLICATION_JSON);
            sse.send(Map.of("data", "/sse/error"), MediaType.APPLICATION_JSON);
            sse.completeWithError(new RuntimeException("sse异常"));
            sse.send(Map.of("data", "/sse/error"), MediaType.APPLICATION_JSON);
            sse.send(Map.of("data", "/sse/error"), MediaType.APPLICATION_JSON);
            sse.complete();
            sse.complete();
            sse.completeWithError(new RuntimeException("sse异常"));
            sse.completeWithError(new RuntimeException("sse异常"));
        }).start();
        return sse;
    }

    /**
     * 手工测试断开连接
     */
    @GetMapping("/sse/terminate")
    public SseEmitter sseTerminate(HttpServletResponse response) {
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        SseEmitterWrapper sse = new SseEmitterWrapper(Long.MAX_VALUE);
        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                if (sse.isComplete()) {
                    return;
                }
                sse.send(Map.of("data", "/sse/terminate"), MediaType.APPLICATION_JSON);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            sse.complete();
        }).start();
        return sse;
    }

}
