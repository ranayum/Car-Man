package edu.moravian.csci395.carman.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** Typed wrapper around DataStore<Preferences> for all app settings. */
class CarManSettings(private val dataStore: DataStore<Preferences>) {

    companion object {
        val OWNER_NAME            = stringPreferencesKey("owner_name")
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val DEFAULT_OIL_MILES     = intPreferencesKey("default_oil_miles")
        val DEFAULT_TIRE_MILES    = intPreferencesKey("default_tire_miles")
        val DEFAULT_BRAKE_MILES   = intPreferencesKey("default_brake_miles")
        val USE_DARK_THEME        = booleanPreferencesKey("use_dark_theme")
    }

    val ownerName: Flow<String> =
        dataStore.data.map { it[OWNER_NAME] ?: "" }

    val notificationsEnabled: Flow<Boolean> =
        dataStore.data.map { it[NOTIFICATIONS_ENABLED] ?: true }

    val defaultOilMiles: Flow<Int> =
        dataStore.data.map { it[DEFAULT_OIL_MILES] ?: 3000 }

    val defaultTireMiles: Flow<Int> =
        dataStore.data.map { it[DEFAULT_TIRE_MILES] ?: 6000 }

    val defaultBrakeMiles: Flow<Int> =
        dataStore.data.map { it[DEFAULT_BRAKE_MILES] ?: 12000 }

    val useDarkTheme: Flow<Boolean> =
        dataStore.data.map { it[USE_DARK_THEME] ?: false }

    suspend fun setOwnerName(name: String) {
        dataStore.edit { it[OWNER_NAME] = name }
    }
    suspend fun setNotificationsEnabled(enabled: Boolean) {
        dataStore.edit { it[NOTIFICATIONS_ENABLED] = enabled }
    }
    suspend fun setDefaultOilMiles(miles: Int) {
        dataStore.edit { it[DEFAULT_OIL_MILES] = miles }
    }
    suspend fun setDefaultTireMiles(miles: Int) {
        dataStore.edit { it[DEFAULT_TIRE_MILES] = miles }
    }
    suspend fun setDefaultBrakeMiles(miles: Int) {
        dataStore.edit { it[DEFAULT_BRAKE_MILES] = miles }
    }
    suspend fun setUseDarkTheme(dark: Boolean) {
        dataStore.edit { it[USE_DARK_THEME] = dark }
    }
}

fun getCarManSettings(dataStore: DataStore<Preferences>): CarManSettings =
    CarManSettings(dataStore)