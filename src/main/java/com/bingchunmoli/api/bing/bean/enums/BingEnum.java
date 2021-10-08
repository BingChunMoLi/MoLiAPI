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
    CNBING("bing:image:cn"),
    ENBING("bing:image:en"),
    ALLBING("bing:image:all");
    private String key;
}
