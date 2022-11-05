package com.bingchunmoli.api.host.bean;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Host类型
 * @author BingChunMoLi
 */

@Getter
@AllArgsConstructor
public enum HostTypeEnum {

    /**
     * 请求hostTYPE的枚举
     */
    ALL(0, "all"),
    LOCAL(1, "local"),
    MYAD(2, "myAd"),
    NEOHOSTSBASE(3, "neohostsBase");

    private final Integer type;
    private final String info;
}
