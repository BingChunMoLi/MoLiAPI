package com.bingchunmoli.api.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

/**
 * @author MoLi
 */
@Slf4j
@Order(1)
@Component
public class RequestTraceIdInterceptor implements HandlerInterceptor {

    public static ThreadLocal<String> threadLocal = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String traceId = UUID.randomUUID().toString();
        threadLocal.set(traceId);
        MDC.put("traceId", traceId);
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        threadLocal.remove();
        MDC.clear();
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
