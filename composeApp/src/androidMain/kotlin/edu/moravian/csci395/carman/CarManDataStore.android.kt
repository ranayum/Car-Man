package edu.moravian.csci395.carman

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import okio.Path.Companion.toPath

fun createDataStore(context: Context): DataStore<Preferences> =
    PreferenceDataStoreFactory.createWithPath(
        produceFile = {
            context.applicationContext.filesDir
                .resolve("carman.preferences_pb")
                .absolutePath
                .toPath()
        }
    )