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
public class CallableControllerTest {

    private final WebClient client;

    public CallableControllerTest(@LocalServerPort int port) {
        client = WebClient.builder()
                .baseUrl("http://localhost:" + port)
                .build();
    }

    @Test
    void testCallable() {
        Mono<Map<String, String>> mono = client.get()
                .uri("/callable")
                .retrieve()
                .bodyToMono(ParameterizedTypeReference.forType(Map.class));
        assertThat(mono.block()).isEqualTo(Map.of("data", "/callable"));
    }

    @Test
    void testCallableError() {
        Mono<Map<String, String>> mono = client.get()
                .uri("/callable/error")
                .retrieve()
                .bodyToMono(ParameterizedTypeReference.forType(Map.class));
        assertThat(mono.block()).isEqualTo(Map.of("error", "全局异常"));
    }

    @Test
    void testCallableTimeout() {
        Mono<Map<String, String>> mono = client.get()
                .uri("/callable/timeout")
                .retrieve()
                .bodyToMono(ParameterizedTypeReference.forType(Map.class));
        assertThat(mono.block()).isNull();
    }

}
