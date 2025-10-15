package com.example.Social_Media_Portal.AOP;


import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ExceptionHandlingAspect {

    @Pointcut("execution(* com.example.Social_Media_Portal.Controller.*.*(..))")
    public void allControllers() {}

    @AfterThrowing(pointcut = "allControllers()", throwing = "ex")
    public void logControllerExceptions(Exception ex) {
        System.out.println("Logging exception : " + ex.getMessage() + " "  + ex);
    }


    @Around("execution(* com.example.Social_Media_Portal.Controller.*.*(..))")
    public Object calculateTime(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        String method = proceedingJoinPoint.getSignature().toShortString();
        System.out.println("=====>>>>> Executing @Around on method :" + method);
        long start = System.currentTimeMillis();

        Object result = proceedingJoinPoint.proceed();
        long end = System.currentTimeMillis();
        long time = end - start;
        System.out.println("=====>>>>> Duration : " + time + " ms");
        return result;
    }

}
