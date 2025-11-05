package com.github.jiangwangyang.web.util;

import jakarta.annotation.Nonnull;
import lombok.Getter;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Spring 上下文工具类
 * 需要注入Spring容器初始化ApplicationContext
 */
public final class SpringContextUtil implements ApplicationContextAware {

    @Getter
    private static ApplicationContext applicationContext;

    public static Object getBean(String name) {
        return applicationContext.getBean(name);
    }

    public static <T> T getBean(String name, Class<T> requiredType) {
        return applicationContext.getBean(name, requiredType);
    }

    public static Object getBean(String name, Object... args) {
        return applicationContext.getBean(name, args);
    }

    public static <T> T getBean(Class<T> requiredType) {
        return applicationContext.getBean(requiredType);
    }

    public static <T> T getBean(Class<T> requiredType, Object... args) {
        return applicationContext.getBean(requiredType, args);
    }

    @Override
    public void setApplicationContext(@Nonnull ApplicationContext applicationContext) throws BeansException {
        SpringContextUtil.applicationContext = applicationContext;
    }
}
