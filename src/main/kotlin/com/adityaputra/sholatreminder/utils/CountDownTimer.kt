package com.adityaputra.sholatreminder.utils

import kotlinx.coroutines.*
import org.koin.core.time.TimeInMillis
import java.util.concurrent.TimeUnit

class CountdownTimer(private val durationInMillis: Long) {
    private var job: Job? = null

    fun start(onTick: (Long) -> Unit, onFinish: () -> Unit) {
        cancel()
        job = CoroutineScope(Dispatchers.Default).launch {
            var remainingMillis = durationInMillis

            while (remainingMillis > 0) {
                onTick(remainingMillis)
                delay(1000)
                remainingMillis -= 1000
            }
            onFinish()
        }
    }

    fun cancel() {
        job?.cancel()
    }
}
