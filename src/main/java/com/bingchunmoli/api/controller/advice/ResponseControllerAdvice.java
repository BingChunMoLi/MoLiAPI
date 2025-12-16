package com.bingchunmoli.api.controller.advice;

import com.bingchunmoli.api.bean.ResultVO;
import com.bingchunmoli.api.exception.ApiException;
import org.jspecify.annotations.NonNull;
import org.springframework.core.MethodParameter;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.awt.image.BufferedImage;

/**
 * 统一响应处理
 * @author bingchunmoli
 **/
@RestControllerAdvice("com.bingchunmoli.api")
public class ResponseControllerAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(@NonNull MethodParameter returnType, @NonNull Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, @NonNull MediaType selectedContentType, @NonNull Class<? extends HttpMessageConverter<?>> selectedConverterType, @NonNull ServerHttpRequest request, @NonNull ServerHttpResponse response) {
        if (returnType.getGenericParameterType().equals(String.class)) {
            //String无法直接包装
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                //iss contentType为text错误的contentType类型预期为application/json
                return objectMapper.writeValueAsString(new ResultVO<>(body));
            } catch (JacksonException e) {
                throw new ApiException(e);
            }
        }
        if (returnType.getGenericParameterType().equals(BufferedImage.class)) {
            return body;
        }
        if (body instanceof FileSystemResource) {
            return body;
        }
        if (body instanceof ResultVO<?>) {
            return body;
        }
        return new ResultVO<>(body);
    }
}