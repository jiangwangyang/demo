package com.github.jiangwangyang.demo.common.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

@Component
public final class ObjectMapperUtil {

    @Getter
    private static ObjectMapper objectMapper;

    public ObjectMapperUtil(ObjectMapper objectMapper) {
        ObjectMapperUtil.objectMapper = objectMapper;
    }

    @SneakyThrows
    public static String writeValueAsString(Object object) {
        return objectMapper.writeValueAsString(object);
    }

    @SneakyThrows
    public static <T> T readValue(String content, Class<T> valueType) {
        return objectMapper.readValue(content, valueType);
    }

    @SneakyThrows
    public static <T> T readValue(String content, TypeReference<T> valueTypeRef) {
        return objectMapper.readValue(content, valueTypeRef);
    }

    @SneakyThrows
    public static <T> T readValue(String content, JavaType valueType) {
        return objectMapper.readValue(content, valueType);
    }
}
