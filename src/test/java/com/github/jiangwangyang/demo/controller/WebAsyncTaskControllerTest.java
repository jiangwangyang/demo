package com.github.jiangwangyang.demo.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 无法正确测试超时！
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WebAsyncTaskControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void testWebAsyncTask() {
        ResponseEntity<?> response = restTemplate.getForEntity("/webAsyncTask", Map.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isInstanceOf(Map.class).isEqualTo(Map.of("data", "webAsyncTask"));
    }

    @Test
    void testWebAsyncTaskError() {
        ResponseEntity<?> response = restTemplate.getForEntity("/webAsyncTask/error", Map.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isInstanceOf(Map.class).isEqualTo(Map.of("error", "webAsyncTask异常"));
    }

}
