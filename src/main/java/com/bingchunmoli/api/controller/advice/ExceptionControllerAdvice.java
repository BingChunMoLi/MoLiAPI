package com.bingchunmoli.api.controller.advice;

import com.bingchunmoli.api.bean.ResultVO;
import com.bingchunmoli.api.bean.enums.CodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @copyright(c) 2017-2020 冰纯茉莉
 * @Description: TODO 控制类统一异常处理
 * @Author 冰彦糖
 * @Data 2020/11/16 19:52
 * @ClassName ExceptionControllerAdvice
 * @PackageName: com.bingchunmoli.api.Controller.advice
 * @Version 0.0.1-SNAPSHOT
 **/
@RestControllerAdvice
@Slf4j
public class ExceptionControllerAdvice {
    @ExceptionHandler
    public ResultVO httpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e){
        log.error(e.getMessage());
        return new ResultVO(CodeEnum.ERROR.getCode() , CodeEnum.ERROR.getMsg(), "没有受支持的请求方法，请求方法错误.");
    }
    @ExceptionHandler
    public ResultVO defaultException(Exception e){
        log.error(e.getMessage());
        return new ResultVO(CodeEnum.ERROR.getCode(),CodeEnum.ERROR.getMsg(), "默认未分类异常");
    }
    @ExceptionHandler
    public ResultVO defaultThrowable(Throwable throwable){
        log.error(throwable.getMessage());
        return new ResultVO(CodeEnum.ERROR.getCode() , CodeEnum.ERROR.getMsg(), "严重异常");
    }
}