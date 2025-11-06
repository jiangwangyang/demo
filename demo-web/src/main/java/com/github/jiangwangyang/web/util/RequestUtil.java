package com.github.jiangwangyang.web.util;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;

/**
 * 请求工具类
 * 注意：必须在Spring请求线程中调用，否则会抛出异常
 */
public final class RequestUtil {
    private RequestUtil() {
    }

    /**
     * 获取当前请求对象
     * @return 当前请求对象
     */
    @Nonnull
    public static HttpServletRequest getRequest() {
        return Optional.ofNullable((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .map(ServletRequestAttributes::getRequest)
                .orElseThrow();
    }

    /**
     * 获取当前响应对象
     * @return 当前响应对象
     */
    @Nonnull
    public static HttpServletResponse getResponse() {
        return Optional.ofNullable((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .map(ServletRequestAttributes::getResponse)
                .orElseThrow();
    }

    /**
     * 获取当前请求属性 可用于在请求线程内传递数据
     * 属性只在当前请求内有效 需要先在当前请求线程设置 请求结束后自动清除
     * @param name 属性名
     * @return 属性值
     */
    @Nullable
    @SuppressWarnings("unchecked")
    public static <T> T getAttribute(@Nonnull String name) {
        return (T) getRequest().getAttribute(name);
    }

    /**
     * 设置当前请求属性 可用于在请求线程内传递数据
     * 属性只在当前请求内有效 请求结束后自动清除
     * @param name  属性名
     * @param value 属性值
     */
    public static void setAttribute(@Nonnull String name, @Nonnull Object value) {
        getRequest().setAttribute(name, value);
    }

    /**
     * 移除当前请求属性
     * @param name 属性名
     */
    public static void removeAttribute(@Nonnull String name) {
        getRequest().removeAttribute(name);
    }
}
