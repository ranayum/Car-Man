package edu.moravian.csci395.carman

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import okio.Path.Companion.toPath

private var dataStoreInstance: DataStore<Preferences>? = null

fun createDataStore(context: Context): DataStore<Preferences> =
    dataStoreInstance ?: PreferenceDataStoreFactory.createWithPath(
        produceFile = {
            context.applicationContext.filesDir
                .resolve("carman.preferences_pb")
                .absolutePath
                .toPath()
        }
    ).also { dataStoreInstance = it }