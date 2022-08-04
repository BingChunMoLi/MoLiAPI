package com.bingchunmoli.api.exception;


import org.slf4j.MDC;

import java.text.MessageFormat;

/**
 * Api异常基类
 *
 * @author bingchunmoli
 */
public class ApiException extends RuntimeException {

    public ApiException(String msg) {
        super(MessageFormat.format("traceId: {0}\n{1}", MDC.get("traceId"), msg));
    }
}
