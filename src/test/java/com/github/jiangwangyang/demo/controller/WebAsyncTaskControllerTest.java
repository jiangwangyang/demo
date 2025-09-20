package com.github.jiangwangyang.demo.controller;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WebAsyncTaskControllerTest {

    private final WebClient client;

    public WebAsyncTaskControllerTest(@LocalServerPort int port) {
        client = WebClient.builder()
                .baseUrl("http://localhost:" + port)
                .build();
    }

    @Test
    void testWebAsyncTask() {
        client.get()
                .uri("/webAsyncTask")
                .retrieve()
                .bodyToMono(Object.class)
                .doOnNext(map -> assertThat(map).isInstanceOf(Map.class).isEqualTo(Map.of("data", "webAsyncTask")))
                .block();
    }

    @Test
    void testWebAsyncTaskError() {
        client.get()
                .uri("/webAsyncTask/error")
                .retrieve()
                .bodyToMono(Object.class)
                .doOnNext(map -> assertThat(map).isInstanceOf(Map.class).isEqualTo(Map.of("error", "webAsyncTask异常")))
                .block();
    }

    @Test
    void testWebAsyncTaskTimeout() {
        client.get()
                .uri("/webAsyncTask/timeout")
                .retrieve()
                .bodyToMono(Object.class)
                .doOnNext(map -> assertThat(map).isInstanceOf(Map.class).isEqualTo(Map.of("timeout", "异步请求超时")))
                .block();
    }

    @Test
    void testWebAsyncTaskOnTimeout() {
        client.get()
                .uri("/webAsyncTask/onTimeout")
                .retrieve()
                .bodyToMono(Object.class)
                .doOnNext(map -> assertThat(map).isInstanceOf(Map.class).isEqualTo(Map.of("timeout", "webAsyncTask超时")))
                .block();
    }

    @Test
    void testWebAsyncTaskBug() {
        client.get()
                .uri("/webAsyncTask/bug")
                .retrieve()
                .bodyToMono(Object.class)
                .doOnNext(map -> assertThat(map).isInstanceOf(Map.class).isEqualTo(Map.of("bug", "webAsyncTask超时")))
                .block();
    }

}
