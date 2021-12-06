package com.bingchunmoli.api.weather.service.impl;

import cn.hutool.core.text.StrPool;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.bingchunmoli.api.properties.ApiKeyProperties;
import com.bingchunmoli.api.utils.IntegerUtil;
import com.bingchunmoli.api.utils.RedisUtil;
import com.bingchunmoli.api.weather.bean.WeatherVO;
import com.bingchunmoli.api.weather.bean.enums.WeatherCacheKey;
import com.bingchunmoli.api.weather.service.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.StringJoiner;
import java.util.concurrent.TimeUnit;

/**
 * @author bingchunmoli
 */
@Service
@RequiredArgsConstructor
public class WeatherServiceImpl implements WeatherService {
    private final ApiKeyProperties apiKeyProperties;
    private final RedisUtil redisUtil;

    @Override
    public String getWeather(Integer day, String location) {
        if (location.contains(StrPool.COMMA) || IntegerUtil.isInteget(location)) {
            // 按经维度查询 或者 id查询
            return getWeatherString(day, location);
        }
        //按城市名查询，需要查询城市id
        String locationId = getLocationId(location);
        return getWeatherString(day, locationId);
    }

    private String getWeatherString(Integer day, String location) {
        StringJoiner keyJoiner = new StringJoiner(":", WeatherCacheKey.BY_DAY.getKey(), ":"+location)
                .add(String.valueOf(day));
        String redisCache = redisUtil.get(keyJoiner.toString());
        if (redisCache != null) {
            return redisCache;
        }
        StringJoiner joiner = new StringJoiner("")
                .add("https://")
                .add(apiKeyProperties.getWeatherUri())
                .add("/v7/weather/")
                .add(String.valueOf(day))
                .add("d?key=")
                .add(apiKeyProperties.getWeatherKey())
                .add("&location=")
                .add(location);
        String response = HttpUtil.get(joiner.toString());
        redisUtil.setEx(keyJoiner.toString(), response, 24, TimeUnit.HOURS);
        return response;
    }

    /**
     * 根据location模糊查询
     * @param location 地区名称
     * @return 地区Id
     */
    private String getLocationId(String location) {
        StringJoiner keyJoiner = new StringJoiner(":", WeatherCacheKey.LOOKUP.getKey(), location);
        String redisCache = redisUtil.get(keyJoiner.toString());
            if (redisCache != null) {
            WeatherVO weatherVO = JSON.parseObject(redisCache, WeatherVO.class);
            return weatherVO.getLocation().get(0).getId();
        }
        StringJoiner joiner = new StringJoiner("")
                .add("https://")
                .add(apiKeyProperties.getWeatherGeoUri())
                .add("/v2/city/lookup?key=")
                .add(apiKeyProperties.getWeatherKey())
                .add("&location=")
                .add(location);
        String response = HttpUtil.get(joiner.toString());
        redisUtil.setEx(keyJoiner.toString(), response, 24, TimeUnit.HOURS);
        WeatherVO weatherVO = JSON.parseObject(response, WeatherVO.class);
        return weatherVO.getLocation().get(0).getId();
    }

}
