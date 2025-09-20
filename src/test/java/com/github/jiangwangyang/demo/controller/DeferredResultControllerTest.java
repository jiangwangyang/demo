package com.github.jiangwangyang.demo.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DeferredResultControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void testDeferred() {
        ResponseEntity<?> response = restTemplate.getForEntity("/deferred", Map.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isInstanceOf(Map.class).isEqualTo(Map.of("data", "deferred"));
    }

    @Test
    void testDeferredTimeout() {
        ResponseEntity<?> response = restTemplate.getForEntity("/deferred/timeout", Map.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isInstanceOf(Map.class).isEqualTo(Map.of("timeout", "异步请求超时"));
    }

}
