package com.adityaputra.sholatreminder.action

import com.adityaputra.sholatreminder.AppModule
import com.adityaputra.sholatreminder.model.CityState
import com.adityaputra.sholatreminder.model.DropdownItem
import com.adityaputra.sholatreminder.model.IDCity
import com.adityaputra.sholatreminder.service.CityStateService
import com.adityaputra.sholatreminder.viewmodel.LocationViewModel
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.components.service
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.firstOrNull

class PrayerTimeActions : AnAction(), DumbAware {
    private val app = AppModule()
    private val locationViewModel: LocationViewModel = app.getLocationViewModel()
    private val scope = CoroutineScope(Dispatchers.Default + Job())


    init {
        locationViewModel.fetchIndonesianCity()
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        fetchAndShowCities(project)
    }

    private fun fetchAndShowCities(project: Project) {
        scope.launch {
            val cities = locationViewModel.iDcities.firstOrNull()
            if (cities.isNullOrEmpty()) {
                logMessage("City list is empty or null")
                return@launch
            }
            val items = mapCitiesToDropdownItems(cities)
            showDropdownDialog(project, items)
        }
    }

    private fun mapCitiesToDropdownItems(cities: List<IDCity>): List<DropdownItem<String>> {
        return cities.map { city -> DropdownItem(city.id, city.lokasi) }
    }

    private suspend fun showDropdownDialog(project: Project, items: List<DropdownItem<String>>) {
        withContext(Dispatchers.Main) {
            SearchDropdownDialog(
                title = "Choose City",
                items = items,
                onItemSelected = { selected ->
                    handleDropdownSelection(selected)
                }
            ).show(project)
        }
    }

    private fun handleDropdownSelection(selected: DropdownItem<String>) {
        val stateService = service<CityStateService>()
        stateService.updateSetting(CityState(selected.label, selected.key))
    }

    private fun logMessage(message: String) {
        println(message)
    }
}