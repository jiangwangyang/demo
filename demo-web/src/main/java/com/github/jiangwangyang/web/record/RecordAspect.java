package com.github.jiangwangyang.web.record;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

/**
 * 记录被 @Recordable 注解标记的类或方法的执行情况
 * 1.记录方法信息
 * 2.记录执行信息
 * @see Recordable
 */
@Aspect
public class RecordAspect {

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 切入点：匹配所有被 @Recordable 注解的类中的所有方法
     * @see Recordable
     */
    @Pointcut("@within(com.github.jiangwangyang.web.record.Recordable)")
    public void recordableClassPointcut() {
        // 切入点定义，无实际代码
    }

    /**
     * 切入点：匹配所有被 @Recordable 注解的方法
     * @see Recordable
     */
    @Pointcut("@annotation(com.github.jiangwangyang.web.record.Recordable)")
    public void recordableMethodPointcut() {
        // 切入点定义，无实际代码
    }

    /**
     * 环绕通知：在目标方法执行前后执行
     * 1.记录方法信息
     * 2.记录执行信息
     * @param proceedingJoinPoint 环绕通知的连接点，用于执行目标方法
     * @return 目标方法的返回值
     */
    @Around("recordableClassPointcut() || recordableMethodPointcut()")
    public Object around(ProceedingJoinPoint proceedingJoinPoint) {
        // 方法信息
        MethodRecord methodRecord = new MethodRecord(
                proceedingJoinPoint.getTarget().getClass().getName(),
                proceedingJoinPoint.getSignature().getName(),
                Arrays.stream(proceedingJoinPoint.getArgs())
                        .map(arg -> {
                            if (arg == null) {
                                return null;
                            }
                            try {
                                return objectMapper.writeValueAsString(arg);
                            } catch (JsonProcessingException e) {
                                return arg.toString();
                            }
                        })
                        .toList()
        );
        // 执行记录对象
        RecordTask<Object> recordTask = new RecordTask<>(new Supplier<>() {
            @SneakyThrows
            @Override
            public Object get() {
                return proceedingJoinPoint.proceed();
            }
        });
        // 执行并记录信息
        try {
            return recordTask.get();
        } finally {
            RequestRecordUtil.record(methodRecord + " " + recordTask.getTaskRecord());
        }
    }

    /**
     * 方法信息记录对象
     * @param className  类名
     * @param methodName 方法名
     * @param args       参数列表
     */
    public record MethodRecord(
            String className,
            String methodName,
            List<String> args
    ) {
    }
}
