package com.bingchunmoli.api.weather.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTPayload;
import cn.hutool.jwt.JWTUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bingchunmoli.api.bean.ResultVO;
import com.bingchunmoli.api.bean.enums.CodeEnum;
import com.bingchunmoli.api.config.ApiConfig;
import com.bingchunmoli.api.exception.ApiParamException;
import com.bingchunmoli.api.ip.controller.IpController;
import com.bingchunmoli.api.utils.RedisUtil;
import com.bingchunmoli.api.weather.bean.WeatherSub;
import com.bingchunmoli.api.weather.bean.WeatherSubscribeParam;
import com.bingchunmoli.api.weather.bean.enums.WeatherCacheKey;
import com.bingchunmoli.api.weather.bean.enums.WeatherDayEnums;
import com.bingchunmoli.api.weather.service.WeatherService;
import com.jthinking.common.util.ip.IPInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


/**
 * 天气
 *
 * @author bingchunmoli
 */
@RestController
@Tag(name = "和风天气")
@RequiredArgsConstructor
@RequestMapping("weather")
@ConditionalOnProperty(prefix = "moli.apiConfig", name = {"weatherKey"})
public class WeatherController {
    private final WeatherService weatherService;
    private final IpController ipController;
    private final RedisUtil redisUtil;
    private final ApiConfig apiConfig;

    /**
     * 按天查询天气
     *
     * @param day      天数(3,7,10,15)|7
     * @param location 可以是经维度也可以是locationId并且可以是城市名称
     * @return 天气数据
     */
    @GetMapping("byDay")
    @Operation(summary = "按天查询天气", description = "可以查询未来n天的天气,n的取值为3,7,10,15")
    public String getWeatherByDay(@RequestParam(defaultValue = "3") Integer day, @RequestParam String location, HttpServletRequest request) throws IOException {
        if (StrUtil.isBlank(location)) {
            IPInfo ipInfo = ipController.getAddress(request);
            location = ipInfo.getLng() + "," + ipInfo.getLat();
        }
        if (!Objects.equals(day, WeatherDayEnums.THREE_DAY.getDay()) && !Objects.equals(day, WeatherDayEnums.SEVEN_DAY.getDay())) {
            throw new ApiParamException("暂不支持的参数");
        }
        return weatherService.getWeatherByDay(day, location);
    }


    /**
     * 查找当前天气
     *
     * @param request 获取请求IP
     * @return 当前实时天气
     * @throws IOException 内存异常或者字符编码异常
     */
    @GetMapping("now")
    @Operation(summary = "查询当日的天气")
    public String getWeatherByNow(HttpServletRequest request) throws IOException {
        IPInfo ipInfo = ipController.getAddress(request);
        return weatherService.getWeatherByNow(ipInfo.getLng() + "," + ipInfo.getLat());
    }

    /**
     * 订阅天气通知
     * @param param email和location 邮件通知地址和订阅天气地址
     * @return 是否成功
     */
    @PostMapping("sub")
    @Operation(summary = "订阅天气通知", description = "订阅天气，如果未来三天内天气可能出现下雨或下雪等天气会发送邮件通知")
    public ResultVO<Boolean> subscribe(@Valid @RequestBody WeatherSubscribeParam param) {
        String key = WeatherCacheKey.SUBSCRIBE.getKey() + param.getEmail();
        Object object = redisUtil.getObject(key);
        if (object != null) {
            return new ResultVO<>("A0001", "已经发送过了，请稍后再试", false);
        }
        Boolean res = weatherService.sendSubscribeMail(param);
        if (res) {
            redisUtil.setObject(key, 1, 15, TimeUnit.MINUTES);
            return ResultVO.ok(true);
        }
        return new ResultVO<>(CodeEnum.FAILURE, false);
    }

    /**
     * 取消订阅天气通知
     * @param param jwt
     * @return 是否成功
     */
    @GetMapping("unSub")
    @Operation(summary = "取消订阅天气")
    public ResultVO<Boolean> unSubscribe(String param){
        if (JWTUtil.verify(param, apiConfig.getWeatherKey().getBytes())) {
            JWT jwt = JWTUtil.parseToken(param);
            JWTPayload payload = jwt.getPayload();
            JSONObject jsonObject = payload.getClaimsJson();
            String location = jsonObject.getStr("location");
            String email = jsonObject.getStr("email");
            return ResultVO.ok(weatherService.remove(new LambdaQueryWrapper<WeatherSub>()
                    .eq(WeatherSub::getLocation, location)
                    .eq(WeatherSub::getEmail, email)));
        }
        return ResultVO.ok(false);
    }

    /**
     * 天气订阅邮件回调
     * @param param jwt
     * @return 是否成功
     */
    @GetMapping("callback")
    @Operation(summary = "取消订阅天气的确认接口")
    public ResultVO<Boolean> callback(String param) {
        if (JWTUtil.verify(param, apiConfig.getWeatherKey().getBytes())) {
            JWT jwt = JWTUtil.parseToken(param);
            JWTPayload payload = jwt.getPayload();
            JSONObject jsonObject = payload.getClaimsJson();
            String location = jsonObject.getStr("location");
            String email = jsonObject.getStr("email");
            return ResultVO.ok(weatherService.save(WeatherSub.builder()
                    .email(email)
                    .location(location)
                    .build()));
        }
        return ResultVO.ok(false);
    }
}
