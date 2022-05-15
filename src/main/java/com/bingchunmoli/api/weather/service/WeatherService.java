package com.bingchunmoli.api.weather.service;

import java.io.UnsupportedEncodingException;

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
     * @throws UnsupportedEncodingException 字符编码异常（URL无法编码）
     */
    String getWeatherByDay(Integer day, String location) throws UnsupportedEncodingException;


    /**
     * 获取实时天气
     *
     * @param address 地址
     * @return 天气信息
     * @throws UnsupportedEncodingException 不支持的URL编码异常
     */
    String getWeatherByNow(String address) throws UnsupportedEncodingException;
}
