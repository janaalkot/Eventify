package com.evently.common.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingAspect.class);

    @Around("execution(* com.evently..controller..*(..)) || execution(* com.evently..service..*(..))")
    public Object logMethodCall(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        String signature = joinPoint.getSignature().toShortString();

        LOGGER.info("Entering {}", signature);
        try {
            Object result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - startTime;
            LOGGER.info("Completed {} in {} ms", signature, executionTime);
            return result;
        } catch (Throwable throwable) {
            long executionTime = System.currentTimeMillis() - startTime;
            LOGGER.error("Failed {} after {} ms", signature, executionTime, throwable);
            throw throwable;
        }
    }

    @AfterThrowing(pointcut = "execution(* com.evently..controller..*(..)) || execution(* com.evently..service..*(..))", throwing = "throwable")
    public void logException(Throwable throwable) {
        LOGGER.error("Unhandled exception captured by AOP", throwable);
    }
}
