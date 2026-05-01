package edu.moravian.csci395.carman.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.moravian.csci395.carman.data.CarDao
import edu.moravian.csci395.carman.data.CarEntity
import edu.moravian.csci395.carman.data.MaintenanceEventDao
import edu.moravian.csci395.carman.data.MaintenanceEventEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class UpcomingEvent(
    val event: MaintenanceEventEntity,
    val car: CarEntity?
)

/**
 * ViewModel for the Home dashboard.
 */
class HomeVM : ViewModel() {
    private val _upcomingEvents = MutableStateFlow<List<UpcomingEvent>>(emptyList())
    val upcomingEvents: StateFlow<List<UpcomingEvent>> = _upcomingEvents

    private var setUp = false

    fun setup(carDao: CarDao, eventDao: MaintenanceEventDao) {
        if (setUp) return
        setUp = true

        viewModelScope.launch {
            combine(
                eventDao.getAllUpcoming(),
                carDao.getAll()
            ) { events, cars ->
                events.map { event ->
                    UpcomingEvent(event, cars.find { it.id == event.carId })
                }
            }.collect {
                _upcomingEvents.value = it
            }
        }
    }
}
