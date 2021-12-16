package com.bingchunmoli.api.weather.bean.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author bingchunmoli
 */
@Getter
@AllArgsConstructor
public enum WeatherCacheKey {

    /**
     * 按天查询前缀
     */
    BY_DAY("weather:by_day:"),
    LOOKUP("weather:lookup:");

    private final String key;

}
