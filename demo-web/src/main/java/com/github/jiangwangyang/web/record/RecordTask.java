package com.github.jiangwangyang.web.record;

import lombok.Getter;
import lombok.SneakyThrows;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

/**
 * 创建任务执行器，用于执行任务并记录任务执行时间
 * 包含Runnable、Callable、Supplier
 * @param <T> 任务返回值类型
 */
public class RecordTask<T> implements Runnable, Callable<T>, Supplier<T> {
    private final LocalDateTime createTime = LocalDateTime.now();
    private final AtomicBoolean executed = new AtomicBoolean(false);
    private final Callable<T> task;
    @Getter
    private volatile TaskRecord<T> taskRecord;

    public RecordTask(Runnable task) {
        this.task = Executors.callable(task, null);
    }

    public RecordTask(Callable<T> task) {
        this.task = task;
    }

    public RecordTask(Supplier<T> task) {
        this.task = task::get;
    }

    /**
     * 执行任务并记录任务执行时间
     * @return 任务返回值
     */
    @SneakyThrows
    public T execute() {
        if (!executed.compareAndSet(false, true)) {
            throw new IllegalStateException("RecordTask不可多次执行");
        }
        LocalDateTime startTime = LocalDateTime.now();
        T result = null;
        Exception exception = null;
        try {
            result = task.call();
            return result;
        } catch (Exception e) {
            exception = e;
            throw e;
        } finally {
            LocalDateTime endTime = LocalDateTime.now();
            long waitTimeMillis = startTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() - createTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            long executeTimeMillis = endTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() - startTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            taskRecord = new TaskRecord<>(createTime, startTime, endTime, waitTimeMillis, executeTimeMillis, result, exception);
        }
    }

    @Override
    public void run() {
        execute();
    }

    @Override
    public T call() {
        return execute();
    }

    @Override
    public T get() {
        return execute();
    }

    /**
     * 任务执行信息记录类
     * @param createTime        创建时间
     * @param startTime         开始时间
     * @param endTime           结束时间
     * @param waitTimeMillis    等待时间 (毫秒)
     * @param executeTimeMillis 执行时间 (毫秒)
     * @param result            任务返回值
     * @param exception         任务异常信息
     * @param <T>
     */
    public record TaskRecord<T>(
            LocalDateTime createTime,
            LocalDateTime startTime,
            LocalDateTime endTime,
            Long waitTimeMillis,
            Long executeTimeMillis,
            T result,
            Exception exception
    ) {
    }
}
