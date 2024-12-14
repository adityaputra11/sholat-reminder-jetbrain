package com.adityaputra.sholatreminder

import com.adityaputra.sholatreminder.network.MyQuranApi
import com.adityaputra.sholatreminder.repository.PrayerRepository
import com.adityaputra.sholatreminder.viewmodel.LocationViewModel
import com.adityaputra.sholatreminder.viewmodel.PrayerViewModel
import com.intellij.openapi.components.Service
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Service(Service.Level.APP)
class AppModule{
    private val retrofit: Retrofit = Retrofit.Builder()
    .baseUrl("https://api.myquran.com/")
    .addConverterFactory(GsonConverterFactory.create())
    .build()

    private val api: MyQuranApi = retrofit.create(MyQuranApi::class.java)
    private val repository = PrayerRepository(api)
    private val prayerViewModel = PrayerViewModel(repository)
    private val locationViewModel = LocationViewModel(repository)

    fun getPrayerViewModel():PrayerViewModel{
        return prayerViewModel
    }

    fun getLocationViewModel():LocationViewModel{
        return locationViewModel
    }
}