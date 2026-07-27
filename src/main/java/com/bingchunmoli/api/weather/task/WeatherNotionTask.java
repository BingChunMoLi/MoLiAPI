package com.bingchunmoli.api.weather.task;

import com.bingchunmoli.api.exception.ApiTaskException;
import com.bingchunmoli.api.utils.SendMailUtil;
import com.bingchunmoli.api.weather.bean.WeatherDailyBean;
import com.bingchunmoli.api.weather.bean.WeatherSub;
import com.bingchunmoli.api.weather.bean.enums.Code;
import com.bingchunmoli.api.weather.service.WeatherService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

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
@ConditionalOnProperty(prefix = "moli.features.tasks", name = "weather-notification", havingValue = "true")
@RequiredArgsConstructor
public class WeatherNotionTask {
    private final WeatherService weatherService;
    private final ObjectMapper om;
    private final SendMailUtil mailUtil;


    @Scheduled(cron = "0 30 18 * * ?")
    public void notion() {
        final List<WeatherSub> list = weatherService.list();
        final Map<String, WeatherDailyBean> notifiedLocationMap = getNotifiedLocation(list);
        notionMessage(notifiedLocationMap, list);
    }

    @Async
    public void notionMessage(
            final Map<String, WeatherDailyBean> notifiedLocationMap,
            final List<WeatherSub> list
    ) {
        final Map<String, List<WeatherSub>> messageMap = list.stream()
                .collect(Collectors.groupingBy(WeatherSub::getLocation));
        for (final Map.Entry<String, WeatherDailyBean> entry : notifiedLocationMap.entrySet()) {
            final List<WeatherSub> weatherSub = messageMap.get(entry.getKey());
            final WeatherDailyBean value = entry.getValue();
            if (log.isDebugEnabled()) {
                log.debug("key: {}, message: {}", entry.getKey(), entry.getValue());
            }
            final Context context = new Context();
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
                    log.error("发送邮件异常: ", e);
                    throw new ApiTaskException(e);
                }
            });
        }
    }

    private Map<String, WeatherDailyBean> getNotifiedLocation(final List<WeatherSub> list) {
        final List<String> locationList = list.stream().map(WeatherSub::getLocation).distinct().toList();
        final HashMap<String, WeatherDailyBean> map = new HashMap<>();
        for (final String location : locationList) {
            final JsonNode weather = weatherService.getWeatherByDay(3, location);
            final WeatherDailyBean weatherDailyBean;
            try {
                if (log.isDebugEnabled()) {
                    log.debug("location: {}, weather: {}", location, weather);
                }
                weatherDailyBean = om.treeToValue(weather, WeatherDailyBean.class);
            } catch (JacksonException e) {
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
