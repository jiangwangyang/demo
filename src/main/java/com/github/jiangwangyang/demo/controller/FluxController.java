package com.github.jiangwangyang.demo.controller;

import jakarta.servlet.http.HttpServletResponse;
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
public class FluxController {

    @GetMapping("/flux")
    public Flux<ServerSentEvent<Map<String, String>>> flux(HttpServletResponse response) {
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        return Flux.fromIterable(List.of(
                ServerSentEvent.builder(Map.of("data", "你好")).build(),
                ServerSentEvent.builder(Map.of("data", "你好")).build()));
    }

    @GetMapping("/flux/timeout")
    public Flux<ServerSentEvent<Map<String, String>>> fluxTimeout(HttpServletResponse response) {
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        return Flux.fromIterable(List.of(
                        ServerSentEvent.builder(Map.of("data", "timeout")).build(),
                        ServerSentEvent.builder(Map.of("data", "timeout")).build()))
                .map(e -> {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                    return e;
                })
                .timeout(Duration.ofMillis(100), Flux.just(ServerSentEvent.builder(Map.of("timeout", "flux")).build()));
    }

    @GetMapping("/flux/error")
    public Flux<ServerSentEvent<Map<String, String>>> fluxError(HttpServletResponse response) {
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        return Flux.fromIterable(List.of(
                        ServerSentEvent.builder(Map.of("data", "error")).build(),
                        ServerSentEvent.builder(Map.of("data", "!")).build()))
                .map(e -> {
                    if ("!".equals(Optional.ofNullable(e.data()).map(data -> data.get("data")).orElse(null))) {
                        throw new RuntimeException("error");
                    }
                    return e;
                })
                .onErrorResume(ex -> Flux.just(ServerSentEvent.builder(Map.of("error", ex.getMessage())).build()));
    }

    @GetMapping("/flux/terminate")
    public Flux<ServerSentEvent<Map<String, String>>> fluxTerminate(HttpServletResponse response) {
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        return Flux.fromIterable(List.of(
                        ServerSentEvent.builder(Map.of("data", "你好")).build(),
                        ServerSentEvent.builder(Map.of("data", "你好")).build()))
                .map(e -> {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                    return e;
                });
    }

}
