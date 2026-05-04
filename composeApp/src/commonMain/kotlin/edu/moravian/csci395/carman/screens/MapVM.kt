package edu.moravian.csci395.carman.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.moravian.csci395.carman.data.MechanicDao
import edu.moravian.csci395.carman.data.MechanicEntity
import edu.moravian.csci395.carman.data.OverpassService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.time.Clock

/**
 * ViewModel for the Mechanics Map screen.
 */
class MapVM : ViewModel() {
    private val _mechanics = MutableStateFlow<List<MechanicEntity>>(emptyList())
    val mechanics: StateFlow<List<MechanicEntity>> = _mechanics

    private val _discoveredMechanics = MutableStateFlow<List<MechanicEntity>>(emptyList())
    val discoveredMechanics: StateFlow<List<MechanicEntity>> = _discoveredMechanics

    private val _selectedMechanic = MutableStateFlow<MechanicEntity?>(null)
    val selectedMechanic: StateFlow<MechanicEntity?> = _selectedMechanic

    private val overpassService = OverpassService()
    private var setUp = false

    fun selectMechanic(mechanic: MechanicEntity?) {
        _selectedMechanic.value = mechanic
    }

    fun saveMechanic(mechanic: MechanicEntity, mechanicDao: MechanicDao) {
        viewModelScope.launch {
            val now = Clock.System.now().toEpochMilliseconds()
            mechanicDao.insert(mechanic.copy(createdAt = now))
            _selectedMechanic.value = null
        }
    }

    fun setup(mechanicDao: MechanicDao) {
        if (setUp) return
        setUp = true

        viewModelScope.launch {
            mechanicDao.getAll().collect {
                _mechanics.value = it
            }
        }
    }

    fun searchArea(north: Double, south: Double, east: Double, west: Double) {
        viewModelScope.launch {
            try {
                val results = overpassService.searchMechanics(north, south, east, west)
                _discoveredMechanics.value = results
            } catch (e: Exception) {
                // TODO: Handle error (e.g., show a snackbar)
                e.printStackTrace()
            }
        }
    }
}
