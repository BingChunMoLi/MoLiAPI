package com.bingchunmoli.api.config.web;

import com.bingchunmoli.api.interceptor.IpInterceptor;
import com.bingchunmoli.api.interceptor.RequestTraceIdInterceptor;
import com.bingchunmoli.api.interceptor.SystemAuthenticationInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.converter.BufferedImageHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * 图片转换器及拦截器注册
 * @author bingchunmoli
 **/
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {
    private static final String[] ORIGINS = new String[]{"GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD"};
    private final IpInterceptor ipInterceptor;
    private final RequestTraceIdInterceptor requestTraceIdInterceptor;
    private final SystemAuthenticationInterceptor systemAuthenticationInterceptor;

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
        registry.addInterceptor(systemAuthenticationInterceptor).addPathPatterns("/system/**");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedHeaders("X-Request-Id", "Content-Type", "Origin", "X-Request-With",
                        "If-Modified-Since", "Pragma", "Last-Modified", "Cache-Control",
                        "Expires", "Access-Control-Allow-Credentials", "Dnt", "Keep-Alive",
                        "User-Agent", "Range")
                .exposedHeaders("X-Request-Id", "Content-Type", "Content-Length",
                        "Content-Md5", "Content-Encoding")
                .allowCredentials(true)
                .allowedMethods(ORIGINS)
                .maxAge(3600);
    }

    /**
     * Configure simple automated controllers pre-configured with the response
     * status code and/or a view to render the response body. This is useful in
     * cases where there is no need for custom controller logic -- e.g. render a
     * home page, perform simple site URL redirects, return a 404 status with
     * HTML content, a 204 with no content, and more.
     *
     * @param registry
     * @see ViewControllerRegistry
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("forward:/index.html");
        registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
    }
}
