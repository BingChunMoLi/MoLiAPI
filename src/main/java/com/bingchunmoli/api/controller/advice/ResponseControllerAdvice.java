package com.bingchunmoli.api.controller.advice;

import com.bingchunmoli.api.bean.ResultVO;
import org.jspecify.annotations.NonNull;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.lang.reflect.Method;

/**
 * 统一响应处理
 * @author bingchunmoli
 **/
@RestControllerAdvice("com.bingchunmoli.api")
public class ResponseControllerAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(
            @NonNull final MethodParameter returnType,
            @NonNull final Class<? extends HttpMessageConverter<?>> converterType
    ) {
        final Method method = returnType.getMethod();
        final boolean rawMethod = method != null && AnnotatedElementUtils.hasAnnotation(method, RawResponse.class);
        final boolean rawController = AnnotatedElementUtils.hasAnnotation(
                returnType.getContainingClass(),
                RawResponse.class
        );
        return JacksonJsonHttpMessageConverter.class.isAssignableFrom(converterType)
                && !rawMethod
                && !rawController;
    }

    @Override
    public Object beforeBodyWrite(
            final Object body,
            final MethodParameter returnType,
            @NonNull final MediaType selectedContentType,
            @NonNull final Class<? extends HttpMessageConverter<?>> selectedConverterType,
            @NonNull final ServerHttpRequest request,
            @NonNull final ServerHttpResponse response
    ) {
        if (body instanceof ResultVO<?>) {
            return body;
        }
        return ResultVO.ok(body);
    }
}
