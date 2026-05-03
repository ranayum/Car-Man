package edu.moravian.csci395.carman.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.moravian.csci395.carman.data.CarManSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SettingsVM : ViewModel() {
    private var settings: CarManSettings? = null

    private val _ownerName = MutableStateFlow("")
    val ownerName: StateFlow<String> = _ownerName

    private val _notificationsEnabled = MutableStateFlow(true)
    val notificationsEnabled: StateFlow<Boolean> = _notificationsEnabled

    private val _defaultOilMiles = MutableStateFlow("3000")
    val defaultOilMiles: StateFlow<String> = _defaultOilMiles

    private val _defaultTireMiles = MutableStateFlow("6000")
    val defaultTireMiles: StateFlow<String> = _defaultTireMiles

    private val _defaultBrakeMiles = MutableStateFlow("12000")
    val defaultBrakeMiles: StateFlow<String> = _defaultBrakeMiles

    private val _useDarkTheme = MutableStateFlow(false)
    val useDarkTheme: StateFlow<Boolean> = _useDarkTheme

    private val _language = MutableStateFlow("en")
    val language: StateFlow<String> = _language

    fun setup(settings: CarManSettings) {
        if (this.settings != null) return
        this.settings = settings
        viewModelScope.launch {
            settings.ownerName.collect { _ownerName.value = it }
        }
        viewModelScope.launch {
            settings.notificationsEnabled.collect { _notificationsEnabled.value = it }
        }
        viewModelScope.launch {
            settings.defaultOilMiles.collect { _defaultOilMiles.value = it.toString() }
        }
        viewModelScope.launch {
            settings.defaultTireMiles.collect { _defaultTireMiles.value = it.toString() }
        }
        viewModelScope.launch {
            settings.defaultBrakeMiles.collect { _defaultBrakeMiles.value = it.toString() }
        }
        viewModelScope.launch {
            settings.useDarkTheme.collect { _useDarkTheme.value = it }
        }
        viewModelScope.launch {
            settings.language.collect { _language.value = it }
        }
    }

    fun setOwnerName(name: String) { _ownerName.value = name }

    fun setLanguage(lang: String) {
        _language.value = lang
        viewModelScope.launch { settings?.setLanguage(lang) }
    }
    fun setNotificationsEnabled(v: Boolean) {
        _notificationsEnabled.value = v
        viewModelScope.launch { settings?.setNotificationsEnabled(v) }
    }
    fun setDefaultOilMiles(v: String) {
        if (v.all { it.isDigit() }) {
            _defaultOilMiles.value = v
            v.toIntOrNull()?.let { miles ->
                viewModelScope.launch { settings?.setDefaultOilMiles(miles) }
            }
        }
    }
    fun setDefaultTireMiles(v: String) {
        if (v.all { it.isDigit() }) {
            _defaultTireMiles.value = v
            v.toIntOrNull()?.let { miles ->
                viewModelScope.launch { settings?.setDefaultTireMiles(miles) }
            }
        }
    }
    fun setDefaultBrakeMiles(v: String) {
        if (v.all { it.isDigit() }) {
            _defaultBrakeMiles.value = v
            v.toIntOrNull()?.let { miles ->
                viewModelScope.launch { settings?.setDefaultBrakeMiles(miles) }
            }
        }
    }
    fun setUseDarkTheme(v: Boolean) {
        _useDarkTheme.value = v
        viewModelScope.launch { settings?.setUseDarkTheme(v) }
    }
    fun saveOwnerName() {
        viewModelScope.launch { settings?.setOwnerName(_ownerName.value) }
    }
}