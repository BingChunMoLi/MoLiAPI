package com.bingchunmoli.api.bing.bean.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author BingChunMoLi
 */
@Getter
@AllArgsConstructor
public enum BingEnum {
    /**
     * bing图片key
     */
    CN_BING("bing:image:cn", 0),
    EN_BING("bing:image:en", 1),
    ALL_BING("bing:image:all", null);
    private String key;
    private Integer search;
}
