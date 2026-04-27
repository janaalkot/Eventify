package com.evently.user_service.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Aspect
@Component
public class LoggingAspect {
    
    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Around("execution(* com.evently.user_service.controller..*(..))")
    public Object logRequests(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        
        logger.info("================ AOP LOG =================");
        logger.info("🎯 Method: {}", joinPoint.getSignature().toShortString());
        
        try {
            Object result = joinPoint.proceed();
            long time = System.currentTimeMillis() - start;
            
            logger.info("✅ SUCCESS");
            logger.info("⏱ Execution Time: {} ms", time);
            logger.info("=========================================");
            
            return result;
            
        } catch (Exception e) {
            long time = System.currentTimeMillis() - start;
            
            logger.error("❌ ERROR: {} - {}", e.getClass().getSimpleName(), e.getMessage(), e);
            logger.error("⏱ Execution Time: {} ms", time);
            logger.error("=========================================");
            
            throw e;
        }
    }
}
