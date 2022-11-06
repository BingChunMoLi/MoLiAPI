package com.bingchunmoli.api.config.web.mvc;

import com.bingchunmoli.api.interceptor.IpInterceptor;
import com.bingchunmoli.api.interceptor.RequestTraceIdInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.BufferedImageHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * 图片转换器及拦截器注册
 * @author bingchunmoli
 **/
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {
    private final IpInterceptor ipInterceptor;
    private final RequestTraceIdInterceptor requestTraceIdInterceptor;
    /**
     * 图片转换器
     */
    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(new BufferedImageHttpMessageConverter());
    }

    /**
     * 拦截器注册
     * @param registry 拦截器注册器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(ipInterceptor).addPathPatterns("/**");
        registry.addInterceptor(requestTraceIdInterceptor).addPathPatterns("/**");
    }
}