package com.r1nge.weatherapp.data.mappers

import com.r1nge.weatherapp.data.remote.WeatherDataDto
import com.r1nge.weatherapp.data.remote.WeatherDto
import com.r1nge.weatherapp.domain.weather.WeatherData
import com.r1nge.weatherapp.domain.weather.WeatherInfo
import com.r1nge.weatherapp.domain.weather.WeatherType
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private data class IndexedWeatherData(
    val index: Int,
    val data: WeatherData
)

fun WeatherDataDto.toWeatherDataMap(): Map<Int, List<WeatherData>> {
    return time.mapIndexed { index, time ->
        val temperature = temperatures[index]
        val weatherCode = weatherCodes[index]
        val windSpeed = windSpeeds[index]
        val pressure = pressures[index]
        val humidity = humidities[index]
        IndexedWeatherData(
            index = index,
            data = WeatherData(
                time = LocalDateTime.parse(time, DateTimeFormatter.ISO_DATE_TIME),
                temperatureCelsius = temperature,
                pressure = pressure,
                windSpeed = windSpeed,
                humidity = humidity,
                weatherType = WeatherType.fromWMO(weatherCode)
            )
        )
    }.groupBy {
        it.index / 24
    }.mapValues {
        it.value.map { it.data }
    }
}

fun WeatherDto.toWeatherInfo(): WeatherInfo {
    val weatherDataMap = weatherData.toWeatherDataMap()
    val now = LocalDateTime.now()

    val hour: Int
    var dayOffset = 0
    if (now.minute < 30) {
        hour = now.hour
    } else {
        if (now.hour + 1 == 24) {
            dayOffset = 1
            hour = (now.hour + 1) % 24
        } else {
            hour = now.hour + 1
        }
    }

    val currentWeatherData = weatherDataMap[dayOffset]?.find {
        it.time.hour == hour
    }
    return WeatherInfo(
        weatherDataPerDay = weatherDataMap,
        currentWeatherData = currentWeatherData
    )
}
