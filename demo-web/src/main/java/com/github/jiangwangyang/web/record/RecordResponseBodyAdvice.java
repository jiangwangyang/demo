package com.github.jiangwangyang.web.record;

import com.github.jiangwangyang.web.response.Response;
import com.github.jiangwangyang.web.util.RequestUtil;
import jakarta.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 响应体增强器
 * 将请求中的extra属性添加到响应体中
 * 记录日志
 */
@ControllerAdvice
@Slf4j
public class RecordResponseBodyAdvice implements ResponseBodyAdvice<Response<?>> {
    @Override
    public boolean supports(@Nonnull MethodParameter returnType, @Nonnull Class<? extends HttpMessageConverter<?>> converterType) {
        return Response.class.isAssignableFrom(returnType.getParameterType());
    }

    @Override
    public Response<?> beforeBodyWrite(Response<?> body, @Nonnull MethodParameter returnType, @Nonnull MediaType selectedContentType, @Nonnull Class<? extends HttpMessageConverter<?>> selectedConverterType, @Nonnull ServerHttpRequest request, @Nonnull ServerHttpResponse response) {
        if (body == null) {
            return null;
        }
        List<String> recordList = RequestRecordUtil.getRecordList();
        if (!recordList.isEmpty()) {
            if (body.getExtra() == null) {
                body.setExtra(new ConcurrentHashMap<>());
            }
            body.getExtra().put(RequestRecordUtil.RECORD_KEY, recordList);
            log.info("{} {} {}", RequestUtil.getRequest().getMethod(), RequestUtil.getRequest().getRequestURI(), recordList);
        }
        return body;
    }
}
