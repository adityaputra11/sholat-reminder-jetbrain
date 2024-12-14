package com.adityaputra.sholatreminder.network

import com.adityaputra.sholatreminder.model.IDCityResponse
import com.adityaputra.sholatreminder.model.PrayerResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface MyQuranApi {
    @GET("v2/sholat/jadwal/{kota}/{tanggal}")
    suspend fun getPrayerTimes(
        @Path("kota") city: String,
        @Path("tanggal") date: String
    ): PrayerResponse

    @GET("v2/sholat/kota/semua")
    suspend fun getIDCities():IDCityResponse
}