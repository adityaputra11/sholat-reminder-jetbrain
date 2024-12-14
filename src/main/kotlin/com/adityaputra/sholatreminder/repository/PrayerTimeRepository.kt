package com.adityaputra.sholatreminder.repository

import com.adityaputra.sholatreminder.network.MyQuranApi

class PrayerRepository(private val api: MyQuranApi) {
    suspend fun getPrayerTimes(city: String, date: String) =
        api.getPrayerTimes(city, date)

    suspend fun getIDCities() = api.getIDCities()
}