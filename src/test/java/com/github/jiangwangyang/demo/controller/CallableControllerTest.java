package com.github.jiangwangyang.demo.controller;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CallableControllerTest {

    private final WebClient client;

    public CallableControllerTest(@LocalServerPort int port) {
        client = WebClient.builder()
                .baseUrl("http://localhost:" + port)
                .build();
    }

    @Test
    void testCallable() {
        client.get()
                .uri("/callable")
                .retrieve()
                .bodyToMono(Map.class)
                .doOnNext(map -> assertThat(map).isInstanceOf(Map.class).isEqualTo(Map.of("data", "callable")))
                .block();
    }

    @Test
    void testCallableError() {
        client.get()
                .uri("/callable/error")
                .retrieve()
                .bodyToMono(Map.class)
                .doOnNext(map -> assertThat(map).isInstanceOf(Map.class).isEqualTo(Map.of("error", "callable异常")))
                .block();
    }

    @Test
    void testCallableTimeout() {
        client.get()
                .uri("/callable/timeout")
                .retrieve()
                .bodyToMono(Map.class)
                .doOnNext(map -> assertThat(map).isInstanceOf(Map.class).isEqualTo(Map.of("timeout", "异步请求超时")))
                .block();
    }

}
