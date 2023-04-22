package com.bingchunmoli.api.exception;

/**
 * Api任务异常
 *
 * @author bingchunmoli
 */
public class ApiTaskException extends ApiException{

    public ApiTaskException(String msg) {
        super(msg);
    }

    public ApiTaskException(Throwable cause) {
        super(cause);
    }
}
