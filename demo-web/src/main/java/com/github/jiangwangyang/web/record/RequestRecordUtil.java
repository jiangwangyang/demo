package com.github.jiangwangyang.web.record;

import jakarta.annotation.Nonnull;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

/**
 * 请求记录工具类
 * 注意：必须在Spring请求线程中调用，否则会抛出异常
 * @see RequestContextHolder
 */
public final class RequestRecordUtil {
    public static final String RECORD_KEY = "record";

    private RequestRecordUtil() {
    }

    @Nonnull
    @SuppressWarnings("unchecked")
    public static List<String> getRecordList() {
        HttpServletRequest request = Optional
                .ofNullable((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .map(ServletRequestAttributes::getRequest)
                .orElseThrow();
        List<String> recordList = (List<String>) request.getAttribute(RECORD_KEY);
        if (recordList == null) {
            recordList = new CopyOnWriteArrayList<>();
            request.setAttribute(RECORD_KEY, recordList);
        }
        return recordList;
    }

    public static void record(@Nonnull String text) {
        List<String> recordList = getRecordList();
        StackTraceElement stackTraceElement = getCallerStackTrace();
        recordList.add(MessageFormat.format(
                "{0} {1} {2}",
                LocalDateTime.now(), stackTraceElement, text
        ));
    }

    public static void recordRunSync(@Nonnull Runnable runnable) {
        List<String> recordList = getRecordList();
        StackTraceElement stackTraceElement = getCallerStackTrace();
        RecordTask<Void> recordTask = new RecordTask<>(runnable);
        try {
            recordTask.run();
        } finally {
            recordList.add(MessageFormat.format(
                    "{0} {1} {2}",
                    LocalDateTime.now(), stackTraceElement, recordTask
            ));
        }
    }

    public static <U> U recordSupplySync(@Nonnull Supplier<U> supplier) {
        List<String> recordList = getRecordList();
        StackTraceElement stackTraceElement = getCallerStackTrace();
        RecordTask<U> recordTask = new RecordTask<>(supplier);
        try {
            return recordTask.get();
        } finally {
            recordList.add(MessageFormat.format(
                    "{0} {1} {2}",
                    LocalDateTime.now(), stackTraceElement, recordTask
            ));
        }
    }

    public static CompletableFuture<Void> recordRunAsync(@Nonnull Runnable runnable, @Nonnull Executor executor) {
        List<String> recordList = getRecordList();
        StackTraceElement stackTraceElement = getCallerStackTrace();
        RecordTask<Void> recordTask = new RecordTask<>(runnable);
        return CompletableFuture.runAsync(() -> {
            try {
                recordTask.run();
            } finally {
                recordList.add(MessageFormat.format(
                        "{0} {1} {2}",
                        LocalDateTime.now(), stackTraceElement, recordTask
                ));
            }
        }, executor);
    }

    public static <U> CompletableFuture<U> recordSupplyAsync(@Nonnull Supplier<U> supplier, @Nonnull Executor executor) {
        List<String> recordList = getRecordList();
        StackTraceElement stackTraceElement = getCallerStackTrace();
        RecordTask<U> recordTask = new RecordTask<>(supplier);
        return CompletableFuture.supplyAsync(() -> {
            try {
                return recordTask.get();
            } finally {
                recordList.add(MessageFormat.format(
                        "{0} {1} {2}",
                        LocalDateTime.now(), stackTraceElement, recordTask
                ));
            }
        }, executor);
    }

    /**
     * 获取调用者的栈信息 从栈中找到第一个不是当前工具类的调用者
     * @return 调用者的栈信息
     */
    private static StackTraceElement getCallerStackTrace() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for (StackTraceElement stackTraceElement : stackTrace) {
            String className = stackTraceElement.getClassName();
            if (!className.equals(RequestRecordUtil.class.getName())
                    && !className.startsWith("java.")
                    && !className.startsWith("jdk.")) {
                return stackTraceElement;
            }
        }
        return stackTrace[stackTrace.length - 1];
    }
}
