package com.bingchunmoli.api.weather;

import com.bingchunmoli.api.weather.controller.WeatherController;
import com.bingchunmoli.api.weather.service.WeatherService;
import com.jthinking.common.util.ip.IPInfo;
import com.jthinking.common.util.ip.IPInfoUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.UnsupportedEncodingException;

@Slf4j
@SpringBootTest
public class WeatherTest {
    @Autowired(required = false)
    private WeatherController weatherController;
    @Autowired
    private WeatherService weatherService;

    @AfterEach
    void afterAll(){

    }

    @Test
    void enableConfigTest() {
        log.info("weatherController: {}", weatherController);
        Assertions.assertNull(weatherController, "天气接口未启用");
    }

    @Test
    void serviceTest() throws UnsupportedEncodingException {
        IPInfo ipInfo = IPInfoUtils.getIpInfo("");
        String address = weatherService.getWeatherByNow(ipInfo.getLng() + "," + ipInfo.getLat());
        log.info("addressResult: {}", address);

    }
}
