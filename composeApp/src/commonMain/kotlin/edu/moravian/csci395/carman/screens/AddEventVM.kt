package edu.moravian.csci395.carman.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.moravian.csci395.carman.data.CarDao
import edu.moravian.csci395.carman.data.MaintenanceEventDao
import edu.moravian.csci395.carman.data.MaintenanceEventEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the Add/Edit Maintenance Event form.
 */
class AddEventVM : ViewModel() {
    private var carDao: CarDao? = null
    private var eventDao: MaintenanceEventDao? = null
    private var carId: Long = -1
    private var eventId: Long? = null

    private val _type = MutableStateFlow("OIL_CHANGE")
    val type: StateFlow<String> = _type

    private val _title = MutableStateFlow("Oil Change")
    val title: StateFlow<String> = _title

    private val _intervalMiles = MutableStateFlow("5000")
    val intervalMiles: StateFlow<String> = _intervalMiles

    private val _notes = MutableStateFlow("")
    val notes: StateFlow<String> = _notes

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving

    private val _isEditMode = MutableStateFlow(false)
    val isEditMode: StateFlow<Boolean> = _isEditMode

    fun setup(carId: Long, carDao: CarDao, eventDao: MaintenanceEventDao, eventId: Long? = null) {
        this.carId = carId
        this.carDao = carDao
        this.eventDao = eventDao
        this.eventId = eventId
        
        if (eventId != null && !_isEditMode.value) {
            _isEditMode.value = true
            viewModelScope.launch {
                eventDao.getByIdOnce(eventId)?.let { event ->
                    _type.value = event.type
                    _title.value = event.title
                    _intervalMiles.value = event.intervalMiles.toString()
                    _notes.value = event.notes ?: ""
                    this@AddEventVM.carId = event.carId
                }
            }
        }
    }

    fun setType(value: String) {
        _type.value = value
        if (!_isEditMode.value) {
            if (_type.value == "OIL_CHANGE") _title.value = "Oil Change"
            else if (_type.value == "TIRE_CHECK") _title.value = "Tire Rotation"
            else if (_type.value == "BRAKE_CHECK") _title.value = "Brake Inspection"
        }
    }

    fun setTitle(value: String) { _title.value = value }
    fun setIntervalMiles(value: String) { _intervalMiles.value = value.filter { it.isDigit() } }
    fun setNotes(value: String) { _notes.value = value }

    fun canSave(): Boolean {
        return _title.value.isNotBlank() && _intervalMiles.value.toIntOrNull() != null
    }

    fun save(onSuccess: () -> Unit) {
        val eDao = eventDao ?: return
        val cDao = carDao ?: return
        if (!canSave()) return

        _isSaving.value = true
        viewModelScope.launch {
            val interval = _intervalMiles.value.toInt()
            
            if (eventId == null) {
                // Add new
                val car = cDao.getByIdOnce(carId)
                val currentMileage = car?.currentMileage ?: 0
                val nextDue = currentMileage + interval

                val event = MaintenanceEventEntity(
                    carId = carId,
                    type = _type.value,
                    title = _title.value.trim(),
                    notes = _notes.value.trim().ifBlank { null },
                    intervalMiles = interval,
                    nextDueMileage = nextDue
                )
                eDao.insert(event)
            } else {
                // Update existing
                val existingEvent = eDao.getByIdOnce(eventId!!) ?: return@launch
                
                // If interval changed, we might need to adjust nextDueMileage? 
                // For simplicity, let's keep it based on lastCompletedMileage if it exists.
                val nextDue = existingEvent.lastCompletedMileage?.let { it + interval } ?: existingEvent.nextDueMileage

                val updatedEvent = existingEvent.copy(
                    type = _type.value,
                    title = _title.value.trim(),
                    notes = _notes.value.trim().ifBlank { null },
                    intervalMiles = interval,
                    nextDueMileage = nextDue
                )
                eDao.update(updatedEvent)
            }
            
            _isSaving.value = false
            onSuccess()
        }
    }
    
    fun delete(onSuccess: () -> Unit) {
        val eDao = eventDao ?: return
        val id = eventId ?: return
        viewModelScope.launch {
            eDao.getByIdOnce(id)?.let { eDao.delete(it) }
            onSuccess()
        }
    }
}
