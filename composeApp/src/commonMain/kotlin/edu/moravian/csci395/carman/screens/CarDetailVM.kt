package edu.moravian.csci395.carman.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.moravian.csci395.carman.data.CarDao
import edu.moravian.csci395.carman.data.CarEntity
import edu.moravian.csci395.carman.data.MaintenanceEventDao
import edu.moravian.csci395.carman.data.MaintenanceEventEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the Car Detail screen. Streams a single car and its
 * maintenance events from the database for the supplied car id.
 */
class CarDetailVM : ViewModel() {
    private var eventDao: MaintenanceEventDao? = null
    private var carDao: CarDao? = null
    private var setUp = false

    private val _car = MutableStateFlow<CarEntity?>(null)
    val car: StateFlow<CarEntity?> = _car

    private val _events = MutableStateFlow<List<MaintenanceEventEntity>>(emptyList())
    val events: StateFlow<List<MaintenanceEventEntity>> = _events

    /** Begins collecting the car and its events. Idempotent per VM instance. */
    fun setup(
        carId: Long,
        carDao: CarDao,
        eventDao: MaintenanceEventDao,
    ) {
        if (setUp) return
        setUp = true
        this.carDao = carDao
        this.eventDao = eventDao

        viewModelScope.launch {
            carDao.getById(carId).collect { _car.value = it }
        }
        viewModelScope.launch {
            eventDao.getAllForCar(carId).collect { _events.value = it }
        }
    }

    /**
     * Marks an event as completed at the car's current mileage.
     * Updates lastCompletedMileage and nextDueMileage.
     */
    fun completeEvent(event: MaintenanceEventEntity) {
        val currentCar = _car.value ?: return
        val currentMileage = currentCar.currentMileage ?: return
        val dao = eventDao ?: return

        viewModelScope.launch {
            val updatedEvent = event.copy(
                lastCompletedMileage = currentMileage,
                nextDueMileage = currentMileage + event.intervalMiles
            )
            dao.update(updatedEvent)
        }
    }
}
