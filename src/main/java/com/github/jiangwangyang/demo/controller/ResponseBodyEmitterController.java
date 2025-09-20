package com.github.jiangwangyang.demo.controller;

import com.github.jiangwangyang.demo.common.ResponseBodyEmitterWrapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import java.nio.charset.StandardCharsets;

/**
 * SSE适合用来发送结构数据
 * Emitter适合用于发送无结构数据
 */
@RestController
@Slf4j
public class ResponseBodyEmitterController {

    @GetMapping("/emitter")
    public ResponseBodyEmitter emitter(HttpServletResponse response) {
        response.setContentType(MediaType.TEXT_EVENT_STREAM_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        ResponseBodyEmitterWrapper emitter = new ResponseBodyEmitterWrapper(100L);
        new Thread(() -> {
            emitter.send("你");
            emitter.send("好");
            emitter.complete();
            emitter.send("你");
            emitter.send("好");
            emitter.complete();
            emitter.complete();
            emitter.completeWithError(new RuntimeException("emitter异常"));
            emitter.completeWithError(new RuntimeException("emitter异常"));
        }).start();
        return emitter;
    }

    @GetMapping("/emitter/timeout")
    public ResponseBodyEmitter emitterTimeout(HttpServletResponse response) {
        response.setContentType(MediaType.TEXT_EVENT_STREAM_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        ResponseBodyEmitterWrapper emitter = new ResponseBodyEmitterWrapper(100L);
        new Thread(() -> {
            emitter.send("你");
            emitter.send("好");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {
            }
            emitter.send("你");
            emitter.send("好");
            emitter.complete();
            emitter.complete();
            emitter.completeWithError(new RuntimeException("emitter异常"));
            emitter.completeWithError(new RuntimeException("emitter异常"));
        }).start();
        return emitter;
    }

    @GetMapping("/emitter/error")
    public ResponseBodyEmitter emitterError(HttpServletResponse response) {
        response.setContentType(MediaType.TEXT_EVENT_STREAM_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        ResponseBodyEmitterWrapper emitter = new ResponseBodyEmitterWrapper(100L);
        new Thread(() -> {
            emitter.send("你");
            emitter.send("好");
            emitter.completeWithError(new RuntimeException("emitter异常"));
            emitter.send("你");
            emitter.send("好");
            emitter.complete();
            emitter.complete();
            emitter.completeWithError(new RuntimeException("emitter异常"));
            emitter.completeWithError(new RuntimeException("emitter异常"));
        }).start();
        return emitter;
    }

}
