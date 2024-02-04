package com.bingchunmoli.api.weather.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bingchunmoli.api.weather.bean.WeatherSub;
import com.bingchunmoli.api.weather.bean.WeatherSubscribeParam;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.UnsupportedEncodingException;

/**
 * 继承天气订阅接口
 * @author bingchunmoli
 */
public interface WeatherService extends IService<WeatherSub> {
    /**
     * 根据天数和地址查询天气
     *
     * @param day      天气
     * @param location 地址，可以是经维度也可以是locationId并且可以是城市名称
     * @return 查询出的天气数据
     * @throws UnsupportedEncodingException 字符编码异常（URL无法编码）
     */
    String getWeatherByDay(Integer day, String location) throws UnsupportedEncodingException, JsonProcessingException;


    /**
     * 获取实时天气
     *
     * @param address 地址
     * @return 天气信息
     * @throws UnsupportedEncodingException 不支持的URL编码异常
     */
    String getWeatherByNow(String address) throws UnsupportedEncodingException, JsonProcessingException;

    /**
     * 发送确认订阅邮件
     * @param param 地址和邮箱
     * @return 是否发送成功
     */
    Boolean sendSubscribeMail(WeatherSubscribeParam param);
}
