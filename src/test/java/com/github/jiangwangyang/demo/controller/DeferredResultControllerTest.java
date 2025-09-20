package com.github.jiangwangyang.demo.controller;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DeferredResultControllerTest {

    private final WebClient client;

    public DeferredResultControllerTest(@LocalServerPort int port) {
        client = WebClient.builder()
                .baseUrl("http://localhost:" + port)
                .build();
    }

    @Test
    void testDeferred() {
        client.get()
                .uri("/deferred")
                .retrieve()
                .bodyToMono(Object.class)
                .doOnNext(map -> assertThat(map).isInstanceOf(Map.class).isEqualTo(Map.of("data", "deferred")))
                .block();
    }

    @Test
    void testDeferredError() {
        client.get()
                .uri("/deferred/error")
                .retrieve()
                .bodyToMono(Object.class)
                .doOnNext(map -> assertThat(map).isInstanceOf(Map.class).isEqualTo(Map.of("error", "deferred异常")))
                .block();
    }

    @Test
    void testDeferredTimeout() {
        client.get()
                .uri("/deferred/timeout")
                .retrieve()
                .bodyToMono(Object.class)
                .doOnNext(map -> assertThat(map).isInstanceOf(Map.class).isEqualTo(Map.of("timeout", "异步请求超时")))
                .block();
    }

    @Test
    void testDeferredOnTimeout() {
        client.get()
                .uri("/deferred/onTimeout")
                .retrieve()
                .bodyToMono(Object.class)
                .doOnNext(map -> assertThat(map).isInstanceOf(Map.class).isEqualTo(Map.of("timeout", "deferred超时")))
                .block();
    }

    @Test
    void testDeferredBug() {
        client.get()
                .uri("/deferred/bug")
                .retrieve()
                .bodyToMono(Object.class)
                .doOnNext(map -> assertThat(map).isInstanceOf(Map.class).isEqualTo(Map.of("bug", "deferred超时")))
                .block();
    }

}
