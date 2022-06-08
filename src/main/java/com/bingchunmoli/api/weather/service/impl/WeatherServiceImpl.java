package com.bingchunmoli.api.weather.service.impl;

import cn.hutool.core.text.StrPool;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.bingchunmoli.api.properties.ApiKeyProperties;
import com.bingchunmoli.api.utils.IntegerUtil;
import com.bingchunmoli.api.utils.StringRedisUtil;
import com.bingchunmoli.api.weather.bean.WeatherVO;
import com.bingchunmoli.api.weather.bean.enums.WeatherCacheKey;
import com.bingchunmoli.api.weather.service.WeatherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.concurrent.TimeUnit;

/**
 * @author bingchunmoli
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WeatherServiceImpl implements WeatherService {
    private final ApiKeyProperties apiKeyProperties;
    private final StringRedisUtil stringRedisUtil;

    @Override
    public String getWeatherByDay(Integer day, String location) throws UnsupportedEncodingException {
        if (stringRedisUtil.isNotEnable()) {
            return "redis未启用，不支持此功能";
        }
        if (location.contains(StrPool.COMMA) || IntegerUtil.isInteget(location)) {
            // 按经维度查询 或者 id查询
            return getWeatherByDayCommon(day, location);
        }
        //按城市名查询，需要查询城市id
        return getWeatherByDayCommon(day, getLocationId(location));
    }

    @Override
    public String getWeatherByNow(String address) throws UnsupportedEncodingException {
        if (stringRedisUtil.isNotEnable()) {
            return "redis未启用，不支持此功能";
        }
        String redisCacheKey = new StringJoiner(":", WeatherCacheKey.BY_NOW.getKey(), ":" + address).toString();
        String redisCache = stringRedisUtil.get(redisCacheKey);
        return Optional.ofNullable(redisCache).orElse(doGetWeatherByNow(redisCacheKey, address));
    }

    /**
     * 获取按天天气(3,5,7)
     *
     * @param day      天数
     * @param location 地址
     * @return 天气信息
     * @throws UnsupportedEncodingException 无法编码异常
     */
    private String getWeatherByDayCommon(Integer day, String location) throws UnsupportedEncodingException {
        String redisCacheKey = new StringJoiner(":", WeatherCacheKey.BY_DAY.getKey(), ":" + location)
                .add(String.valueOf(day)).toString();
        String redisCache = stringRedisUtil.get(redisCacheKey);
        return Optional.ofNullable(redisCache).orElse(doGetWeatherByDay(redisCacheKey, day, location));
    }

    /**
     * 根据请求获取按天天气
     *
     * @param redisCacheKey 缓存的key
     * @param day           天数
     * @param location      地址
     * @return 天气信息
     * @throws UnsupportedEncodingException 无法编码异常
     */
    private String doGetWeatherByDay(String redisCacheKey, Integer day, String location) throws UnsupportedEncodingException {
        String joiner = "https://" +
                apiKeyProperties.getWeatherUri() +
                "/v7/weather/" +
                day +
                "d?key=" +
                apiKeyProperties.getWeatherKey() +
                "&location=" +
                getLocationId(location);
        String res = HttpUtil.get(joiner);
        stringRedisUtil.setEx(redisCacheKey, res, 12, TimeUnit.HOURS);
        return res;
    }


    /**
     * 请求接口获取按天天气并缓存
     *
     * @param redisCacheKey 缓存的key
     * @param location      地址
     * @return 天气
     * @throws UnsupportedEncodingException url编码异常
     */
    private String doGetWeatherByNow(String redisCacheKey, String location) throws UnsupportedEncodingException {
        String requestUrl = "https://" +
                apiKeyProperties.getWeatherUri() +
                "/v7/weather/now?key=" +
                apiKeyProperties.getWeatherKey() +
                "&location=" +
                getLocationId(location);
        String res = HttpUtil.get(requestUrl);
        stringRedisUtil.setEx(redisCacheKey, res, 1, TimeUnit.HOURS);
        return res;
    }

    /**
     * 根据location模糊查询LocationId根据相关度排序
     *
     * @param location 地区名称
     * @return 地区Id
     * @throws UnsupportedEncodingException 不支持的字符编码异常
     */
    private String getLocationId(String location) throws UnsupportedEncodingException {
        String redisCacheKey = new StringJoiner(":", WeatherCacheKey.LOOKUP.getKey(), location).toString();
        String redisCache = stringRedisUtil.get(redisCacheKey);
        String res = Optional.ofNullable(redisCache).orElse(doGetLocationId(redisCacheKey, location));
        WeatherVO weatherVO = JSON.parseObject(res, WeatherVO.class);
        if ("200".equalsIgnoreCase(weatherVO.getCode())) {
            return weatherVO.getLocation().get(0).getId();
        }
        if (log.isDebugEnabled()) {
            log.debug("和风天气状态码为非200, weatherVO: {}", weatherVO);
        }
        return "第三方错误";
    }

    /**
     * 请求接口获取locationId
     *
     * @param redisCacheKey 缓存的key
     * @param location      地址| 可以是中文地址
     * @return LocationId 根据相关度排序
     * @throws UnsupportedEncodingException 不支持的字符编码异常
     */
    private String doGetLocationId(String redisCacheKey, String location) throws UnsupportedEncodingException {
        String requestUrl = "https://" +
                apiKeyProperties.getWeatherGeoUri() +
                "/v2/city/lookup?key=" +
                apiKeyProperties.getWeatherKey() +
                "&location=" +
                URLEncoder.encode(location, "utf-8");
        String res = HttpUtil.get(requestUrl);
        stringRedisUtil.setEx(redisCacheKey, res, 24, TimeUnit.HOURS);
        return res;
    }

}
