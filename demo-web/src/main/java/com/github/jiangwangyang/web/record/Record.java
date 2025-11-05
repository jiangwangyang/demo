package com.github.jiangwangyang.web.record;

import lombok.SneakyThrows;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

/**
 * 创建任务执行器，用于执行任务并记录任务执行时间
 * 包含Runnable、Callable、Supplier
 * 注意：recordMap需要是线程安全的集合
 * 注意：taskName必须是全局唯一的
 */
public final class Record {

    public static Runnable of(Map<String, Object> recordMap, String taskName, Runnable task) {
        RecordTask<Void> recordTask = new RecordTask<>(recordMap, taskName, () -> {
            task.run();
            return null;
        });
        return recordTask::execute;
    }

    public static <T> Callable<T> of(Map<String, Object> recordMap, String taskName, Callable<T> task) {
        RecordTask<T> recordTask = new RecordTask<>(recordMap, taskName, new Supplier<>() {
            @SneakyThrows
            @Override
            public T get() {
                return task.call();
            }
        });
        return recordTask::execute;
    }

    public static <T> Supplier<T> of(Map<String, Object> recordMap, String taskName, Supplier<T> task) {
        RecordTask<T> recordTask = new RecordTask<>(recordMap, taskName, task);
        return recordTask::execute;
    }

    /**
     * 任务执行器，用于执行任务并记录任务执行时间
     * @param <T> 任务返回值类型
     */
    private static final class RecordTask<T> {
        public static final String RECORD_TASK_CREATE_TIME_KEY_SUFFIX = "_create_time";
        public static final String RECORD_TASK_START_TIME_KEY_SUFFIX = "_start_time";
        public static final String RECORD_TASK_END_TIME_KEY_SUFFIX = "_end_time";
        public static final String RECORD_TASK_WAIT_TIME_KEY_SUFFIX = "_wait_time";
        public static final String RECORD_TASK_EXECUTE_TIME_KEY_SUFFIX = "_execute_time";

        private final Map<String, Object> recordMap;
        private final String taskName;
        private final Supplier<T> task;
        private final long createTimeMillis;

        RecordTask(Map<String, Object> recordMap, String taskName, Supplier<T> task) {
            this.recordMap = recordMap;
            this.taskName = taskName;
            this.task = task;
            this.createTimeMillis = System.currentTimeMillis();
            recordMap.put(taskName + RECORD_TASK_CREATE_TIME_KEY_SUFFIX, createTimeMillis);
        }

        public T execute() {
            long startTimeMillis = System.currentTimeMillis();
            recordMap.put(taskName + RECORD_TASK_START_TIME_KEY_SUFFIX, startTimeMillis);
            try {
                return task.get();
            } finally {
                long endTimeMillis = System.currentTimeMillis();
                long waitTimeMillis = startTimeMillis - createTimeMillis;
                long executeTimeMillis = endTimeMillis - startTimeMillis;
                recordMap.put(taskName + RECORD_TASK_END_TIME_KEY_SUFFIX, endTimeMillis);
                recordMap.put(taskName + RECORD_TASK_WAIT_TIME_KEY_SUFFIX, waitTimeMillis);
                recordMap.put(taskName + RECORD_TASK_EXECUTE_TIME_KEY_SUFFIX, executeTimeMillis);
            }
        }
    }
}
