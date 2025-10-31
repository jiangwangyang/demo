package com.github.jiangwangyang.demo.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Field;
import java.util.Set;

/**
 * @deprecated 建议只使用FLux
 * 包装SseEmitter
 * 增加complete状态
 * 结束后再send会抛出异常 现已捕获
 * 超时后再complete会抛出异常 现已捕获
 * 结束后或超时后再completeWithError不会抛出异常
 * 不建议使用completeWithError方法 异常会被ControllerAdvice捕获 但如果之前已经发过数据 则不可再返回数据
 * 推荐使用send+complete方法 或者自定义异常 并在ControllerAdvice中不返回数据
 */
@Deprecated
@Slf4j
public class SseEmitterWrapper extends SseEmitter {

    private static final VarHandle COMPLETE_HANDLE;

    static {
        try {
            Field field = ResponseBodyEmitter.class.getDeclaredField("complete");
            field.setAccessible(true);
            COMPLETE_HANDLE = MethodHandles.privateLookupIn(ResponseBodyEmitter.class, MethodHandles.lookup()).unreflectVarHandle(field);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            log.error("SseEmitterWrapper init error: {}", e.getMessage());
            throw new ExceptionInInitializerError(e);
        }
    }

    public SseEmitterWrapper() {
        super();
    }

    public SseEmitterWrapper(Long timeout) {
        super(timeout);
    }

    public boolean isComplete() {
        return (boolean) COMPLETE_HANDLE.getVolatile(this);
    }

    @Override
    public void send(@NonNull Object object) {
        this.send(object, null);
    }

    @Override
    public void send(@NonNull Object object, @Nullable MediaType mediaType) {
        this.send(event().data(object, mediaType));
    }

    @Override
    public void send(SseEventBuilder builder) {
        Set<ResponseBodyEmitter.DataWithMediaType> dataToSend = builder.build();
        synchronized (this) {
            this.send(dataToSend);
        }
    }

    @Override
    public synchronized void send(@NonNull Set<DataWithMediaType> items) {
        try {
            super.send(items);
        } catch (IOException e) {
            log.warn("SseEmitterWrapper.send() IOException: {}", e.getMessage());
        } catch (IllegalStateException e) {
            log.warn("SseEmitterWrapper.send() IllegalStateException: {}", e.getMessage());
        }
    }

    @Override
    public synchronized void complete() {
        try {
            super.complete();
        } catch (IllegalStateException e) {
            log.warn("SseEmitterWrapper.complete() IllegalStateException: {}", e.getMessage());
        }
    }

}
