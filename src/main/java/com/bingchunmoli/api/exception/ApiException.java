package com.bingchunmoli.api.exception;


/**
 * Api异常基类
 * @author bingchunmoli
 */
public class ApiException extends RuntimeException{

    public ApiException(String msg) {
        super(msg);
    }
}
