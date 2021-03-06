package com.bingchunmoli.api.controller.advice;

import com.bingchunmoli.api.bean.ResultVO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.awt.image.BufferedImage;

/**
 * @copyright(c) 2017-2020 冰纯茉莉
 * @Description: TODO 加强restController的统一响应类
 * @Author 冰彦糖
 * @Data 2020/11/16 20:24
 * @ClassName ResponseControllerAdvice
 * @PackageName: com.bingchunmoli.api.Controller.advice
 * @Version 0.0.1-SNAPSHOT
 **/
@RestControllerAdvice("com.bingchunmoli.api")
public class ResponseControllerAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        //@Description: 如果返回值已经是ResultVO的对象就没必要再次封装返回false
        return !returnType.getGenericParameterType().equals(ResultVO.class);
        // || !returnType.getGenericParameterType().equals(BufferedImage.class)
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        if (returnType.getGenericParameterType().equals(String.class)){
            //String无法直接包装
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                return objectMapper.writeValueAsString(new ResultVO(body));
            } catch (JsonProcessingException e) {

                e.printStackTrace();
            }
        }
        if (returnType.getGenericParameterType().equals(BufferedImage.class)){
            return body;
        }
        return new ResultVO(body);
    }
}