package edu.moravian.csci395.carman

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge

/** Android entry point. Builds the Room database and hands it to the Compose app. */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            App(getRoomDatabase(getDatabaseBuilder(this)))
        }
    }
}