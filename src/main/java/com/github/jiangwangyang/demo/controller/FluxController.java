package com.github.jiangwangyang.demo.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@Slf4j
public class FluxController {

    @GetMapping("/flux")
    public Flux<ServerSentEvent<Map<String, String>>> flux(HttpServletResponse response) {
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        return Flux.range(0, 10)
                .map(i -> ServerSentEvent.builder(Map.of("data", "你好")).build())
                .delayElements(Duration.ofMillis(100));
    }

    @GetMapping("/flux/heartbeat")
    public Flux<ServerSentEvent<Map<String, String>>> fluxHeartbeat(HttpServletResponse response) {
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        Flux<ServerSentEvent<Map<String, String>>> dataFlux = Flux.range(0, 10)
                .map(i -> ServerSentEvent.builder(Map.of("data", "/flux/heartbeat")).build())
                .delayElements(Duration.ofMillis(100));
        Flux<ServerSentEvent<Map<String, String>>> heartbeatFlux = Flux.interval(Duration.ZERO, Duration.ofMillis(100))
                .map(l -> ServerSentEvent.builder(Map.of("heartbeat", "")).build());
        return Flux.merge(dataFlux, heartbeatFlux)
                .takeUntilOther(dataFlux.then());
    }

    /**
     * timeout指两次数据之间超时
     * take指整个Flux数据流超时
     */
    @GetMapping("/flux/timeout")
    public Flux<ServerSentEvent<Map<String, String>>> fluxTimeout(HttpServletResponse response) {
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        return Flux.range(0, 10)
                .map(i -> ServerSentEvent.builder(Map.of("data", "/flux/timeout")).build())
                .delayElements(Duration.ofMillis(1000))
                .take(Duration.ofMillis(2100));
    }

    @GetMapping("/flux/error")
    public Flux<ServerSentEvent<Map<String, String>>> fluxError(HttpServletResponse response) {
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        return Flux.fromIterable(List.of(
                        ServerSentEvent.builder(Map.of("data", "/flux/error")).build(),
                        ServerSentEvent.builder(Map.of("data", "flag")).build()))
                .map(e -> {
                    if ("flag".equals(Optional.ofNullable(e.data()).map(data -> data.get("data")).orElse(null))) {
                        throw new RuntimeException("error");
                    }
                    return e;
                });
    }

    /**
     * 手工测试断开连接
     */
    @GetMapping("/flux/terminate")
    public Flux<ServerSentEvent<Map<String, String>>> fluxTerminate(HttpServletResponse response) {
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        return Flux.range(0, 10)
                .map(i -> ServerSentEvent.builder(Map.of("data", "/flux/terminate")).build())
                .delayElements(Duration.ofMillis(1000));
    }

}
