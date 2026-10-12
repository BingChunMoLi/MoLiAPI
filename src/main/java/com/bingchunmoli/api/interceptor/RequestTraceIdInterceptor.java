package com.bingchunmoli.api.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;

/**
 * 请求标识拦截器
 * @author MoLi
 */
@Slf4j
@Order(Integer.MIN_VALUE)
@Component
public class RequestTraceIdInterceptor implements HandlerInterceptor {

    public static final String REQUEST_ID = "requestId";

    @Override
    public boolean preHandle(
            @NonNull final HttpServletRequest request,
            @NonNull final HttpServletResponse response,
            @NonNull final Object handler) {
        MDC.put(REQUEST_ID, UUID.randomUUID().toString());
        response.setHeader("X-Request-Id", MDC.get(REQUEST_ID));
        return true;
    }

    @Override
    public void afterCompletion(
            @NonNull final HttpServletRequest request,
            @NonNull final HttpServletResponse response,
            @NonNull final Object handler,
            final Exception ex) {
        MDC.remove(REQUEST_ID);
    }
}
