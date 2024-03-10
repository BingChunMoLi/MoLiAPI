package com.bingchunmoli.api.interceptor;

import cn.hutool.extra.servlet.JakartaServletUtil;
import com.bingchunmoli.api.utils.RedisUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 基于IP的次数拦截器
 * @author MoLi
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class IpInterceptor implements HandlerInterceptor {
    private final RedisUtil redisUtil;


    @Override
    public boolean preHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        String clientIP = JakartaServletUtil.getClientIP(request);
        String key = "filter:" + requestURI + ":" + clientIP + ":" + request.getRequestedSessionId();
        Integer value = redisUtil.getObject(key);
        if (value != null && value > 40) {
            response.setStatus(429);
            return false;
        }
        AtomicInteger count = new AtomicInteger();
        if (value == null) {
            count.getAndIncrement();
            if (log.isDebugEnabled()) {
                log.debug("{} 用户首次访问 {}", clientIP, requestURI);
            }
        }else {
            count.getAndAdd(value + 1);
        }
        if (log.isDebugEnabled()) {
            log.debug("{} 访问 {} {} 次", clientIP, requestURI, count.get());
        }
        redisUtil.setObject(key, count.get(), 60, TimeUnit.SECONDS);
        return true;
    }
}
