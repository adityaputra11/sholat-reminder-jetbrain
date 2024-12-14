package com.adityaputra.sholatreminder.viewmodel

import com.adityaputra.sholatreminder.repository.PrayerRepository
import com.adityaputra.sholatreminder.model.PrayerData
import com.adityaputra.sholatreminder.service.showLocalNotification
import com.adityaputra.sholatreminder.utils.CountdownTimer
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.Duration
import java.time.LocalTime

class PrayerViewModel(private val repository: PrayerRepository) {
    private val scope = CoroutineScope(Dispatchers.Default + Job())

    private val _prayerTimes = MutableStateFlow<PrayerData?>(null)
    private val _prayerText = MutableStateFlow<String>("")
    val prayerTimes: StateFlow<PrayerData?> get() = _prayerTimes
    val prayerText: StateFlow<String> get() = _prayerText
    private var timerJob: Job? = null


    private var countdownTimer: CountdownTimer? = null
    private var nextPrayerTime: LocalTime? = null
    private var nextPrayerName: String = ""


    private fun startCountdown(targetTime: LocalTime, prayerName: String) {
        countdownTimer?.cancel()

        val now = LocalTime.now()
        var duration = Duration.between(now, targetTime)
        if (duration.isNegative) {
            duration = duration.plusDays(1)
        }
        countdownTimer = CountdownTimer(duration.toMillis())
        countdownTimer?.start(
                onTick = { remainingTime ->
                    val durationMillis = Duration.ofMillis(remainingTime)
                    val hours = durationMillis.toHours()
                    val minutes = durationMillis.toMinutes() % 60
                    val seconds = durationMillis.toSeconds() % 60
                    val cd = String.format("%02d:%02d:%02d", hours, minutes, seconds)
                    _prayerText.value = "$prayerName($targetTime) -$cd"
                },
                onFinish = {
                    showLocalNotification(prayerName, targetTime.toString())
                    updatePrayerText()
                }
            )
    }


    private fun updatePrayerText() {
        val currentTime = LocalTime.now()
        val prayerData = _prayerTimes.value?.jadwal

        if (prayerData != null) {
            val prayerTimes = listOf(
                "Subuh" to LocalTime.parse(prayerData.subuh),
                "Dzuhur" to LocalTime.parse(prayerData.dzuhur),
                "Ashar" to LocalTime.parse(prayerData.ashar),
                "Maghrib" to LocalTime.parse(prayerData.maghrib),
                "Isya" to LocalTime.parse(prayerData.isya)
            )

            val nextPrayer = prayerTimes.firstOrNull { it.second > currentTime }
                ?: prayerTimes.first()

            nextPrayerTime = nextPrayer.second
            nextPrayerName = nextPrayer.first

            startCountdown(nextPrayerTime!!, nextPrayerName)

        }

         _prayerText.value = "tidak dapat menentukan waktu sholat"
    }




    fun onClear(){
        timerJob?.cancel()
    }


    fun fetchPrayerTimes(city: String, date: String) {
        _prayerText.value = "Medapatkan Jadwal Sholat"
        scope.launch {
            println("Starting coroutine for fetchPrayerTimes with city: $city and date: $date")
            try {
                val response = repository.getPrayerTimes(city, date)
                println("API response received: $response")
                _prayerTimes.value = response.data
                updatePrayerText()
                println("Prayer times updated successfully.")
            } catch (e: Exception) {
                _prayerText.value = ""
                println("Error fetching prayer times: ${e.message}")
                e.printStackTrace()
            }
        }
    }



    fun getCurrentPray(){

    }


    fun clear() {
        scope.cancel()
        countdownTimer?.cancel()
    }

}