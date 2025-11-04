package com.github.jiangwangyang.web.util;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * 请求额外信息工具类
 * 注意：必须在Spring请求线程中调用，否则会抛出异常
 */
public class RequestExtraUtil {

    public static final String EXTRA_ATTRIBUTE_NAME = "extra";

    private RequestExtraUtil() {
    }

    @Nullable
    public static <T> T getExtra(@Nonnull String name) {
        return (T) getExtraMap().get(name);
    }

    public static void setExtra(@Nonnull String name, @Nonnull Object value) {
        getExtraMap().put(name, value);
    }

    @Nonnull
    public static Map<String, Object> getExtraMap() {
        if (RequestUtil.getAttribute(EXTRA_ATTRIBUTE_NAME) == null) {
            RequestUtil.setAttribute(EXTRA_ATTRIBUTE_NAME, new HashMap<>());
        }
        return RequestUtil.getAttribute(EXTRA_ATTRIBUTE_NAME);
    }

    /**
     * 获取请求额外信息工具类包装对象
     * 由于RequestExtraUtil只能在当前请求线程使用，如果要在别的线程使用，则必须主动注入request对象
     * @param request 请求对象
     * @return 请求额外信息工具类包装对象
     */
    public static RequestExtraUtilWrapper of(HttpServletRequest request) {
        return new RequestExtraUtilWrapper(request);
    }

    /**
     * 请求额外信息工具类包装对象
     * 由于RequestExtraUtil只能在当前请求线程使用，如果要在别的线程使用，则必须主动注入request对象
     */
    public static final class RequestExtraUtilWrapper {
        @Getter
        private final HttpServletRequest request;

        private RequestExtraUtilWrapper(HttpServletRequest request) {
            this.request = request;
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
}
