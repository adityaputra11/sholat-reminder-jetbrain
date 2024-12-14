package com.adityaputra.sholatreminder

import com.adityaputra.sholatreminder.helper.formattedDateTime
import com.adityaputra.sholatreminder.model.CityState
import com.adityaputra.sholatreminder.network.MyQuranApi
import com.adityaputra.sholatreminder.repository.PrayerRepository
import com.adityaputra.sholatreminder.service.CityStateService
import com.adityaputra.sholatreminder.utils.CountdownTimer
import com.adityaputra.sholatreminder.viewmodel.LocationViewModel
import com.adityaputra.sholatreminder.viewmodel.PrayerViewModel
import com.ibm.icu.util.TimeUnit
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.StatusBar
import com.intellij.openapi.wm.StatusBarWidget
import com.intellij.testFramework.fixtures.injectionForHost
import java.awt.Component
import java.awt.event.MouseEvent
import com.intellij.util.Consumer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.observeOn
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class PrayerStatusBarWidget() : StatusBarWidget {
    private val scope = CoroutineScope(Dispatchers.IO + Job())
    private var presentation: StatusBarWidget.WidgetPresentation? = null
    private var statusBar: StatusBar? = null
    private val app = AppModule()
    private val prayerViewModel:PrayerViewModel = app.getPrayerViewModel()
    val cityState = service<CityStateService>()
    private var showText= ""

    init {
        prayerViewModel.fetchPrayerTimes(cityState.state.cityKey, formattedDateTime)
    }


    override fun getPresentation(): StatusBarWidget.WidgetPresentation {
        if (presentation == null) {
            presentation = createPresentation()
            observePrayerTimes()
        }
        return presentation!!
    }

    private fun observePrayerTimes() {
        scope.launch {
            prayerViewModel.prayerText.collect { text ->
                text.let {
                    showText = text + cityState.getCityName()
                    updateWidget()
                }
            }
        }

        cityState.addListener(object : CityStateService.CityStateListener {
            override fun onStateChanged(newState: CityState) {
                prayerViewModel.fetchPrayerTimes(newState.cityKey, formattedDateTime)
            }
        })
    }

    private fun createPresentation() = object : StatusBarWidget.TextPresentation {

        override fun getText(): String {
            return showText
        }

        override fun getTooltipText(): String {
            return "Next prayer time"
        }

        override fun getClickConsumer(): Consumer<MouseEvent>? {
            return null
        }

        override fun getAlignment(): Float {
            return Component.LEFT_ALIGNMENT
        }
    }


    private fun updateWidget() {
        statusBar?.updateWidget(ID())
    }

    override fun ID(): String = "PrayerTimeWidget"

    override fun install(statusBar: StatusBar) {
        this.statusBar = statusBar
    }

    override fun dispose() {
        prayerViewModel.onClear()
        statusBar = null
        presentation = null
    }
}