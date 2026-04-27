package com.evently.event_service.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.http.HttpServletRequest;

@Aspect
@Component
public class LoggingAspect {
    
    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Around("execution(* com.evently.event_service.controller..*(..))")
    public Object logRequests(ProceedingJoinPoint joinPoint) throws Throwable {

        long start = System.currentTimeMillis();

        // Get request from RequestContextHolder (thread-safe for aspects)
        ServletRequestAttributes attributes = null;
        String method = "UNKNOWN";
        String uri = "UNKNOWN";
        String email = "UNKNOWN";
        String role = "UNKNOWN";
        
        try {
            attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                method = request.getMethod();
                uri = request.getRequestURI();
                
                String emailHeader = request.getHeader("X-User-Email");
                if (emailHeader != null && !emailHeader.isEmpty()) {
                    email = emailHeader;
                }
                
                String roleHeader = request.getHeader("X-User-Role");
                if (roleHeader != null && !roleHeader.isEmpty()) {
                    role = roleHeader;
                }
            }
        } catch (IllegalStateException e) {
            // RequestContextHolder throws IllegalStateException if called outside of a request context
            logger.debug("Request context not available (possibly running outside servlet context)", e);
        } catch (Exception e) {
            logger.warn("Error retrieving request context", e);
        }

        // Log request details
        logger.info("================ AOP LOG =================");
        logger.info("📡 API: {} {}", method, uri);
        logger.info("👤 User: {}", email);
        logger.info("🔐 Role: {}", role);
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