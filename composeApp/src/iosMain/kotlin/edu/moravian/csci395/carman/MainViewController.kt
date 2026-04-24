package edu.moravian.csci395.carman

import androidx.compose.ui.window.ComposeUIViewController
import edu.moravian.csci395.carman.data.getRoomDatabase

/** iOS entry point. Builds the Room database and hands it to the Compose app. */
fun MainViewController() = ComposeUIViewController {
    App(getRoomDatabase(getDatabaseBuilder()))
}