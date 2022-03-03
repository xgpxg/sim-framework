package com.yao2san.sim.framework.log.aspect;

import com.yao2san.sim.framework.log.annotation.PrintLog;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;
import java.util.Arrays;

@Aspect
@Slf4j
public class PrintLogAspect {
    @Pointcut("@annotation(com.yao2san.sim.framework.log.annotation.PrintLog)")
    public void pointcut() {
    }

    @Around("pointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        PrintLog annotation = method.getAnnotation(PrintLog.class);
        Long start = System.currentTimeMillis();

        Object[] args = joinPoint.getArgs();
        log.info("{} - {} in parameters: {}", annotation.name(), annotation.prefix(), Arrays.toString(args));

        Object proceed;
        try {
            proceed = joinPoint.proceed();
        } catch (Throwable throwable) {
            throw throwable;
        }

        Long end = System.currentTimeMillis();

        if (annotation.useTimer()) {
            log.info("{} - {} use time: {}", annotation.name(), annotation.prefix(), (end - start));
        }

        return proceed;
    }
}
