package com.r1nge.weatherapp.presentation

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.r1nge.weatherapp.domain.location.LocationTracker
import com.r1nge.weatherapp.domain.repository.WeatherRepository
import com.r1nge.weatherapp.domain.util.Resource
import kotlinx.coroutines.launch
import javax.inject.Inject

class WeatherViewModel @Inject constructor(
    private val repository: WeatherRepository,
    private val locationTracker: LocationTracker
) : ViewModel() {

    var state by mutableStateOf(WeatherState())
        private set

    fun loadWeatherInfo() {
        viewModelScope.launch {
            state = state.copy(
                isLoading = false,
                error = null
            )
            locationTracker.getCurrentLocation()?.let { locationData ->
                when (val result =
                    repository.getWeatherData(locationData.altitude, locationData.longitude)) {
                    is Resource.Success -> state = state.copy(
                        weatherInfo = result.data,
                        isLoading = false,
                        error = null
                    )

                    is Resource.Error -> {
                        state = state.copy(
                            weatherInfo = null,
                            isLoading = false,
                            error = result.message
                        )
                    }
                }
            } ?: kotlin.run {
                state = state.copy(
                    isLoading = false,
                    error = "Couldn't retrieve location. Make sure to grant permission and enable GPS"
                )
            }
        }
    }
}