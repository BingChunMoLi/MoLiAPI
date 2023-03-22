package com.bingchunmoli.api.weather.bean

import com.bingchunmoli.api.weather.bean.enums.Code
import lombok.Data


@Data
class WeatherDailyBean {
    var code: Code? = null
    var basic: Basic? = null
    var daily: List<DailyBean>? = null
    var refer: Refer? = null

    class DailyBean {
        var fxDate = ""
        var sunrise = ""
        var sunset = ""
        var moonRise = ""
        var moonSet = ""
        var moonPhase = ""
        var tempMax = ""
        var tempMin = ""
        var iconDay = ""
        var textDay = ""
        var iconNight = ""
        var textNight = ""
        var wind360Day = ""
        var windDirDay = ""
        var windScaleDay = ""
        var windSpeedDay = ""
        var wind360Night = ""
        var windDirNight = ""
        var windScaleNight = ""
        var windSpeedNight = ""
        var humidity = ""
        var precip = ""
        var pressure = ""
        var vis = ""
        var cloud = ""
        var uvIndex = ""
        var moonPhaseIcon = ""
    }

    class Basic {
        var updateTime: String? = null
        var fxLink: String? = null
    }

    class Refer {
        var sourcesList: List<String>? = null
        var licenseList: List<String>? = null
    }
}