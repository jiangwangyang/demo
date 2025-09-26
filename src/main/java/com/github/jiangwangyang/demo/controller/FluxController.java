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
import java.util.concurrent.TimeoutException;

/**
 * 流式数据推荐使用WebFlux或Emitter
 */
@RestController
@Slf4j
public class FluxController {

    /**
     * 推荐写法
     * 不推荐用take设置超时 因为不能自定义响应结果
     * timeout 设置数据间隔超时 应写在业务处理之前
     * 业务处理
     * onErrorResume TimeoutException 处理超时异常，返回默认值
     * onErrorResume 处理其它异常，返回默认值
     * doOnSubscribe 记录订阅事件
     * doFinally 记录结束事件
     */
    @GetMapping("/flux")
    public Flux<ServerSentEvent<Map<String, String>>> flux(HttpServletResponse response) {
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        return Flux.range(0, 10)
                .delayElements(Duration.ofMillis(100))
                // 先对上游数据设置超时 下游业务时间应由业务代码自己把控
                .timeout(Duration.ofMillis(1000))
                // 然后再对上游数据进行业务处理 超时不应写在业务逻辑之后
                .map(i -> ServerSentEvent.builder(Map.of("data", "/flux")).build())
                // 最后做异常处理和日志输出
                .onErrorResume(TimeoutException.class, e -> {
                    log.error("flux timeout: {}", e.getMessage());
                    return Flux.just(ServerSentEvent.builder(Map.of("timeout", "/flux")).build());
                })
                .onErrorResume(e -> {
                    log.error("flux error", e);
                    return Flux.just(ServerSentEvent.builder(Map.of("error", "/flux")).build());
                })
                .doOnSubscribe(s -> log.info("/flux订阅：{}", s))
                .doFinally(signalType -> log.info("/flux结束：{}", signalType));
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
                })
                .doFinally(signalType -> log.info("/flux/error结束：{}", signalType));
    }

    /**
     * timeout指两次数据之间超时
     * timeout如果指定了返回数据，则属于onComplete正常结束，只是简单的截断数据流，不会触发任何异常
     * 如果没有指定返回数据，则属于onError异常结束，需要在onErrorResume中处理
     */
    @GetMapping("/flux/timeout")
    public Flux<ServerSentEvent<Map<String, String>>> fluxTimeout(HttpServletResponse response) {
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        return Flux.range(0, 10)
                .map(i -> ServerSentEvent.builder(Map.of("data", "/flux/timeout")).build())
                .delayElements(Duration.ofMillis(5000))
                // 指定返回数据，属于onComplete正常结束
                // .timeout(Duration.ofMillis(100), Flux.just(ServerSentEvent.builder(Map.of("timeout", "/flux/timeout")).build()))
                .timeout(Duration.ofMillis(100))
                .onErrorResume(TimeoutException.class, e -> Flux.just(ServerSentEvent.builder(Map.of("timeout", "/flux/timeout")).build()))
                .doFinally(signalType -> log.info("/flux/timeout结束：{}", signalType));
    }


    /**
     * take指整个Flux数据流超时
     * take属于onComplete正常结束，只是简单的截断数据流，不会触发任何异常
     */
    @GetMapping("/flux/take")
    public Flux<ServerSentEvent<Map<String, String>>> fluxTake(HttpServletResponse response) {
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        return Flux.range(0, 10)
                .map(i -> ServerSentEvent.builder(Map.of("data", "/flux/take")).build())
                .delayElements(Duration.ofMillis(100))
                .take(Duration.ofMillis(150))
                .doFinally(signalType -> log.info("/flux/take结束：{}", signalType));
    }

    /**
     * 在数据流中添加心跳数据
     */
    @GetMapping("/flux/heartbeat")
    public Flux<ServerSentEvent<Map<String, String>>> fluxHeartbeat(HttpServletResponse response) {
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        Flux<ServerSentEvent<Map<String, String>>> dataFlux = Flux.range(0, 10)
                .map(i -> ServerSentEvent.builder(Map.of("data", "/flux/heartbeat")).build())
                .delayElements(Duration.ofMillis(100));
        Flux<ServerSentEvent<Map<String, String>>> heartbeatFlux = Flux.interval(Duration.ZERO, Duration.ofMillis(100))
                .map(l -> ServerSentEvent.builder(Map.of("heartbeat", "")).build());
        return Flux.merge(dataFlux, heartbeatFlux)
                .takeUntilOther(dataFlux.then())
                .doFinally(signalType -> log.info("/flux/heartbeat结束：{}", signalType));
    }

    /**
     * 手工测试断开连接
     * 触发IOException
     */
    @GetMapping("/flux/terminate")
    public Flux<ServerSentEvent<Map<String, String>>> fluxTerminate(HttpServletResponse response) {
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        return Flux.range(0, 10)
                .map(i -> ServerSentEvent.builder(Map.of("data", "/flux/terminate")).build())
                .delayElements(Duration.ofMillis(1000))
                .doFinally(signalType -> log.info("/flux/terminate结束：{}", signalType));
    }

}
