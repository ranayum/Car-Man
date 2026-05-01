package edu.moravian.csci395.carman

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import edu.moravian.csci395.carman.data.getCarManSettings
import edu.moravian.csci395.carman.data.getRoomDatabase
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val launcher = registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { _ -> }
            launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        scheduleMileageReminder()

        val database = getRoomDatabase(getDatabaseBuilder(this))
        val settings = getCarManSettings(createDataStore(this))

        setContent {
            App(database = database, settings = settings)
        }
    }

    private fun scheduleMileageReminder() {
        val workRequest = PeriodicWorkRequestBuilder<MaintenanceWorker>(7, TimeUnit.DAYS).build()
        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            "MileageReminder",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest,
        )
    }
}