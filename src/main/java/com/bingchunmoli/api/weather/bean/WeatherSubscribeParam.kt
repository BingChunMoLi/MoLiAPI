package com.bingchunmoli.api.weather.bean

import jakarta.validation.constraints.Email

data class WeatherSubscribeParam constructor(@Email val email: String, val location: String)
