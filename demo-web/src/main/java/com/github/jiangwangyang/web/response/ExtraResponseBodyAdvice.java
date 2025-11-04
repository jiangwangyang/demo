package com.github.jiangwangyang.web.response;

import com.github.jiangwangyang.web.util.RequestUtil;
import jakarta.annotation.Nonnull;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.Map;

/**
 * 响应体增强器
 * 将请求中的extra属性添加到响应体中
 */
@ControllerAdvice
public class ExtraResponseBodyAdvice implements ResponseBodyAdvice<Response<?>> {
    @Override
    public boolean supports(@Nonnull MethodParameter returnType, @Nonnull Class<? extends HttpMessageConverter<?>> converterType) {
        return Response.class.isAssignableFrom(returnType.getParameterType());
    }

    @Override
    public Response<?> beforeBodyWrite(Response<?> body, @Nonnull MethodParameter returnType, @Nonnull MediaType selectedContentType, @Nonnull Class<? extends HttpMessageConverter<?>> selectedConverterType, @Nonnull ServerHttpRequest request, @Nonnull ServerHttpResponse response) {
        Map<String, Object> extraMap = RequestUtil.getAttribute("extra");
        if (extraMap != null) {
            if (body.getExtra() == null) {
                body.setExtra(extraMap);
            } else {
                for (Map.Entry<String, Object> entry : extraMap.entrySet()) {
                    body.getExtra().putIfAbsent(entry.getKey(), entry.getValue());
                }
            }
        }
        return body;
    }
}
