package edu.moravian.csci395.carman.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.moravian.csci395.carman.data.CarDao
import edu.moravian.csci395.carman.data.CarEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the Log Mileage screen. Loads the target car and writes
 * an updated mileage value back to the database on save.
 */
class LogMileageVM : ViewModel() {
    private var setUp = false
    private var carDao: CarDao? = null
    private var carId: Long = 0L

    private val _car = MutableStateFlow<CarEntity?>(null)
    val car: StateFlow<CarEntity?> = _car

    private val _newMileage = MutableStateFlow("")
    val newMileage: StateFlow<String> = _newMileage

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving

    fun setup(carId: Long, carDao: CarDao) {
        if (setUp) return
        setUp = true
        this.carDao = carDao
        this.carId = carId
        viewModelScope.launch {
            carDao.getById(carId).collect { _car.value = it }
        }
    }

    fun setNewMileage(value: String) {
        _newMileage.value = value.filter { it.isDigit() }
    }

    /** True once a valid integer has been entered. */
    fun canSave(): Boolean = _newMileage.value.toIntOrNull() != null

    /** Persists the new mileage on the car, then calls [onSuccess]. */
    fun save(onSuccess: () -> Unit) {
        val dao = carDao ?: return
        val mileage = _newMileage.value.toIntOrNull() ?: return
        _isSaving.value = true
        viewModelScope.launch {
            dao.updateMileage(
                carId = carId,
                mileage = mileage,
                timestamp = kotlin.time.Clock.System.now().toEpochMilliseconds(),
            )
            _isSaving.value = false
            onSuccess()
        }
    }
}