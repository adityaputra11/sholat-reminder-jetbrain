package com.adityaputra.sholatreminder.viewmodel

import com.adityaputra.sholatreminder.model.IDCity
import com.adityaputra.sholatreminder.repository.PrayerRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class LocationViewModel(private val repository: PrayerRepository) {
    private val scope = CoroutineScope(Dispatchers.Default + Job())

    private val _iDcities = MutableStateFlow<List<IDCity>?>(null)
    val iDcities: StateFlow<List<IDCity>?> get() = _iDcities

    fun fetchIndonesianCity() {
        scope.launch {
            try {
                val response = repository.getIDCities()
                println("API response received: $response")
                _iDcities.value = response.data
            } catch (e: Exception) {
                println("Error fetching prayer times: ${e.message}")
                e.printStackTrace()
            }
        }

        //:TODO For Next Version
        fun fetchCountry() {

        }

        //:TODO For Next Version
        fun fetchStateByCountryCode() {

        }

        //:TODO For Next Version
        fun fetchCityByStateCode() {

        }

    }

}