package com.bingchunmoli.api.exception;


import com.bingchunmoli.api.interceptor.RequestTraceIdInterceptor;
import org.slf4j.MDC;

import java.text.MessageFormat;

/**
 * Api异常基类
 *
 * @author bingchunmoli
 */
public class ApiException extends RuntimeException {

    public ApiException(String msg) {
        super(MessageFormat.format("traceId: {0}threadLocal:{1}\n{2}", MDC.get("traceId"), RequestTraceIdInterceptor.threadLocal.get(), msg));
    }
}
