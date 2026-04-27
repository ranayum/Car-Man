package edu.moravian.csci395.carman.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.moravian.csci395.carman.data.CarDao
import edu.moravian.csci395.carman.data.CarEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel backing the Cars list screen. Collects the reactive list of
 * cars from the database and exposes it as a StateFlow for the UI.
 */
class CarsVM : ViewModel() {
    private var carDao: CarDao? = null

    private val _cars = MutableStateFlow<List<CarEntity>>(emptyList())
    /** All cars currently in the database, newest first. */
    val cars: StateFlow<List<CarEntity>> = _cars

    /**
     * Supplies the DAO the VM needs. Safe to call multiple times —
     * subsequent calls are ignored so we don't stack up collectors.
     */
    fun setup(carDao: CarDao) {
        if (this.carDao == null) {
            this.carDao = carDao
            viewModelScope.launch {
                carDao.getAll().collect { _cars.value = it }
            }
        }
    }
}