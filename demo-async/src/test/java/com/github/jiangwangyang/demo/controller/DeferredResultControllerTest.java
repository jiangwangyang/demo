package com.github.jiangwangyang.demo.controller;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

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
    void testDeferredResult() {
        Mono<Map<String, String>> mono = client.get()
                .uri("/deferredResult")
                .retrieve()
                .bodyToMono(ParameterizedTypeReference.forType(Map.class));
        assertThat(mono.block()).isEqualTo(Map.of("data", "/deferredResult"));
    }

    @Test
    void testDeferredResultError() {
        Mono<Map<String, String>> mono = client.get()
                .uri("/deferredResult/error")
                .retrieve()
                .bodyToMono(ParameterizedTypeReference.forType(Map.class));
        assertThat(mono.block()).isEqualTo(Map.of("error", "全局异常"));
    }

    @Test
    void testDeferredResultTimeout() {
        Mono<Map<String, String>> mono = client.get()
                .uri("/deferredResult/timeout")
                .retrieve()
                .bodyToMono(ParameterizedTypeReference.forType(Map.class));
        assertThat(mono.block()).isNull();
    }

}
