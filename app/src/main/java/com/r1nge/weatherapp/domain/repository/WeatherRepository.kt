package com.r1nge.weatherapp.domain.repository

import com.r1nge.weatherapp.domain.util.Resource
import com.r1nge.weatherapp.domain.weather.WeatherInfo

interface WeatherRepository {
    suspend fun getWeatherData(lat: Double, long: Double): Resource<WeatherInfo>
}