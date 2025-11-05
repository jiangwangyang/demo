package com.github.jiangwangyang.demo;

import com.github.jiangwangyang.demo.controller.DemoController;
import com.github.jiangwangyang.web.response.Response;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DemoApplicationTest {
    private final WebClient webClient;

    public DemoApplicationTest(@LocalServerPort int port) {
        webClient = WebClient.builder()
                .baseUrl("http://localhost:" + port)
                .build();
    }

    @Test
    void testDemo() {
        Response<DemoController.VO> vo = webClient
                .post()
                .uri("/demo")
                .bodyValue(new DemoController.DTO("demo"))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Response<DemoController.VO>>() {
                })
                .block();
        assertThat(vo).isEqualTo(Response.success(new DemoController.VO("demo"), Map.of()));
    }
}
