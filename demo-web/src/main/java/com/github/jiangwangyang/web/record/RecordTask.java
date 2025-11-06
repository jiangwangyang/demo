package com.github.jiangwangyang.web.record;

import lombok.SneakyThrows;

import java.text.MessageFormat;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

/**
 * 创建任务执行器，用于执行任务并记录任务执行时间
 * 包含Runnable、Callable、Supplier
 * 注意：recordMap需要是线程安全的集合
 * 注意：taskName必须是全局唯一的
 * @param <T> 任务返回值类型
 */
public final class RecordTask<T> {
    private final Map<String, Object> recordMap;
    private final String taskName;
    private final Supplier<T> task;
    private final long createTimeMillis = System.currentTimeMillis();

    private RecordTask(Map<String, Object> recordMap, String taskName, Supplier<T> task) {
        this.recordMap = recordMap;
        this.taskName = taskName;
        this.task = task;
    }

    public static Runnable ofRunnable(Map<String, Object> recordMap, String taskName, Runnable task) {
        RecordTask<Void> recordTask = new RecordTask<>(recordMap, taskName, () -> {
            task.run();
            return null;
        });
        return recordTask::execute;
    }

    public static <T> Callable<T> ofCallable(Map<String, Object> recordMap, String taskName, Callable<T> task) {
        RecordTask<T> recordTask = new RecordTask<>(recordMap, taskName, new Supplier<>() {
            @SneakyThrows
            @Override
            public T get() {
                return task.call();
            }
        });
        return recordTask::execute;
    }

    public static <T> Supplier<T> ofSupplier(Map<String, Object> recordMap, String taskName, Supplier<T> task) {
        RecordTask<T> recordTask = new RecordTask<>(recordMap, taskName, task);
        return recordTask::execute;
    }

    @SuppressWarnings("unchecked")
    public T execute() {
        long startTimeMillis = System.currentTimeMillis();
        Object result = null;
        try {
            result = task.get();
            return (T) result;
        } catch (Exception e) {
            result = e;
            throw e;
        } finally {
            long endTimeMillis = System.currentTimeMillis();
            long waitTimeMillis = startTimeMillis - createTimeMillis;
            long executeTimeMillis = endTimeMillis - startTimeMillis;
            recordMap.put(taskName, MessageFormat.format(
                    "execute_time: {0}, wait_time: {1}, create_time: {2}, start_time: {3}, end_time: {4}, result: {5}",
                    executeTimeMillis, waitTimeMillis, createTimeMillis, startTimeMillis, endTimeMillis, result
            ));
        }
    }
}
