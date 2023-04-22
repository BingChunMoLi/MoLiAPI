package com.bingchunmoli.api.aspect;

import com.bingchunmoli.api.annotation.Log;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @author MoLi
 */
@Slf4j
@Aspect
@Component
public class LogAspect {

    @Async
    @AfterReturning(pointcut = "@annotation(logging)", returning = "result")
    public void log(JoinPoint jp, Log logging, Object result){
        if (log.isInfoEnabled()) {
            log.info("模块名: {}, 其他信息: {}, 方法全类名: {}, 方法名: {}, 请求参数:{}, 返回值: {}", logging.module(), logging.info(), jp.getTarget().getClass().getName(), jp.getSignature().getName(), jp.getArgs(), result);
        }
    }

    @Async
    @AfterThrowing(pointcut = "@annotation(logging)", throwing = "e")
    public void logThrowing(JoinPoint jp, Log logging, Exception e){
        if (log.isDebugEnabled()) {
            log.error("模块名: {}, 其他信息: {}, 方法全类名: {}, 方法名: {}, 请求参数:{}, 异常信息", logging.module(), logging.info(), jp.getTarget().getClass().getName(), jp.getSignature().getName(), jp.getArgs(), e);
        }
    }
}
