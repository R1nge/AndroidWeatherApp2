package com.r1nge.weatherapp.domain.location

import com.r1nge.weatherapp.data.location.LocationData

interface LocationTracker {
    suspend fun getCurrentLocation(): LocationData?
}