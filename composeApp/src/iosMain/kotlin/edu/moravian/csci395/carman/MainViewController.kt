package edu.moravian.csci395.carman

import androidx.compose.ui.window.ComposeUIViewController
import edu.moravian.csci395.carman.data.getCarManSettings
import edu.moravian.csci395.carman.data.getRoomDatabase

fun MainViewController() = ComposeUIViewController {
    App(
        database = getRoomDatabase(getDatabaseBuilder()),
        settings = getCarManSettings(createDataStore()),
    )
}