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
import org.jetbrains.annotations.NotNull;
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
        try {
            notionMessage(notifiedLocationMap, list);
        } catch (JsonProcessingException e) {
            throw new ApiTaskException(e);
        }
    }

    private void notionMessage(Map<String, WeatherDailyBean> notifiedLocationMap,
                               List<WeatherSub> list) throws JsonProcessingException {
        Map<String, List<WeatherSub>> messageMap = list.stream().collect(Collectors.groupingBy(WeatherSub::getLocation));
        for (Map.Entry<String, WeatherDailyBean> entry : notifiedLocationMap.entrySet()) {
            List<WeatherSub> weatherSub = messageMap.get(entry.getKey());
            String messageStr = coverMessageToHtml(entry.getKey(), entry.getValue());
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

    private String coverMessageToHtml(String location, @NotNull WeatherDailyBean dailyBean) throws JsonProcessingException {
        WeatherDailyBean.DailyBean today = dailyBean.getDaily().get(0);
        WeatherDailyBean.DailyBean tomorrow = dailyBean.getDaily().get(1);
        WeatherDailyBean.DailyBean dayAfterTomorrow = dailyBean.getDaily().get(2);
        return StrUtil.format("""
                        <!DOCTYPE html>
                        <html lang="en">
                            <head>
                                <meta charset="UTF-8">
                                <meta http-equiv="X-UA-Compatible" content="IE=edge">
                                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                                <title>天气不好,记得带伞呦😘😘😘</title>
                                <link rel="stylesheet" href="https://obs.bingchunmoli.com/qweather-icons.css">
                            </head>
                            <body style="height:100vh;width: 100%;">
                                    <div style="margin: 0 auto; display: flex;align-items: center;justify-content: center;">
                                        <td>地区: {}</td>
                                    </div>
                                    <div style="width: 100%; display: flex; flex-direction: row; justify-content: space-around; align-items: stretch;">
                                        <div>
                                            <div>日期: </div>
                                            <div>图标:</div>
                                            <div>最低气温/最高气温: </div>
                                            <div>天气: </div>
                                        </div>
                                        <div>
                                            <div>{}&nbsp;</div>
                                            <div><i class="qi-{}"></i></div>
                                            <div>{}°c/{}°c</div>
                                            <div>{}</div>
                                        </div>
                                        <div>
                                            <div>{}&nbsp;</div>
                                            <div><i class="qi-{}"></i></div>
                                            <div>{}°c/{}°c</div>
                                            <div>{}</div>
                                        </div>
                                        <div>
                                            <div>{}&nbsp;</div>
                                            <div><i class="qi-{}"></i></div>
                                            <div>{}°c/{}°c</div>
                                            <div>{}</div>
                                        </div>
                                </div>
                                原始信息:
                                <code>{}</code>
                            </body>
                        </html>
                        """.trim(),
                location,
                today.getFxDate(),
                today.getIconDay(),
                today.getTempMin(),
                today.getTempMax(),
                today.getTextDay(),
                tomorrow.getFxDate(),
                tomorrow.getIconDay(),
                tomorrow.getTempMin(),
                tomorrow.getTempMax(),
                tomorrow.getTextDay(),
                dayAfterTomorrow.getFxDate(),
                dayAfterTomorrow.getIconDay(),
                dayAfterTomorrow.getTempMin(),
                dayAfterTomorrow.getTempMax(),
                dayAfterTomorrow.getTextDay(),
                om.writeValueAsString(dailyBean));
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