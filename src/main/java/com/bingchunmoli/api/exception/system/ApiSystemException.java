package com.bingchunmoli.api.exception.system;

import com.bingchunmoli.api.exception.ApiException;

public class ApiSystemException extends ApiException {

    public ApiSystemException(String msg) {
        super(msg);
    }

    public ApiSystemException(Throwable cause) {
        super(cause);
    }
}
