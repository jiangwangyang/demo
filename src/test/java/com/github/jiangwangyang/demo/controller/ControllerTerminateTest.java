package com.github.jiangwangyang.demo.controller;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.web.reactive.function.client.WebClient;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

/**
 * 只有数据发送了一半断开连接会报IO异常
 * 数据还没开始发送就断开连接则不会报异常
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ControllerTerminateTest {

    private final WebClient client;

    public ControllerTerminateTest(@LocalServerPort int port) {
        client = WebClient.builder()
                .baseUrl("http://localhost:" + port)
                .build();
    }

    @Test
    void testTerminate() throws InterruptedException {
        client.get()
                .uri("/terminate")
                .retrieve()
                .bodyToFlux(DataBuffer.class)
                .take(1)
                .doOnNext(buf -> {
                    byte[] bytes = new byte[buf.readableByteCount()];
                    buf.read(bytes);
                    System.out.println(new String(bytes, StandardCharsets.UTF_8));
                    DataBufferUtils.release(buf);
                })
                .blockFirst();
        Thread.sleep(1000);
    }

    @Test
    void testSseTerminate() throws InterruptedException {
        client.get()
                .uri("/sse/terminate")
                .retrieve()
                .bodyToFlux(DataBuffer.class)
                .take(1)
                .doOnNext(buf -> {
                    byte[] bytes = new byte[buf.readableByteCount()];
                    buf.read(bytes);
                    System.out.println(new String(bytes, StandardCharsets.UTF_8));
                    DataBufferUtils.release(buf);
                })
                .blockFirst();
        Thread.sleep(1000);
    }

    @Test
    void testDeferredTerminate() throws InterruptedException {
        try {
            client.get()
                    .uri("/deferred/terminate")
                    .retrieve()
                    .bodyToFlux(DataBuffer.class)
                    .take(1)
                    .doOnNext(buf -> {
                        byte[] bytes = new byte[buf.readableByteCount()];
                        buf.read(bytes);
                        System.out.println(new String(bytes, StandardCharsets.UTF_8));
                        DataBufferUtils.release(buf);
                    })
                    .blockFirst(Duration.ofMillis(50));
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
        }
        Thread.sleep(1000);
    }

}
