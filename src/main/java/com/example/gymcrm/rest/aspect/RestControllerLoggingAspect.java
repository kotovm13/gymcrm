package com.example.gymcrm.rest.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

import static com.example.gymcrm.rest.filter.RestLoggingFilter.TRANSACTION_ID;

@Aspect
@Component
public class RestControllerLoggingAspect {
    private static final Logger LOGGER = LoggerFactory.getLogger(RestControllerLoggingAspect.class);

    @Around("@within(org.springframework.web.bind.annotation.RestController)")
    public Object logControllerOperation(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String operation = signature.getDeclaringType().getSimpleName() + "." + signature.getName();
        String transactionId = MDC.get(TRANSACTION_ID);
        long startedAt = System.nanoTime();

        LOGGER.info("REST operation started transactionId={}, operation={}", transactionId, operation);
        try {
            Object result = joinPoint.proceed();
            LOGGER.info("REST operation completed transactionId={}, operation={}, durationMs={}",
                    transactionId, operation, elapsedMilliseconds(startedAt));
            return result;
        } catch (Throwable throwable) {
            LOGGER.warn("REST operation failed transactionId={}, operation={}, durationMs={}, exception={}",
                    transactionId,
                    operation,
                    elapsedMilliseconds(startedAt),
                    throwable.getClass().getSimpleName());
            throw throwable;
        }
    }

    private long elapsedMilliseconds(long startedAt) {
        return TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startedAt);
    }
}
