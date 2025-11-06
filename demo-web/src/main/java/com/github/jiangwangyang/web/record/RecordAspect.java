package com.github.jiangwangyang.web.record;

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

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Map;

/**
 * 记录控制器方法的执行情况
 * 1.记录请求METHOD
 * 2.记录请求URI
 * 3.记录控制器参数
 * 4.记录控制器执行时间
 */
@Aspect
public class RecordAspect {
    public static final String RECORD_REQUEST_METHOD_KEY = "request_method";
    public static final String RECORD_REQUEST_URI_KEY = "request_uri";
    public static final String RECORD_CONTROLLER_PARAMS_KEY = "controller_params";
    public static final String RECORD_CONTROLLER_EXECUTE_TIME_KEY = "controller_execute_time";

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
        Map<String, Object> recordMap = RequestExtraUtil.of().getExtraMap();
        recordMap.put(RECORD_REQUEST_METHOD_KEY, RequestUtil.getRequest().getMethod());
        recordMap.put(RECORD_REQUEST_URI_KEY, RequestUtil.getRequest().getRequestURI());
        recordMap.put(RECORD_CONTROLLER_PARAMS_KEY, Arrays.stream(joinPoint.getArgs()).toList().toString());
    }

    /**
     * 环绕通知：在目标方法执行前后执行
     * 4.记录控制器执行时间
     * @param joinPoint 连接点
     * @return 目标方法的返回值
     * @throws Throwable 目标方法抛出的异常
     */
    @Around("controllerPointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        Map<String, Object> recordMap = RequestExtraUtil.of().getExtraMap();
        long startTimeMillis = System.currentTimeMillis();
        try {
            return joinPoint.proceed();
        } finally {
            long endTimeMillis = System.currentTimeMillis();
            long executeTimeMillis = endTimeMillis - startTimeMillis;
            recordMap.put(RECORD_CONTROLLER_EXECUTE_TIME_KEY, MessageFormat.format(
                    "execute_time: {0}, start_time: {1}, end_time: {2}",
                    executeTimeMillis, startTimeMillis, endTimeMillis
            ));
        }
    }
}
