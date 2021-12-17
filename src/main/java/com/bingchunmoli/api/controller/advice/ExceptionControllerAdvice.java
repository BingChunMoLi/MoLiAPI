package com.bingchunmoli.api.controller.advice;

import com.bingchunmoli.api.bean.ResultVO;
import com.bingchunmoli.api.bean.enums.CodeEnum;
import com.bingchunmoli.api.exception.ApiException;
import com.bingchunmoli.api.exception.ApiParamException;
import com.bingchunmoli.api.qrcode.exception.FileIsEmptyException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author 冰彦糖
 * @version 0.0.1-SNAPSHOT
 **/
@RestControllerAdvice
@Slf4j
public class ExceptionControllerAdvice {
    @ExceptionHandler
    public ResultVO<String> fileIsEmptyException(FileIsEmptyException e) {
        log.error(e.getMessage());
        return new ResultVO<>(CodeEnum.ERROR.getCode(), e.getMessage(), "文件为空,请确认文件是否存在");
    }

    @ExceptionHandler
    public ResultVO<String> httpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.error(e.getMessage());
        return new ResultVO<>(CodeEnum.ERROR.getCode(), CodeEnum.ERROR.getMsg(), "没有受支持的请求方法，请求方法错误.");
    }

    @ExceptionHandler
    public ResultVO<String> defaultException(Exception e) {
        log.error("defaultException: {}, msg: {}", e, e.getMessage());
        e.printStackTrace();
        return new ResultVO<>(CodeEnum.FAILURE.getCode(), CodeEnum.FAILURE.getMsg(), "默认未分类异常");
    }

    @ExceptionHandler
    public ResultVO<String> defaultThrowable(Throwable throwable) {
        log.error(throwable.getMessage());
        return new ResultVO<>(CodeEnum.FAILURE.getCode(), CodeEnum.FAILURE.getMsg(), "严重异常");
    }

    @ExceptionHandler
    public ResultVO<String> apiException(ApiException e) {
        log.error(e.getMessage());
        return new ResultVO<>(CodeEnum.FAILURE.getCode(), CodeEnum.FAILURE.getMsg(), "api异常");
    }

    @ExceptionHandler
    public ResultVO<String> apiParamException(ApiParamException e) {
        log.error(e.getMessage());
        return new ResultVO<>(CodeEnum.ERROR.getCode(), CodeEnum.ERROR.getMsg(), "请求参数异常");
    }
}