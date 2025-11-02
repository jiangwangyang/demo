package com.github.jiangwangyang.demo.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * WebClient配置
 */
@Configuration
public class WebClientConfig {

    // 连接池连接数
    private static final int MAX_CONNECTION = 100;
    // 连接超时时间
    private static final int CONNECT_TIMEOUT_MILLIS = 2000;
    // 整个响应超时时间
    private static final int RESPONSE_TIMEOUT_MILLIS = 30_000;
    // 读取超时时间（两次读取间隔）
    private static final int READ_TIMEOUT_MILLIS = 5000;
    // 写入超时时间（两次写入间隔）
    private static final int WRITE_TIMEOUT_MILLIS = 5000;

    @Bean
    public WebClient webClient() {
        ConnectionProvider provider = ConnectionProvider.builder("web-client")
                .maxConnections(MAX_CONNECTION)
                .build();
        HttpClient httpClient = HttpClient.create(provider)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, CONNECT_TIMEOUT_MILLIS)
                .responseTimeout(Duration.ofMillis(RESPONSE_TIMEOUT_MILLIS))
                .doOnConnected(connection -> connection
                        .addHandlerLast(new ReadTimeoutHandler(READ_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS))
                        .addHandlerLast(new WriteTimeoutHandler(WRITE_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)));
        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}
