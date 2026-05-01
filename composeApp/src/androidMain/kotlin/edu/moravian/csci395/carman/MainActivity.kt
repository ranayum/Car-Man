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
import edu.moravian.csci395.carman.data.getRoomDatabase
import java.util.concurrent.TimeUnit

/** Android entry point. Builds the Room database and hands it to the Compose app. */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // Request notification permission for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val launcher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { _ -> }
            launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        scheduleMaintenanceCheck()

        setContent {
            App(getRoomDatabase(getDatabaseBuilder(this)))
        }
    }

    private fun scheduleMaintenanceCheck() {
        val workRequest = PeriodicWorkRequestBuilder<MaintenanceWorker>(1, TimeUnit.DAYS)
            .build()
        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            "MaintenanceCheck",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }
}
