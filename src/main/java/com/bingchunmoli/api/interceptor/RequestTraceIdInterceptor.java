package com.bingchunmoli.api.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;

/**
 * TraceId拦截器生成和清除traceId
 * @author MoLi
 */
@Slf4j
@Order(Integer.MIN_VALUE)
@Component
public class RequestTraceIdInterceptor implements HandlerInterceptor {

    public static final String TRACE_ID = "traceId";

    @Override
    public boolean preHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) {
        MDC.put(TRACE_ID, UUID.randomUUID().toString());
        response.setHeader("X-Request-Id", MDC.get(TRACE_ID));
        return true;
    }

    @Override
    public void afterCompletion(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler, Exception ex) {
        MDC.clear();
    }
}
