package edu.moravian.csci395.carman.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.moravian.csci395.carman.data.MechanicDao
import edu.moravian.csci395.carman.data.MechanicEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the Add Mechanic screen.
 */
class AddMechanicVM : ViewModel() {
    private var mechanicDao: MechanicDao? = null

    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name

    private val _address = MutableStateFlow("")
    val address: StateFlow<String> = _address

    private val _latitude = MutableStateFlow("40.630")
    val latitude: StateFlow<String> = _latitude

    private val _longitude = MutableStateFlow("-75.381")
    val longitude: StateFlow<String> = _longitude

    private val _phone = MutableStateFlow("")
    val phone: StateFlow<String> = _phone

    private val _notes = MutableStateFlow("")
    val notes: StateFlow<String> = _notes

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving

    fun setup(mechanicDao: MechanicDao) {
        this.mechanicDao = mechanicDao
    }

    fun setName(value: String) { _name.value = value }
    fun setAddress(value: String) { _address.value = value }
    fun setLatitude(value: String) { _latitude.value = value }
    fun setLongitude(value: String) { _longitude.value = value }
    fun setPhone(value: String) { _phone.value = value }
    fun setNotes(value: String) { _notes.value = value }

    fun canSave(): Boolean {
        return _name.value.isNotBlank() && 
               _latitude.value.toDoubleOrNull() != null && 
               _longitude.value.toDoubleOrNull() != null
    }

    fun save(onSuccess: () -> Unit) {
        val dao = mechanicDao ?: return
        if (!canSave()) return

        _isSaving.value = true
        viewModelScope.launch {
            val mechanic = MechanicEntity(
                name = _name.value.trim(),
                addressLine = _address.value.trim().ifBlank { null },
                latitude = _latitude.value.toDouble(),
                longitude = _longitude.value.toDouble(),
                phone = _phone.value.trim().ifBlank { null },
                notes = _notes.value.trim().ifBlank { null },
                createdAt = kotlin.time.Clock.System.now().toEpochMilliseconds()
            )
            dao.insert(mechanic)
            _isSaving.value = false
            onSuccess()
        }
    }
}
