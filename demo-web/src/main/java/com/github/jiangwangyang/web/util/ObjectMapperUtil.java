package com.github.jiangwangyang.web.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.SneakyThrows;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * ObjectMapper 工具类
 * 需要注入Spring容器从而初始化ObjectMapper
 */
public final class ObjectMapperUtil implements ApplicationContextAware {

    @Getter
    private static ObjectMapper objectMapper;

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

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ObjectMapperUtil.objectMapper = applicationContext.getBean(ObjectMapper.class);
    }
}
