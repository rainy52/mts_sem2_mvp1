package com.mipt.mvpmts2.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class LoggingAspect {

  private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

  @Pointcut("within(com.mipt.mvpmts2.service..*)")
  public void serviceLayer() {
  }

  @Pointcut("execution(public * *(..))")
  public void publicMethods() {
  }


  @Pointcut("serviceLayer() && publicMethods()")
  public void servicePublicMethods() {
  }


  @Around("servicePublicMethods()")
  public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
    String className = joinPoint.getSignature().getDeclaringTypeName();
    String methodName = joinPoint.getSignature().getName();
    Object[] args = joinPoint.getArgs();

    long startTime = System.currentTimeMillis();

    logger.info("START {}.{}({})",
        className,
        methodName,
        Arrays.toString(args)
    );

    Object result;
    try {
      result = joinPoint.proceed();

      long endTime = System.currentTimeMillis();
      long duration = endTime - startTime;

      if (result != null) {
        logger.info("END {}.{}() - {}, {} мс",
            className, methodName, result.getClass().getSimpleName(), duration);
      } else {
        logger.info("END {}.{}() - {} мс",
            className, methodName, duration);
      }

      return result;

    } catch (Throwable e) {
      long endTime = System.currentTimeMillis();
      long duration = endTime - startTime;

      logger.error("ERROR {}.{}() - {}, {} мс",
          className, methodName, e.getClass().getSimpleName(), duration, e);

      throw e;
    }
  }
}