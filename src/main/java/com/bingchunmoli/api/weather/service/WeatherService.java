package com.bingchunmoli.api.weather.service;

/**
 * @author bingchunmoli
 */
public interface WeatherService {
    /**
     * 根据天数和地址查询天气
     *
     * @param day      天气
     * @param location 地址，可以是经维度也可以是locationId并且可以是城市名称
     * @return 查询出的天气数据
     */
    String getWeather(Integer day, String location);
}
