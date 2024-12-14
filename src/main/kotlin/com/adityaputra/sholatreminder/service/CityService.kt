package com.adityaputra.sholatreminder.service

import com.adityaputra.sholatreminder.model.CityState
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage


@State(
    name = "CityPluginState",
    storages = [Storage("cityPlugin.xml")]
)
@Service // Ini membuatnya sebagai service yang dapat diakses secara global
class CityStateService : PersistentStateComponent<CityState> {

    private val listeners = mutableListOf<CityStateListener>()
    private var state = CityState()

    // Interface untuk listener
    interface CityStateListener {
        fun onStateChanged(newState: CityState)
    }


    override fun getState(): CityState {
        return state
    }

    override fun loadState(state: CityState) {
        this.state = state
        notifyListeners()
    }

    fun updateSetting(cityState: CityState) {
        state = cityState
        notifyListeners()
    }

    // Fungsi untuk menambah listener
    fun addListener(listener: CityStateListener) {
        listeners.add(listener)
    }

    // Fungsi untuk menghapus listener
    fun removeListener(listener: CityStateListener) {
        listeners.remove(listener)
    }

    // Fungsi untuk memberitahu semua listener
    private fun notifyListeners() {
        listeners.forEach { it.onStateChanged(state) }
    }

    fun isShow(): Boolean = state.isShow

    fun getCityName() : String{
        if (isShow()){
            return " [${state.cityName}]"
        }
        return ""
    }
}
