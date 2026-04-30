package edu.moravian.csci395.carman.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.moravian.csci395.carman.data.CarDao
import edu.moravian.csci395.carman.data.CarEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the Add Car form. Holds the value of each text field as a StateFlow
 * and writes a new [CarEntity] to the database when [save] is called.
 */
class AddCarVM : ViewModel() {
    private var carDao: CarDao? = null

    private val _make = MutableStateFlow("")
    val make: StateFlow<String> = _make

    private val _model = MutableStateFlow("")
    val model: StateFlow<String> = _model

    private val _year = MutableStateFlow("")
    val year: StateFlow<String> = _year

    private val _licensePlate = MutableStateFlow("")
    val licensePlate: StateFlow<String> = _licensePlate

    private val _color = MutableStateFlow("")
    val color: StateFlow<String> = _color

    private val _currentMileage = MutableStateFlow("")
    val currentMileage: StateFlow<String> = _currentMileage

    private val _weeklyAverageMiles = MutableStateFlow("")
    val weeklyAverageMiles: StateFlow<String> = _weeklyAverageMiles

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving

    /** Supplies the DAO this VM needs. Idempotent. */
    fun setup(carDao: CarDao) {
        if (this.carDao == null) this.carDao = carDao
    }

    fun setMake(value: String) { _make.value = value }
    fun setModel(value: String) { _model.value = value }
    fun setYear(value: String) { _year.value = value.filter { it.isDigit() }.take(4) }
    fun setLicensePlate(value: String) { _licensePlate.value = value }
    fun setColor(value: String) { _color.value = value }
    fun setCurrentMileage(value: String) { _currentMileage.value = value.filter { it.isDigit() } }
    fun setWeeklyAverageMiles(value: String) { _weeklyAverageMiles.value = value.filter { it.isDigit() } }

    /** True when the required fields (make, model, year) are filled with valid values. */
    fun canSave(): Boolean {
        return _make.value.isNotBlank() &&
                _model.value.isNotBlank() &&
                _year.value.toIntOrNull() != null
    }

    /**
     * Saves the form as a new car. Calls [onSuccess] on the main thread once the
     * insert completes. No-op if required fields are missing or [setup] hasn't run.
     */
    fun save(onSuccess: () -> Unit) {
        val dao = carDao ?: return
        if (!canSave()) return
        _isSaving.value = true
        viewModelScope.launch {
            val now = kotlin.time.Clock.System.now().toEpochMilliseconds()
            val mileage = _currentMileage.value.toIntOrNull()
            val car = CarEntity(
                make = _make.value.trim(),
                model = _model.value.trim(),
                year = _year.value.toInt(),
                licensePlate = _licensePlate.value.trim().ifBlank { null },
                color = _color.value.trim().ifBlank { null },
                currentMileage = mileage,
                weeklyAverageMiles = _weeklyAverageMiles.value.toIntOrNull(),
                mileageUpdatedAt = mileage?.let { now },
                createdAt = now,
            )
            dao.insert(car)
            _isSaving.value = false
            onSuccess()
        }
    }
}