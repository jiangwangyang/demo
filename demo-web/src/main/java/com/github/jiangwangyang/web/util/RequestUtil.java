package com.github.jiangwangyang.web.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

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
    public static HttpServletRequest getRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }

    /**
     * 获取当前响应对象
     * @return 当前响应对象
     */
    public static HttpServletResponse getResponse() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
    }

    /**
     * 获取当前请求属性 可用于在请求线程内传递数据
     * 属性只在当前请求内有效 需要先在当前请求线程设置 请求结束后自动清除
     * @param name 属性名
     * @return 属性值
     */
    public static <T> T getAttribute(String name) {
        return (T) getRequest().getAttribute(name);
    }

    /**
     * 设置当前请求属性 可用于在请求线程内传递数据
     * 属性只在当前请求内有效 请求结束后自动清除
     * @param name  属性名
     * @param value 属性值
     */
    public static void setAttribute(String name, Object value) {
        getRequest().setAttribute(name, value);
    }
}
