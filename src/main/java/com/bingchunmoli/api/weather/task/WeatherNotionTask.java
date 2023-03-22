package com.bingchunmoli.api.weather.task;

import cn.hutool.core.util.StrUtil;
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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
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
        notionMessage(notifiedLocationMap, list);
    }

    private void notionMessage(Map<String, WeatherDailyBean> notifiedLocationMap,
                               List<WeatherSub> list) {
        Map<String, List<WeatherSub>> messageMap = list.stream().collect(Collectors.groupingBy(WeatherSub::getLocation));
        for (Map.Entry<String, WeatherDailyBean> entry : notifiedLocationMap.entrySet()) {
            List<WeatherSub> weatherSub = messageMap.get(entry.getKey());
            String messageStr = coverMessageToHtml(entry.getValue());
            if (log.isDebugEnabled()) {
                log.debug("message: {}", messageStr);
            }
            weatherSub.forEach(v ->
                    {
                        try {
                            mailUtil.sendHtmlMail(v.getEmail(), "天气不好,记得带伞呦😘😘😘", messageStr);
                        } catch (MessagingException e) {
                            throw new ApiTaskException(e);
                        }
                    }
            );
        }
    }

    private String coverMessageToHtml(WeatherDailyBean dailyBean) {
        return StrUtil.format("""
                        <!DOCTYPE html>
                        <html lang="en">
                            <head>
                                <meta charset="UTF-8">
                                <meta http-equiv="X-UA-Compatible" content="IE=edge">
                                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                                <title>天气不好,记得带伞呦😘😘😘</title>
                            </head>
                            <body style="height:100vh;">
                                响应式页面: <a href="{}">响应式页面</a>
                                <h1>{}: {}</h1>
                                <h2> 最高气温: {}</h2>
                                <h2> 最低气温: {}</h2>
                                <h1>{}: {}</h1>
                                <h2> 最高气温: {}</h2>
                                <h2> 最低气温: {}</h2>
                                <br/>
                                <br/>
                                <br/>
                                原始信息:
                                <code>{}</code>
                            </body>
                        </html>
                        """.trim(),
                dailyBean.getFxLink(),
                dailyBean.getDaily().get(0).getFxDate(),
                dailyBean.getDaily().get(0).getTextDay(),
                dailyBean.getDaily().get(0).getTempMax(),
                dailyBean.getDaily().get(0).getTempMin(),
                dailyBean.getDaily().get(1).getFxDate(),
                dailyBean.getDaily().get(1).getTextDay(),
                dailyBean.getDaily().get(1).getTempMax(),
                dailyBean.getDaily().get(1).getTempMin(),
                dailyBean);
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
