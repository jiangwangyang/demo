package com.github.jiangwangyang.web.record;

import com.github.jiangwangyang.web.util.RequestExtraUtil;
import com.github.jiangwangyang.web.util.RequestUtil;
import lombok.SneakyThrows;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Supplier;

/**
 * 记录控制器方法的执行情况
 * 1.记录请求METHOD
 * 2.记录请求URI
 * 3.记录控制器参数
 * 4.记录控制器执行时间
 */
@Aspect
public class ControllerRecordAspect {
    public static final String RECORD_REQUEST_METHOD_KEY = "request_method";
    public static final String RECORD_REQUEST_URI_KEY = "request_uri";
    public static final String RECORD_CONTROLLER_PARAMS_KEY = "controller_params";
    public static final String RECORD_CONTROLLER_EXECUTE_TIME_KEY = "controller_execute_time";

    /**
     * 切入点：匹配所有被 @Controller注解的类中的所有方法
     * @see Controller
     */
    @Pointcut("@within(org.springframework.stereotype.Controller)")
    public void controllerPointcut() {
        // 切入点定义，无实际代码
    }

    /**
     * 切入点：匹配所有被 @RestController 注解的类中的所有方法
     * @see RestController
     */
    @Pointcut("@within(org.springframework.web.bind.annotation.RestController)")
    public void restControllerPointcut() {
        // 切入点定义，无实际代码
    }

    /**
     * 前置通知：在目标方法执行前执行
     * 1.记录请求METHOD
     * 2.记录请求URI
     * 3.记录控制器参数
     */
    @Before("controllerPointcut() || restControllerPointcut()")
    public void before(JoinPoint joinPoint) {
        Map<String, Object> recordMap = RequestExtraUtil.of().getExtraMap();
        recordMap.put(RECORD_REQUEST_METHOD_KEY, RequestUtil.getRequest().getMethod());
        recordMap.put(RECORD_REQUEST_URI_KEY, RequestUtil.getRequest().getRequestURI());
        recordMap.put(RECORD_CONTROLLER_PARAMS_KEY, Arrays.stream(joinPoint.getArgs()).toList().toString());
    }

    /**
     * 环绕通知：在目标方法执行前后执行
     * 4.记录控制器执行时间
     * @param proceedingJoinPoint 环绕通知的连接点，用于执行目标方法
     * @return 目标方法的返回值
     */
    @Around("controllerPointcut() || restControllerPointcut()")
    public Object around(ProceedingJoinPoint proceedingJoinPoint) {
        return RecordTask.ofSupplier(
                RequestExtraUtil.of().getExtraMap(),
                RECORD_CONTROLLER_EXECUTE_TIME_KEY,
                new Supplier<>() {
                    @SneakyThrows
                    @Override
                    public Object get() {
                        return proceedingJoinPoint.proceed();
                    }
                }
        ).get();
    }
}
