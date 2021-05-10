package com.bingchunmoli.api.bean.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @description: 枚举类
 * @date: 2020/11/16 15:46
 * @version: 0.0.1-SNAPSHOT
 * @author Jinx
 */

@Getter
@AllArgsConstructor
public enum CodeEnum {
    /**
     * @description: 成功
     * @author: BingChunMoLi
     */
    SUCCESS("00000","一切OK"),
    /**
     * @description: 客户端错误
     * @author: BingChunMoLi
     * @date: 2020/11/16 19:59
     * @version: 0.0.1-SNAPSHOT
     */
    ERROR("A0001","客户端错误"),
    /**
     * @description: 系统执行错误
     * @author: BingChunMoLi
     * @date: 2020/11/16 19:59
     * @version: 0.0.1-SNAPSHOT
     */ 
    FAILURE("B0001","系统执行出错"),
    /**
     * @description: 调用第三方错误
     * @author: BingChunMoLi
     * @date: 2020/11/16 19:59
     * @version: 0.0.1-SNAPSHOT
     */ 
    THIRD("C0001","调用第三方服务错误");
    private String code;
    private String msg;

}
