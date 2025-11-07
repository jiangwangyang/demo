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
import java.util.concurrent.ThreadPoolExecutor;
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

    public static void record(@Nonnull Object object) {
        record(getRecordList(), getCallerStackTrace(), object);
    }

    public static void recordRunSync(@Nonnull Runnable runnable) {
        List<String> recordList = getRecordList();
        StackTraceElement stackTraceElement = getCallerStackTrace();
        RecordTask<Void> recordTask = new RecordTask<>(runnable);
        try {
            recordTask.run();
        } finally {
            record(recordList, stackTraceElement, recordTask);
        }
    }

    public static <U> U recordSupplySync(@Nonnull Supplier<U> supplier) {
        List<String> recordList = getRecordList();
        StackTraceElement stackTraceElement = getCallerStackTrace();
        RecordTask<U> recordTask = new RecordTask<>(supplier);
        try {
            return recordTask.get();
        } finally {
            record(recordList, stackTraceElement, recordTask);
        }
    }

    public static CompletableFuture<Void> recordRunAsync(@Nonnull Runnable runnable, @Nonnull Executor executor) {
        List<String> recordList = getRecordList();
        StackTraceElement stackTraceElement = getCallerStackTrace();
        RecordTask<Void> recordTask = new RecordTask<>(runnable);
        // 记录线程池信息
        if (executor instanceof ThreadPoolExecutor threadPool) {
            record(recordList, stackTraceElement, new ThreadPoolRecord(
                    threadPool.getActiveCount(),
                    threadPool.getPoolSize(),
                    threadPool.getCorePoolSize(),
                    threadPool.getMaximumPoolSize(),
                    threadPool.getQueue().size(),
                    threadPool.getQueue().remainingCapacity()
            ));
        }
        // 异步执行任务，并在完成后记录结果
        return CompletableFuture.runAsync(() -> {
            try {
                recordTask.run();
            } finally {
                record(recordList, stackTraceElement, recordTask);
            }
        }, executor);
    }

    public static <U> CompletableFuture<U> recordSupplyAsync(@Nonnull Supplier<U> supplier, @Nonnull Executor executor) {
        List<String> recordList = getRecordList();
        StackTraceElement stackTraceElement = getCallerStackTrace();
        RecordTask<U> recordTask = new RecordTask<>(supplier);
        // 记录线程池信息
        if (executor instanceof ThreadPoolExecutor threadPool) {
            record(recordList, stackTraceElement, new ThreadPoolRecord(
                    threadPool.getActiveCount(),
                    threadPool.getPoolSize(),
                    threadPool.getCorePoolSize(),
                    threadPool.getMaximumPoolSize(),
                    threadPool.getQueue().size(),
                    threadPool.getQueue().remainingCapacity()
            ));
        }
        // 异步执行任务，并在完成后记录结果
        return CompletableFuture.supplyAsync(() -> {
            try {
                return recordTask.get();
            } finally {
                record(recordList, stackTraceElement, recordTask);
            }
        }, executor);
    }

    /**
     * 按照特定格式记录信息
     * @param recordList 记录列表
     * @param object     要记录的对象
     */
    private static void record(@Nonnull List<String> recordList, @Nonnull StackTraceElement stackTraceElement, @Nonnull Object object) {
        recordList.add(MessageFormat.format(
                "{0} {1} {2}",
                LocalDateTime.now(), stackTraceElement, object
        ));
    }

    /**
     * 获取调用者的栈信息 从栈中找到第一个不是当前工具类的调用者
     * @return 调用者的栈信息
     */
    @Nonnull
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

    /**
     * 线程池记录信息
     * @param activeCount            活跃线程数
     * @param poolSize               当前线程池大小
     * @param corePoolSize           核心线程数
     * @param maxPoolSize            最大线程数
     * @param queueSize              队列大小
     * @param queueRemainingCapacity 队列剩余容量
     */
    private record ThreadPoolRecord(
            Integer activeCount,
            Integer poolSize,
            Integer corePoolSize,
            Integer maxPoolSize,
            Integer queueSize,
            Integer queueRemainingCapacity
    ) {
    }
}
