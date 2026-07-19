package com.bingchunmoli.api.weather.service.impl;

import cn.hutool.core.text.StrPool;
import cn.hutool.http.HttpUtil;
import cn.hutool.jwt.JWTUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.bingchunmoli.api.config.ApiConfig;
import com.bingchunmoli.api.utils.IntegerUtil;
import com.bingchunmoli.api.utils.SendMailUtil;
import com.bingchunmoli.api.weather.bean.WeatherSub;
import com.bingchunmoli.api.weather.bean.WeatherSubscribeParam;
import com.bingchunmoli.api.weather.bean.WeatherVO;
import com.bingchunmoli.api.weather.bean.enums.WeatherCacheKey;
import com.bingchunmoli.api.weather.mapper.WeatherMapper;
import com.bingchunmoli.api.weather.service.WeatherService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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
    private final ObjectMapper om;
    private final SendMailUtil sendMailUtil;
    private final HttpServletRequest request;

    @Override
    public String getWeatherByDay(Integer day, String location) {
        if (location.contains(StrPool.COMMA) || IntegerUtil.isInteger(location)) {
            // 按经维度查询 或者 id查询
            return getWeatherByDayCommon(day, location);
        }
        //按城市名查询，需要查询城市id
        return getWeatherByDayCommon(day, getLocationId(location));
    }

    @Override
    public String getWeatherByNow(String address) {
        String redisCacheKey = new StringJoiner(":", WeatherCacheKey.BY_NOW.getKey(), ":" + address).toString();
        return doGetWeatherByNow(redisCacheKey, address);
    }

    @Override
    public Boolean sendSubscribeMail(WeatherSubscribeParam param) {
        if (count(new LambdaQueryWrapper<WeatherSub>()
                .eq(WeatherSub::getEmail, param.getEmail())
                .eq(WeatherSub::getLocation, param.getLocation())) > 1) {
            return false;
        }
        HashMap<String, Object> map = new HashMap<>();
        map.put("email", param.getEmail());
        map.put("location", param.getLocation());
        String jwt = JWTUtil.createToken(map, apiConfig.getWeatherKey().getBytes());
        return sendMailUtil.sendMail(sendMailUtil.getDefaultFrom(), param.getEmail(),
                "MoLiAPI天气订阅确认", "地址: " + param.getLocation() + "\n https://" + request.getServerName() + "/weather/callback?param=" + jwt + " \n 如果不是本人订阅你无需回应\n 如已订阅, <span style='color: red;'>推订</span>请访问: https://" + request.getServerName() + "/weather/unsub?param=" + jwt);
    }

    /**
     * 获取按天天气(3,5,7)
     *
     * @param day      天数
     * @param location 地址
     * @return 天气信息
     */
    private String getWeatherByDayCommon(Integer day, String location) {
        String redisCacheKey = new StringJoiner(":", WeatherCacheKey.BY_DAY.getKey(), ":" + location)
                .add(String.valueOf(day)).toString();
        return doGetWeatherByDay(redisCacheKey, day, location);
    }

    /**
     * 根据请求获取按天天气
     *
     * @param redisCacheKey 缓存的key
     * @param day           天数
     * @param location      地址
     * @return 天气信息
     */
    private String doGetWeatherByDay(String redisCacheKey, Integer day, String location) {
        String joiner = "https://" +
                apiConfig.getWeatherUri() +
                "/v7/weather/" +
                day +
                "d?key=" +
                apiConfig.getWeatherKey() +
                "&location=" +
                getLocationId(location);
        return HttpUtil.get(joiner);
    }


    /**
     * 请求接口获取按天天气并缓存
     *
     * @param redisCacheKey 缓存的key
     * @param location      地址
     * @return 天气
     */
    private String doGetWeatherByNow(String redisCacheKey, String location) {
        String requestUrl = "https://" +
                apiConfig.getWeatherUri() +
                "/v7/weather/now?key=" +
                apiConfig.getWeatherKey() +
                "&location=" +
                getLocationId(location);
        return HttpUtil.get(requestUrl);
    }

    /**
     * 根据location模糊查询LocationId根据相关度排序
     *
     * @param location 地区名称
     * @return 地区Id
     */
    private String getLocationId(String location) {
        String redisCacheKey = new StringJoiner(":", WeatherCacheKey.LOOKUP.getKey(), location).toString();
        String res = doGetLocationId(redisCacheKey, location);
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
     */
    private String doGetLocationId(String redisCacheKey, String location) {
        String requestUrl = "https://" +
                apiConfig.getWeatherGeoUri() +
                "/v2/city/lookup?key=" +
                apiConfig.getWeatherKey() +
                "&location=" +
                URLEncoder.encode(location, StandardCharsets.UTF_8);
        return HttpUtil.get(requestUrl);
    }

}