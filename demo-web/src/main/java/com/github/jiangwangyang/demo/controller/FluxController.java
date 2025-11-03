package com.github.jiangwangyang.demo.controller;

import com.github.jiangwangyang.web.util.RequestUtil;
import com.github.jiangwangyang.web.util.ResponseWriteUtil;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.Map;

@RestController
@RequestMapping("/flux")
public class FluxController {

    @GetMapping
    public void flux() {
        RequestUtil.getResponse().setContentType("text/event-stream");
        Flux<?> flux = Flux.just(
                "hello",
                "world",
                Map.of("hello", "world"),
                ServerSentEvent.builder("hello").build(),
                ServerSentEvent.builder("world").build(),
                ServerSentEvent.builder(Map.of("hello", "world")).build()
        );
        ResponseWriteUtil.writeSseFlux(flux);
    }
}
