package com.github.jiangwangyang.demo.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.Map;

@RestController
@Slf4j
public class WebClientController {

    private final WebClient webClient;

    public WebClientController(WebClient webClient) {
        this.webClient = webClient;
    }

    /**
     * 推荐写法
     * 先并行请求 webclient请求是异步的
     * 然后再阻塞等待所有请求完成
     */
    @GetMapping("/webclient/parallel")
    public Map<String, String> getParallel() {
        Mono<? extends Map<String, String>> mono1 = webClient.get()
                .uri("http://localhost:8080/hello")
                .retrieve()
                .bodyToMono(ParameterizedTypeReference.forType(Map.class));
        Mono<Map<String, String>> mono2 = webClient.get()
                .uri("http://localhost:8080/hello")
                .retrieve()
                .bodyToMono(ParameterizedTypeReference.forType(Map.class));
        Map<String, String> map1 = mono1.block();
        Map<String, String> map2 = mono2.block();
        if (map1 == null || map2 == null) {
            return Collections.emptyMap();
        }
        return Map.of("map1", map1.toString(), "map2", map2.toString());
    }

    /**
     * 推荐写法
     * 在WebClient配置中配置好请求和读写超时
     * 业务处理中不设置超时时间
     * 最后处理异常和日志输出
     */
    @GetMapping("/webclient/flux")
    public Flux<ServerSentEvent<Map<String, String>>> getFlux() {
        return webClient.get()
                .uri("http://localhost:8080/flux")
                .retrieve()
                .bodyToFlux(ServerSentEvent.class)
                .map(event -> ServerSentEvent.builder(Map.of("data", "/webclient")).build())
                .onErrorResume(e -> {
                    log.error("webclient error", e);
                    return Flux.just(ServerSentEvent.builder(Map.of("error", "/webclient")).build());
                })
                .doOnSubscribe(s -> log.info("/webclient订阅：{}", s))
                .doFinally(signalType -> log.info("/webclient结束：{}", signalType));
    }

}
