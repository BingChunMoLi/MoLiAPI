package com.bingchunmoli.api.weather.task

import com.bingchunmoli.api.utils.LogUtil.debug
import com.bingchunmoli.api.utils.LogUtil.info
import com.bingchunmoli.api.weather.bean.WeatherDailyBean
import com.bingchunmoli.api.weather.bean.enums.Code
import com.bingchunmoli.api.weather.service.WeatherService
import com.fasterxml.jackson.databind.ObjectMapper
import lombok.Data
import lombok.RequiredArgsConstructor
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
@RequiredArgsConstructor
class WeatherNotionTask (private val weatherService: WeatherService, private val om : ObjectMapper) {

    @Data
    class obj{
        var obj : Array<WeatherDailyBean>? = null
    }

    @Scheduled(cron = "0 30 18 * * ?")
    fun notion(){
        val list = weatherService.list()
        val locationList = list.stream().map { it.location }.distinct().toList()
        val map = HashMap<String, List<WeatherDailyBean.DailyBean>>()
        for (location in locationList) {
            val weather = weatherService.getWeatherByDay(3, location)
            info("weather: {}", weather)
            val weatherDailyBean = om.readValue(weather, obj::class.java)
            if (Code.OK.code != weatherDailyBean.obj?.first()?.code?.code) {
                info("locationList: {}, weatherDailyBean: {}", locationList, weatherDailyBean)
            }
            // FIXME: let null map {}
            val let = weatherDailyBean.obj?.first()?.daily?.let { map.put(location, it) }
        }
        debug("WeatherDailyMap: {}", map)
        val filter = map.filter { (_, v) -> v.first().iconDay.toInt() >= 300 }
        info("filter: {}, map: {}",filter, map)
//        TODO 通知
        TODO("通知")
    }
}