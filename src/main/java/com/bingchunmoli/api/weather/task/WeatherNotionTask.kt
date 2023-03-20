package com.bingchunmoli.api.weather.task

import cn.hutool.json.JSONUtil
import com.bingchunmoli.api.weather.service.WeatherService
import lombok.RequiredArgsConstructor
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
@RequiredArgsConstructor
class WeatherNotionTask (private val weatherService: WeatherService) {

    @Scheduled(cron = "0 0 0 * * ?")
    fun notion(){
        val list = weatherService.list()
        val locationList = list.stream().map { it.location }.distinct().toList()
        for (location in locationList) {
            val weather = weatherService.getWeatherByDay(1, "杭州")
            val root = JSONUtil.parseObj(weather)
            val dailyArray = root.getJSONArray("daily")
            val currDay = dailyArray.getJSONObject(0)
            val tomorrow = dailyArray.getJSONObject(1)
            // Todo 创建天气对象，转为List
            currDay.getStr("fxDate")//预报日期
            currDay.getStr("iconDay")//图标
            currDay.getStr("textDay")//文字天气
            tomorrow.getStr("fxDate")//预报日期
            tomorrow.getStr("iconDay")//图标
            tomorrow.getStr("textDay")//文字天气
            TODO()
        }
        
    }
}