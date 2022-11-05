package com.bingchunmoli.api.exception;

/**
 * @author BingChunMoLi
 */
public class ApiFileIsEmptyException extends ApiParamException {
    public ApiFileIsEmptyException() {
        super("文件为空异常");
    }
}
