package com.github.jiangwangyang.web.record;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.ToString;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

/**
 * 创建任务执行器，用于执行任务并记录任务执行时间
 * 包含Runnable、Callable、Supplier
 * @param <T> 任务返回值类型
 */
@ToString(exclude = {"task"})
public class RecordTask<T> implements Runnable, Callable<T>, Supplier<T> {
    private final Callable<T> task;
    @Getter
    private final LocalDateTime createTime = LocalDateTime.now();
    @Getter
    private volatile LocalDateTime startTime;
    @Getter
    private volatile LocalDateTime endTime;
    @Getter
    private volatile Long waitTimeMillis;
    @Getter
    private volatile Long executeTimeMillis;
    @Getter
    private volatile T result;
    @Getter
    private volatile Exception exception;

    public RecordTask(Runnable task) {
        this.task = () -> {
            task.run();
            return null;
        };
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
        if (startTime != null) {
            throw new IllegalStateException("任务不可多次执行");
        }
        startTime = LocalDateTime.now();
        try {
            result = task.call();
            return result;
        } catch (Exception e) {
            exception = e;
            throw e;
        } finally {
            endTime = LocalDateTime.now();
            waitTimeMillis = startTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() - createTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            executeTimeMillis = endTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() - startTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
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
}
