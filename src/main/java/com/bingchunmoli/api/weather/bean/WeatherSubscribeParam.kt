package com.bingchunmoli.api.weather.bean

import jakarta.validation.constraints.Email

data class WeatherSubscribeParam (@Email val email: String, val location: String)
 
