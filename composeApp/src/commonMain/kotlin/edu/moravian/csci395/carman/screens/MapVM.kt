package edu.moravian.csci395.carman.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.moravian.csci395.carman.data.MechanicDao
import edu.moravian.csci395.carman.data.MechanicEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the Mechanics Map screen.
 */
class MapVM : ViewModel() {
    private val _mechanics = MutableStateFlow<List<MechanicEntity>>(emptyList())
    val mechanics: StateFlow<List<MechanicEntity>> = _mechanics

    private var setUp = false

    fun setup(mechanicDao: MechanicDao) {
        if (setUp) return
        setUp = true

        viewModelScope.launch {
            mechanicDao.getAll().collect {
                _mechanics.value = it
            }
        }
    }
}
