package com.bingchunmoli.api.controller.advice;

import com.bingchunmoli.api.bean.ResultVO;
import com.bingchunmoli.api.bean.enums.CodeEnum;
import com.bingchunmoli.api.even.MessageEven;
import com.bingchunmoli.api.exception.ApiException;
import com.bingchunmoli.api.exception.ApiFileIsEmptyException;
import com.bingchunmoli.api.exception.ApiJsonProcessingException;
import com.bingchunmoli.api.exception.ApiParamException;
import com.bingchunmoli.api.exception.system.ApiSystemException;
import com.bingchunmoli.api.exception.system.ApiUserNonFoundException;
import com.bingchunmoli.api.interceptor.RequestTraceIdInterceptor;
import com.bingchunmoli.api.push.bean.MailMessage;
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
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * 统一异常处理
 * @author 冰彦糖
 **/
@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class ExceptionControllerAdvice {
    private final ApplicationEventPublisher applicationEventPublisher;
    @Value("${spring.mail.enable:false}")
    private boolean mailEnable;
    @Value("${spring.mail.defaultTo}")
    private String defaultTo;
    @Value("${spring.mail.username}")
    private String mailFrom;

    private final ObjectMapper om;

    @ExceptionHandler
    public ResultVO<String> apiUserNonFoundException(ApiUserNonFoundException e) {
        log.error(getExceptionErrorLogMessage("用户不存在"), e);
        ResultVO<String> result = new ResultVO<>(CodeEnum.ERROR, getExceptionJsonMessage());
        result.setMsg("用户未找到");
        return result;
    }

    @ExceptionHandler
    public ResultVO<String> systemException(ApiSystemException e) {
        log.error(getExceptionErrorLogMessage("后台管理异常"), e);
        ResultVO<String> result = new ResultVO<>(CodeEnum.ERROR, getExceptionJsonMessage());
        result.setMsg("后台管理异常");
        return result;
    }

    @ExceptionHandler
    public ResultVO<String> fileIsEmptyException(ApiFileIsEmptyException e) {
        log.error(getExceptionErrorLogMessage("文件为空,请确认文件是否存在"), e);
        ResultVO<String> result = new ResultVO<>(CodeEnum.ERROR, getExceptionJsonMessage());
        result.setMsg("文件为空,请确认文件是否存在");
        return result;
    }

    @ExceptionHandler
    public ResultVO<String> httpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.warn(getExceptionErrorLogMessage("请求方法不正确或者没有受支持的方法"), e);
        ResultVO<String> result = new ResultVO<>(CodeEnum.ERROR, getExceptionJsonMessage());
        result.setMsg("请求方法不正确或者没有受支持的方法");
        return result;
    }

    @ExceptionHandler
    public ResultVO<String> noResourceFoundException(NoResourceFoundException e) {
        log.warn(getExceptionErrorLogMessage("没有找到需要的资源"), e);
        ResultVO<String> result = new ResultVO<>(CodeEnum.ERROR, getExceptionJsonMessage());
        result.setMsg("没有找到需要的资源");
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
    public ResultVO<String> apiJsonProcessingException(ApiJsonProcessingException e) {
        log.error(getExceptionErrorLogMessage("json转换错误(json格式化不正确)"), e);
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
            MailMessage errMailMessage = MailMessage.builder()
                    .from(mailFrom)
                    .to(defaultTo)
                    .title("未分类异常")
                    .build();
            try {
                errMailMessage.setBody("defaultException: " + e.getLocalizedMessage() + " message: " + e.getMessage() + "\n stackTrace: " + om.writeValueAsString(e.getStackTrace()));
            } catch (JsonProcessingException ex) {
                log.error("defaultException: JsonProcessingException: ", ex);
            }
            applicationEventPublisher.publishEvent(new MessageEven(this, errMailMessage));
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
