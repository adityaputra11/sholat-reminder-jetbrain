package com.adityaputra.sholatreminder.model

data class PrayerResponse(
    val status: String,
    val data: PrayerData
)

data class PrayerData(
    val jadwal: Jadwal
)

data class Jadwal(
    val tanggal: String,
    val imsak: String,
    val subuh: String,
    val dzuhur: String,
    val ashar: String,
    val maghrib: String,
    val isya: String
)