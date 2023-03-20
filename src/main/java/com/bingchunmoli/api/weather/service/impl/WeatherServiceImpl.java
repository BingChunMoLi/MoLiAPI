package com.bingchunmoli.api.weather.service.impl;

import cn.hutool.core.text.StrPool;
import cn.hutool.http.HttpUtil;
import cn.hutool.jwt.JWTUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bingchunmoli.api.properties.ApiConfig;
import com.bingchunmoli.api.utils.IntegerUtil;
import com.bingchunmoli.api.utils.RedisUtil;
import com.bingchunmoli.api.utils.SendMailUtil;
import com.bingchunmoli.api.utils.StringRedisUtil;
import com.bingchunmoli.api.weather.bean.WeatherSubscribeParam;
import com.bingchunmoli.api.weather.bean.WeatherVO;
import com.bingchunmoli.api.weather.bean.enums.WeatherCacheKey;
import com.bingchunmoli.api.weather.bean.WeatherSub;
import com.bingchunmoli.api.weather.mapper.WeatherMapper;
import com.bingchunmoli.api.weather.service.WeatherService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.concurrent.TimeUnit;

/**
 * 继承订阅天气
 * @author bingchunmoli
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WeatherServiceImpl extends ServiceImpl<WeatherMapper, WeatherSub> implements WeatherService {
    private final ApiConfig apiConfig;
    private final StringRedisUtil stringRedisUtil;
    private final ObjectMapper om;
    private final SendMailUtil sendMailUtil;
    private final HttpServletRequest request;
    private final RedisUtil redisUtil;

    @Override
    public String getWeatherByDay(Integer day, String location) throws UnsupportedEncodingException, JsonProcessingException {
        if (location.contains(StrPool.COMMA) || IntegerUtil.isInteget(location)) {
            // 按经维度查询 或者 id查询
            return getWeatherByDayCommon(day, location);
        }
        //按城市名查询，需要查询城市id
        return getWeatherByDayCommon(day, getLocationId(location));
    }

    @Override
    public String getWeatherByNow(String address) throws UnsupportedEncodingException, JsonProcessingException {
        String redisCacheKey = new StringJoiner(":", WeatherCacheKey.BY_NOW.getKey(), ":" + address).toString();
        String redisCache = stringRedisUtil.get(redisCacheKey);
        return Optional.ofNullable(redisCache).orElse(doGetWeatherByNow(redisCacheKey, address));
    }

    @Override
    public Boolean sendSubscribeMail(WeatherSubscribeParam param) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("email", param.getEmail());
        map.put("location", param.getLocation());
        String jwt = JWTUtil.createToken(map, apiConfig.getWeatherKey().getBytes());
        return sendMailUtil.sendMail(sendMailUtil.getDefaultFrom(), param.getEmail(),
                "MoLiAPI天气订阅确认", "地址: " + param.getLocation() + "\n https://" + request.getServerName() + "/weather/callback?param=" + jwt + " \n 如果不是本人订阅你无需回应");
    }

    /**
     * 获取按天天气(3,5,7)
     *
     * @param day      天数
     * @param location 地址
     * @return 天气信息
     * @throws UnsupportedEncodingException 无法编码异常
     */
    private String getWeatherByDayCommon(Integer day, String location) throws UnsupportedEncodingException, JsonProcessingException {
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
    private String doGetWeatherByDay(String redisCacheKey, Integer day, String location) throws UnsupportedEncodingException, JsonProcessingException {
        String joiner = "https://" +
                apiConfig.getWeatherUri() +
                "/v7/weather/" +
                day +
                "d?key=" +
                apiConfig.getWeatherKey() +
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
    private String doGetWeatherByNow(String redisCacheKey, String location) throws UnsupportedEncodingException, JsonProcessingException {
        String requestUrl = "https://" +
                apiConfig.getWeatherUri() +
                "/v7/weather/now?key=" +
                apiConfig.getWeatherKey() +
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
     * @throws JsonProcessingException      JSON转换异常
     */
    private String getLocationId(String location) throws UnsupportedEncodingException, JsonProcessingException {
        String redisCacheKey = new StringJoiner(":", WeatherCacheKey.LOOKUP.getKey(), location).toString();
        String redisCache = stringRedisUtil.get(redisCacheKey);
        String res = Optional.ofNullable(redisCache).orElse(doGetLocationId(redisCacheKey, location));
        WeatherVO weatherVO = om.readValue(res, WeatherVO.class);
        if (String.valueOf(HttpStatus.OK.value()).equalsIgnoreCase(weatherVO.getCode())) {
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
                apiConfig.getWeatherGeoUri() +
                "/v2/city/lookup?key=" +
                apiConfig.getWeatherKey() +
                "&location=" +
                URLEncoder.encode(location, "utf-8");
        String res = HttpUtil.get(requestUrl);
        stringRedisUtil.setEx(redisCacheKey, res, 24, TimeUnit.HOURS);
        return res;
    }

}
