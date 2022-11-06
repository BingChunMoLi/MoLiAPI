package com.bingchunmoli.api.weather.bean.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 天气缓存key
 * @author bingchunmoli
 */
@Getter
@AllArgsConstructor
public enum WeatherCacheKey {

    /**
     * 按天查询前缀
     */
    BY_DAY("weather:by_day:"),
    LOOKUP("weather:lookup:"),
    BY_NOW("weather:now:");

    private final String key;

}
