package com.bingchunmoli.api.weather.bean.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 天气支持按天枚举
 * @author bingchunmoli
 */
@Getter
@AllArgsConstructor
public enum WeatherDayEnums {
    /**
     * 和风天气支持按天查询
     */
    THREE_DAY(3),
    SEVEN_DAY(7),
    TEN_DAY(10),
    FIFTEEN_DAY(15);

    private final Integer day;
}
