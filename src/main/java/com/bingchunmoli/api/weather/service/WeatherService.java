package com.bingchunmoli.api.weather.service;

import com.baomidou.mybatisplus.spring.service.IService;
import com.bingchunmoli.api.weather.bean.WeatherSub;
import com.bingchunmoli.api.weather.bean.WeatherSubscribeParam;
import tools.jackson.databind.JsonNode;

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
     */
    JsonNode getWeatherByDay(final Integer day, final String location);


    /**
     * 获取实时天气
     *
     * @param address 地址
     * @return 天气信息
     */
    JsonNode getWeatherByNow(final String address);

    /**
     * 发送确认订阅邮件
     * @param param 地址和邮箱
     * @return 是否发送成功
     */
    Boolean sendSubscribeMail(final WeatherSubscribeParam param);
}
