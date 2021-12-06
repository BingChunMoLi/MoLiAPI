package com.bingchunmoli.api.bean.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 响应返回枚举
 * @author bingchunmoli
 */

@Getter
@AllArgsConstructor
public enum CodeEnum {
    /**
     * 成功
     */
    SUCCESS("00000", "一切OK"),
    /**
     * 客户端错误
     */
    ERROR("A0001", "客户端错误"),
    /**
     * 系统执行错误
     */
    FAILURE("B0001", "系统执行出错"),
    /**
     * 调用第三方错误
     */
    THIRD("C0001", "调用第三方服务错误");
    private final String code;
    private final String msg;

}
