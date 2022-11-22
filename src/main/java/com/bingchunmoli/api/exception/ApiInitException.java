package com.bingchunmoli.api.exception;

/**
 * @author MoLi
 */
public class ApiInitException extends ApiException{

    public ApiInitException(String msg) {
        super(msg);
    }

    public ApiInitException(Throwable e) {
        super(e);
    }
}
