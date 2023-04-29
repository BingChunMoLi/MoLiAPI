package com.bingchunmoli.api.weather.bean.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 缓存前缀key
 * @author bingchunmoli
 */
@Getter
@AllArgsConstructor
public enum WeatherCacheKey {

    /**
     * 按天查询
     */
    BY_DAY("weather:by_day:"),
    /**
     * 地址反解析
     */
    LOOKUP("weather:lookup:"),
    /**
     * 当前时间
     */
    BY_NOW("weather:now:"),
    /**
     * 订阅天气通知
     */
    SUBSCRIBE("weather:subscribe:");

    private final String key;

}
