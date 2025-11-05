package com.github.jiangwangyang.web.aspect;

import com.github.jiangwangyang.web.util.RequestExtraUtil;
import com.github.jiangwangyang.web.util.RequestUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

/**
 * 记录控制器方法的执行情况
 * 1.执行前记录开始时间
 * 2.执行后记录结束时间，执行时间
 * Before：方法执行前增强。
 * AfterReturning：方法正常返回后增强。
 * Around：环绕增强（可控制方法是否执行，功能最强大）。
 * AfterThrowing：方法抛出异常后增强。
 * After：方法执行完成后增强（无论正常返回还是异常）。
 */
@Aspect
public class RecordAspect {
    public static final String RECORD_REQUEST_METHOD_KEY = "request_method";
    public static final String RECORD_REQUEST_URI_KEY = "request_uri";
    public static final String RECORD_CONTROLLER_PARAMS_KEY = "controller_params";
    public static final String RECORD_CONTROLLER_EXECUTION_TIME_KEY = "controller_execution_time";

    /**
     * 切入点：匹配所有被 @Controller 或 @RestController 注解的类中的所有方法
     * 表达式说明：
     * - @within(Controller)：匹配类上有 @Controller 注解的类
     * - @within(RestController)：匹配类上有 @RestController 注解的类
     * - && execution(* *(..))：匹配上述类中的所有方法（任意返回值、任意方法名、任意参数）
     * @see Controller
     * @see RestController
     */
    @Pointcut("@within(org.springframework.stereotype.Controller) || @within(org.springframework.web.bind.annotation.RestController)")
    public void controllerPointcut() {
        // 切入点定义，无实际代码
    }

    /**
     * 前置通知：在目标方法执行前执行
     * 1.记录请求METHOD
     * 2.记录请求URI
     * 3.记录控制器参数
     */
    @Before("controllerPointcut()")
    public void before(JoinPoint joinPoint) {
        RequestExtraUtil.of().setExtra(RECORD_REQUEST_METHOD_KEY, RequestUtil.getRequest().getMethod());
        RequestExtraUtil.of().setExtra(RECORD_REQUEST_URI_KEY, RequestUtil.getRequest().getRequestURI());
        RequestExtraUtil.of().setExtra(RECORD_CONTROLLER_PARAMS_KEY, Arrays.stream(joinPoint.getArgs()).toList().toString());
    }

    /**
     * 环绕执行：在目标方法执行前后执行
     * 4.记录控制器 开始时间 结束时间 执行时间
     */
    @Around("controllerPointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTimeMillis = System.currentTimeMillis();
        try {
            return joinPoint.proceed();
        } finally {
            long endTimeMillis = System.currentTimeMillis();
            long executionTimeMillis = endTimeMillis - startTimeMillis;
            RequestExtraUtil.of().setExtra(RECORD_CONTROLLER_EXECUTION_TIME_KEY, "startTimeMillis: %d, endTimeMillis: %d, executionTimeMillis: %d".formatted(startTimeMillis, endTimeMillis, executionTimeMillis));
        }
    }
}
