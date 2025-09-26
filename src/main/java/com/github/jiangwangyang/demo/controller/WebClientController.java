package com.github.jiangwangyang.demo.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

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
     * 在WebClient配置中配置好请求和读写超时
     * 业务处理中不再设置超时时间
     * 最后处理异常和日志输出
     */
    @GetMapping("/webclient")
    public Flux<ServerSentEvent<Map<String, String>>> getHello() {
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
