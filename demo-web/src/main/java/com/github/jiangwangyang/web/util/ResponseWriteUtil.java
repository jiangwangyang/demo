package com.github.jiangwangyang.web.util;

import jakarta.annotation.Nonnull;
import lombok.SneakyThrows;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.http.codec.ServerSentEventHttpMessageWriter;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.io.PrintWriter;
import java.util.Optional;

/**
 * HttpServletResponse写入工具类
 * 注意：该工具只负责写入响应数据，调用者需要自己设置状态码和响应头
 * 注意：该工具类是同步阻塞的，请勿在非阻塞线程中调用
 * 注意：该工具类忽略了IO异常，调用者需要自己处理异常
 * 注意：必须在Spring请求线程中调用，否则会抛出异常
 */
public final class ResponseWriteUtil {
    private ResponseWriteUtil() {
    }

    @SneakyThrows
    @Nonnull
    private static PrintWriter getWriter() {
        return Optional.ofNullable(RequestUtil.getResponse().getWriter())
                .orElseThrow();
    }

    /**
     * 将对象转换为sse格式字符串
     * @param data 要转换的对象 如果不是sse对象则会自动包装为sse对象
     * @return sse格式字符串
     * @see ServerSentEventHttpMessageWriter
     */
    @Nonnull
    private static String encodeSse(@Nonnull Object data) {
        ServerSentEvent<?> sse = data instanceof ServerSentEvent<?> _sse ? _sse : ServerSentEvent.builder(data).build();
        StringBuilder sb = new StringBuilder();
        if (sse.id() != null) {
            sb.append("id").append(':').append(sse.id()).append('\n');
        }
        if (sse.event() != null) {
            sb.append("event").append(':').append(sse.event()).append('\n');
        }
        if (sse.retry() != null) {
            sb.append("retry").append(':').append(sse.retry().toMillis()).append('\n');
        }
        if (sse.comment() != null) {
            sb.append(':').append(StringUtils.replace(sse.comment(), "\n", "\n:")).append('\n');
        }
        if (sse.data() != null) {
            sb.append("data:");
        }
        String sseText = sb.toString();
        if (sse.data() == null) {
            return sseText + "\n";
        } else if (sse.data() instanceof String text) {
            return sseText + StringUtils.replace(text, "\n", "\ndata:") + "\n\n";
        } else {
            return sseText + ObjectMapperUtil.writeValueAsString(sse.data()) + "\n\n";
        }
    }

    /**
     * 向HTTP响应写入文本内容
     * 注意：该工具类是同步阻塞的，请勿在非阻塞线程中调用
     * @param text 要写入的文本内容
     */
    public static void writeText(@Nonnull String text) {
        getWriter().write(text);
    }

    /**
     * 向HTTP响应写入单个sse内容
     * 注意：该工具类是同步阻塞的，请勿在非阻塞线程中调用
     * @param data 要写入的对象 如果不是sse对象则会自动包装为sse对象
     */
    public static void writeSse(@Nonnull Object data) {
        getWriter().write(encodeSse(data));
    }

    /**
     * 向HTTP响应写入流式文本内容
     * 会自动将Flux发布到Schedulers.boundedElastic()的可阻塞线程中写入
     * @param flux 要写入的流式文本内容
     */
    public static void writeTextFlux(@Nonnull Flux<String> flux) {
        PrintWriter writer = getWriter();
        flux.publishOn(Schedulers.boundedElastic())
                .doOnNext(writer::write)
                .blockLast();
    }

    /**
     * 向HTTP响应写入流式sse内容
     * 会自动将Flux发布到Schedulers.boundedElastic()的可阻塞线程中写入
     * @param flux 要写入流式sse内容 如果数据流不是sse对象则会自动包装为sse对象
     */
    public static <T> void writeSseFlux(@Nonnull Flux<T> flux) {
        PrintWriter writer = getWriter();
        flux.publishOn(Schedulers.boundedElastic())
                .map(ResponseWriteUtil::encodeSse)
                .doOnNext(writer::write)
                .blockLast();
    }
}
