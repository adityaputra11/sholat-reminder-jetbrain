package com.adityaputra.sholatreminder.model

data class IDCityResponse (
    val status: String,
    val data: List<IDCity>
)

data class IDCity (
    val id:String,
    val lokasi:String,
)

data class CityState(
    var cityName: String = "",
    var cityKey: String = "",
    var isShow: Boolean = false,
)