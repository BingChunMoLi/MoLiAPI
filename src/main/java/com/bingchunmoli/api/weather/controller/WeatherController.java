package com.bingchunmoli.api.weather.controller;

import cn.hutool.core.util.StrUtil;
import com.bingchunmoli.api.exception.ApiParamException;
import com.bingchunmoli.api.weather.bean.enums.WeatherDayEnums;
import com.bingchunmoli.api.weather.service.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;


/**
 * 天气
 * @author bingchunmoli
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("weather")
public class WeatherController {

    private final WeatherService weatherService;

    /**
     * 按天查询天气
     * @param day 天数(3,7,10,15)|7
     * @param location 可以是经维度也可以是locationId并且可以是城市名称
     * @return 天气数据
     */
    @GetMapping("byDay")
    public String getWeather(@RequestParam Integer day, @RequestParam String location) {
        if (StrUtil.isBlank(location)) {
            throw new ApiParamException("地址为空");
        }
        if (! Objects.equals(day, WeatherDayEnums.THREE_DAY.getDay()) && ! Objects.equals(day, WeatherDayEnums.SEVEN_DAY.getDay())) {
            throw new ApiParamException("暂不支持的参数");
        }
        return weatherService.getWeather(day, location);
    }


}
