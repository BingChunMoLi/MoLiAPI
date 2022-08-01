package com.bingchunmoli.api.controller.advice;

import com.alibaba.fastjson.JSON;
import com.bingchunmoli.api.bean.MailMessage;
import com.bingchunmoli.api.bean.ResultVO;
import com.bingchunmoli.api.bean.enums.CodeEnum;
import com.bingchunmoli.api.even.MailMessageEven;
import com.bingchunmoli.api.exception.ApiException;
import com.bingchunmoli.api.exception.ApiParamException;
import com.bingchunmoli.api.interceptor.RequestTraceIdInterceptor;
import com.bingchunmoli.api.qrcode.exception.FileIsEmptyException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author 冰彦糖
 * @version 0.0.1-SNAPSHOT
 **/
@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class ExceptionControllerAdvice {

    private final ApplicationEventPublisher applicationEventPublisher;
    @Value("${spring.mail.enable}")
    private boolean mailEnable;

    @ExceptionHandler
    public ResultVO<String> fileIsEmptyException(FileIsEmptyException e) {
        log.error("文件为空, ", e);
        log.info("bate traceId(MDC, threadLocal): {}, {}", MDC.get("traceId"), RequestTraceIdInterceptor.threadLocal.get());
        return new ResultVO<>(CodeEnum.ERROR.getCode(), e.getMessage(), "文件为空,请确认文件是否存在");
    }

    @ExceptionHandler
    public ResultVO<String> httpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.error("没有受支持的方法", e);
        log.info("bate traceId(MDC, threadLocal): {}, {}", MDC.get("traceId"), RequestTraceIdInterceptor.threadLocal.get());
        return new ResultVO<>(CodeEnum.ERROR.getCode(), CodeEnum.ERROR.getMsg(), "没有受支持的请求方法，请求方法错误.");
    }

    @ExceptionHandler
    public ResultVO<String> defaultException(Exception e) {
        log.error("defaultException: {}, msg: {}", e.getMessage(), e);
        e.printStackTrace();
        if (mailEnable) {
            MailMessage errMailMessage = MailMessage.builder().title("出现未分类异常").body("defaultException: "+ JSON.toJSONString(e)).build();
            applicationEventPublisher.publishEvent(new MailMessageEven(errMailMessage));
        }
        log.info("bate traceId(MDC, threadLocal): {}, {}", MDC.get("traceId"), RequestTraceIdInterceptor.threadLocal.get());
        return new ResultVO<>(CodeEnum.FAILURE.getCode(), CodeEnum.FAILURE.getMsg(), "默认未分类异常");
    }

    @ExceptionHandler
    public ResultVO<String> defaultThrowable(Throwable throwable) {
        log.error("系统错误: ", throwable);
        log.info("bate traceId(MDC, threadLocal): {}, {}", MDC.get("traceId"), RequestTraceIdInterceptor.threadLocal.get());
        return new ResultVO<>(CodeEnum.FAILURE.getCode(), CodeEnum.FAILURE.getMsg(), "严重异常");
    }

    @ExceptionHandler
    public ResultVO<String> apiException(ApiException e) {
        log.error("api顶层异常: ", e);
        log.info("bate traceId(MDC, threadLocal): {}, {}", MDC.get("traceId"), RequestTraceIdInterceptor.threadLocal.get());
        return new ResultVO<>(CodeEnum.FAILURE.getCode(), CodeEnum.FAILURE.getMsg(), "api异常");
    }

    @ExceptionHandler
    public ResultVO<String> apiParamException(ApiParamException e) {
        log.error("请求参数不正确或不支持: ", e);
        log.info("bate traceId(MDC, threadLocal): {}, {}", MDC.get("traceId"), RequestTraceIdInterceptor.threadLocal.get());
        return new ResultVO<>(CodeEnum.ERROR.getCode(), CodeEnum.ERROR.getMsg(), "请求参数异常");
    }
}