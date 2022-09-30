package com.bingchunmoli.api.controller.advice;

import com.bingchunmoli.api.bean.MailMessage;
import com.bingchunmoli.api.bean.ResultVO;
import com.bingchunmoli.api.bean.enums.CodeEnum;
import com.bingchunmoli.api.even.MailMessageEven;
import com.bingchunmoli.api.exception.ApiException;
import com.bingchunmoli.api.exception.ApiParamException;
import com.bingchunmoli.api.interceptor.RequestTraceIdInterceptor;
import com.bingchunmoli.api.qrcode.exception.FileIsEmptyException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

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
    private final ObjectMapper om;

    @ExceptionHandler
    public ResultVO<String> fileIsEmptyException(FileIsEmptyException e) {
        log.error(getExceptionErrorLogMessage("文件为空,请确认文件是否存在"), e);
        ResultVO<String> result = new ResultVO<>(CodeEnum.ERROR, getExceptionJsonMessage());
        result.setMsg("文件为空,请确认文件是否存在");
        return result;
    }

    @ExceptionHandler
    public ResultVO<String> httpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.error(getExceptionErrorLogMessage("请求方法不正确或者没有受支持的方法"), e);
        ResultVO<String> result = new ResultVO<>(CodeEnum.ERROR, getExceptionJsonMessage());
        result.setMsg("请求方法不正确或者没有受支持的方法");
        return result;
    }


    @ExceptionHandler
    public ResultVO<String> apiException(ApiException e) {
        log.error(getExceptionErrorLogMessage("api异常"), e);
        return new ResultVO<>(CodeEnum.FAILURE, getExceptionJsonMessage());
    }

    @ExceptionHandler
    public ResultVO<String> apiParamException(ApiParamException e) {
        log.error(getExceptionErrorLogMessage("请求参数不正确或者不支持"), e);
        return new ResultVO<>(CodeEnum.ERROR, getExceptionJsonMessage());
    }

    @ExceptionHandler
    public ResultVO<String> jsonProcessingException(JsonProcessingException e){
        log.error(getExceptionErrorLogMessage("json转换错误(json格式化不正确)"), e);
        return new ResultVO<>(CodeEnum.FAILURE, getExceptionJsonMessage());
    }

    @ExceptionHandler
    public ResultVO<String> methodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e){
        log.error(getExceptionErrorLogMessage("方法参数类型无法匹配"), e);
        return new ResultVO<>(CodeEnum.FAILURE, getExceptionJsonMessage());
    }

    @ExceptionHandler
    public ResultVO<String> nullPointerException(NullPointerException e) {
        log.error(getExceptionErrorLogMessage("空指针异常"), e);
        ResultVO<String> result = new ResultVO<>(CodeEnum.FAILURE, getExceptionJsonMessage());
        result.setMsg("空指针异常");
        return result;
    }

    @ExceptionHandler
    public ResultVO<String> defaultException(Exception e) {
        log.error(getExceptionErrorLogMessage("未分类异常"), e);
        if (mailEnable) {
            MailMessage errMailMessage = null;
            try {
                errMailMessage = MailMessage.builder().title("出现未分类异常").body("defaultException: " + e.getLocalizedMessage() + " message: " + e.getMessage() + "\n stackTrace: " + om.writeValueAsString(e.getStackTrace())).build();
            } catch (JsonProcessingException ex) {
                log.error("defaultException: JsonProcessingException: ", ex);
            }
            applicationEventPublisher.publishEvent(new MailMessageEven(errMailMessage));
        }
        return new ResultVO<>(CodeEnum.FAILURE, getExceptionJsonMessage());
    }

    @ExceptionHandler
    public ResultVO<String> defaultThrowable(Throwable throwable) {
        log.error(getExceptionErrorLogMessage("系统错误"), throwable);
        return new ResultVO<>(CodeEnum.FAILURE, getExceptionJsonMessage());
    }

    private String getExceptionErrorLogMessage(String exceptionMessage){
        return getExceptionJsonMessage() +
                "; \t" +
                exceptionMessage;
    }

    private String getExceptionJsonMessage(){
        return "{" +
                RequestTraceIdInterceptor.TRACE_ID +
                ": " +
                MDC.get(RequestTraceIdInterceptor.TRACE_ID) +
                "}";
    }
}