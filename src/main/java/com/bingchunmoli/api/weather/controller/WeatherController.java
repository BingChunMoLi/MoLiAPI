package com.bingchunmoli.api.weather.controller;

import cn.hutool.core.util.StrUtil;
import com.bingchunmoli.api.exception.ApiParamException;
import com.bingchunmoli.api.ip.controller.IpController;
import com.bingchunmoli.api.weather.bean.enums.WeatherDayEnums;
import com.bingchunmoli.api.weather.service.WeatherService;
import com.jthinking.common.util.ip.IPInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Objects;


/**
 * 天气
 *
 * @author bingchunmoli
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("weather")
@ConditionalOnProperty(prefix = "moli.api-config", name = {"weather-key", "weather-uri", "weather-geo-uri"})
public class WeatherController {

    private final WeatherService weatherService;
    private final IpController ipController;

    /**
     * 按天查询天气
     *
     * @param day      天数(3,7,10,15)|7
     * @param location 可以是经维度也可以是locationId并且可以是城市名称
     * @return 天气数据
     */
    @GetMapping("byDay")
    public String getWeatherByDay(@RequestParam(defaultValue = "3") Integer day, @RequestParam String location, HttpServletRequest request) throws IOException {
        if (StrUtil.isBlank(location)) {
            IPInfo ipInfo = ipController.getAddress(request);
            location = ipInfo.getLng() + "," + ipInfo.getLat();
        }
        if (! Objects.equals(day, WeatherDayEnums.THREE_DAY.getDay()) && ! Objects.equals(day, WeatherDayEnums.SEVEN_DAY.getDay())) {
            throw new ApiParamException("暂不支持的参数");
        }
        return weatherService.getWeatherByDay(day, location);
    }


    /**
     * 查找当前天气
     *
     * @param request 获取请求IP
     * @return 当前实时天气
     * @throws IOException            内存异常或者字符编码异常
     */
    @GetMapping("now")
    public String getWeatherByNow(HttpServletRequest request) throws IOException {
        IPInfo ipInfo = ipController.getAddress(request);
        return weatherService.getWeatherByNow(ipInfo.getLng() + "," + ipInfo.getLat());
    }

}
