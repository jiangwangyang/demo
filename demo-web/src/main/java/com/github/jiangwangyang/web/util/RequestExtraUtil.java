package com.github.jiangwangyang.web.util;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * 请求额外信息工具类
 */
public final class RequestExtraUtil {
    public static final String EXTRA_ATTRIBUTE_NAME = "extra";
    @Getter
    private final HttpServletRequest request;

    private RequestExtraUtil(HttpServletRequest request) {
        this.request = request;
    }

    /**
     * 获取请求额外信息工具类对象 在请求线程调用可直接封装request对象
     * @return 请求额外信息工具类对象
     */
    public static RequestExtraUtil of() {
        return new RequestExtraUtil(RequestUtil.getRequest());
    }

    /**
     * 获取请求额外信息工具类对象 在非请求线程调用
     * 由于RequestExtraUtil只能在当前请求线程使用，如果要在别的线程使用，则必须主动注入request对象
     * @param request 请求对象
     * @return 请求额外信息工具类对象
     */
    public static RequestExtraUtil of(HttpServletRequest request) {
        return new RequestExtraUtil(request);
    }

    @Nullable
    public <T> T getExtra(@Nonnull String name) {
        return (T) getExtraMap().get(name);
    }

    public void setExtra(@Nonnull String name, @Nonnull Object value) {
        getExtraMap().put(name, value);
    }

    @Nonnull
    public Map<String, Object> getExtraMap() {
        if (request.getAttribute(EXTRA_ATTRIBUTE_NAME) == null) {
            request.setAttribute(EXTRA_ATTRIBUTE_NAME, new HashMap<>());
        }
        return (Map<String, Object>) request.getAttribute(EXTRA_ATTRIBUTE_NAME);
    }

}
