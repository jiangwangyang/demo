package com.github.jiangwangyang.demo;

import com.github.jiangwangyang.demo.controller.DemoController;
import com.github.jiangwangyang.web.response.Response;
import com.github.jiangwangyang.web.util.ObjectMapperUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
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

    @Test
    void testRuntimeException() {
        Response<String> vo = webClient
                .post()
                .uri("/exception/runtime")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Response<String>>() {
                })
                .block();
        assertThat(vo).isEqualTo(Response.success("runtime exception", Map.of()));
    }

    @Test
    void testBusinessException() {
        Response<String> vo = webClient
                .post()
                .uri("/exception/business")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Response<String>>() {
                })
                .block();
        assertThat(vo).isEqualTo(Response.fail("business exception", Map.of()));
    }

    @Test
    public void testExecutor() {
        Response<String> vo = webClient
                .post()
                .uri("/executor")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Response<String>>() {
                })
                .block();
        assertThat(vo).isEqualTo(Response.success("executor", Map.of()));
    }

    @Test
    void testFlux() {
        List<?> list = webClient
                .post()
                .uri("/flux")
                .retrieve()
                .bodyToFlux(new ParameterizedTypeReference<ServerSentEvent<String>>() {
                })
                .map(ServerSentEvent::data)
                .toStream()
                .toList();
        assertThat(list).isEqualTo(List.of(
                "hello",
                "world",
                ObjectMapperUtil.writeValueAsString(Map.of("hello", "world")),
                "hello",
                "world",
                ObjectMapperUtil.writeValueAsString(Map.of("hello", "world"))
        ));
    }
}
