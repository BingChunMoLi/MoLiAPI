package com.bingchunmoli.api.weather.task;

import com.bingchunmoli.api.exception.ApiTaskException;
import com.bingchunmoli.api.utils.SendMailUtil;
import com.bingchunmoli.api.weather.bean.WeatherDailyBean;
import com.bingchunmoli.api.weather.bean.WeatherSub;
import com.bingchunmoli.api.weather.bean.enums.Code;
import com.bingchunmoli.api.weather.service.WeatherService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author bingchunmoli
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WeatherNotionTask {
    private final WeatherService weatherService;
    private final ObjectMapper om;
    private final SendMailUtil mailUtil;


    @Scheduled(cron = "0 30 18 * * ?")
    public void notion() {
        List<WeatherSub> list = weatherService.list();
        Map<String, WeatherDailyBean> notifiedLocationMap = getNotifiedLocation(list);
        try {
            notionMessage(notifiedLocationMap, list);
        } catch (JsonProcessingException e) {
            throw new ApiTaskException(e);
        }
    }

    @Async
    public void notionMessage(Map<String, WeatherDailyBean> notifiedLocationMap,
                               List<WeatherSub> list) throws JsonProcessingException {
        Map<String, List<WeatherSub>> messageMap = list.stream().collect(Collectors.groupingBy(WeatherSub::getLocation));
        for (Map.Entry<String, WeatherDailyBean> entry : notifiedLocationMap.entrySet()) {
            List<WeatherSub> weatherSub = messageMap.get(entry.getKey());
            WeatherDailyBean value = entry.getValue();
            if (log.isDebugEnabled()) {
                log.debug("key: {}, message: {}", entry.getKey(), entry.getValue());
            }
            Context context = new Context();
            weatherSub.forEach(v -> {
                context.setLocale(Locale.CHINA);
                context.setVariable("email", v.getEmail());
                context.setVariable("location", v.getLocation());
                context.setVariable("updateTime", value.getUpdateTime());
                context.setVariable("fxLink", value.getFxLink());
                context.setVariable("data", value.getDaily());
                try {
                    mailUtil.sendHtmlMail(v.getEmail(), "天气不好,记得带伞呦😘😘😘", "WeatherNotion", context);
                } catch (MessagingException e) {
                    throw new ApiTaskException(e);
                }
            });
        }
    }

    private Map<String, WeatherDailyBean> getNotifiedLocation(List<WeatherSub> list) {
        List<String> locationList = list.stream().map(WeatherSub::getLocation).distinct().toList();
        HashMap<String, WeatherDailyBean> map = new HashMap<>();
        for (String location : locationList) {
            String weather;
            WeatherDailyBean weatherDailyBean;
            try {
                weather = weatherService.getWeatherByDay(3, location);
                if (log.isDebugEnabled()) {
                    log.debug("location: {}, weather: {}", location, weather);
                }
                weatherDailyBean = om.readValue(weather, WeatherDailyBean.class);
            } catch (UnsupportedEncodingException | JsonProcessingException e) {
                throw new ApiTaskException(e);
            }
            if (!Code.OK.getCode().equalsIgnoreCase(weatherDailyBean.getCode())) {
                if (log.isInfoEnabled()) {
                    log.info("locationList: {}, weatherDailyBean: {}", locationList, weatherDailyBean);
                }
            }
            if (log.isDebugEnabled()) {
                log.debug("weatherDailyBean: {}", weatherDailyBean);
            }
            if (weatherDailyBean.getDaily().stream().anyMatch(v -> Integer.parseInt(v.getIconDay()) >= 300)) {
                map.put(location, weatherDailyBean);
            }
        }
        if (log.isDebugEnabled()) {
            log.debug("WeatherDailyMap: {}", map);
        }
        return map;
    }

}
