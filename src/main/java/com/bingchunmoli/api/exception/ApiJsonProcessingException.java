package com.bingchunmoli.api.exception;

/**
 * api的json解析异常,通常为com.fasterxml.jackson.core.JsonProcessingException捕获后wrapper后抛出
 * @author MoLi
 */
public class ApiJsonProcessingException extends ApiException{
    public ApiJsonProcessingException(String msg) {
        super(msg);
    }

    public ApiJsonProcessingException(Throwable cause) {
        super(cause);
    }
}
