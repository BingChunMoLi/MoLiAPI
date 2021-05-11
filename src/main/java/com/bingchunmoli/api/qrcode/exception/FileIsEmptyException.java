package com.bingchunmoli.api.qrcode.exception;

/**
 * @author BingChunMoLi
 */
public class FileIsEmptyException extends RuntimeException {
    public FileIsEmptyException() {
        super("文件为空异常");
    }
}
