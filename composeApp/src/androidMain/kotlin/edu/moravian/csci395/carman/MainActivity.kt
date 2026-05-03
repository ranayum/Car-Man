package edu.moravian.csci395.carman

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import edu.moravian.csci395.carman.data.getCarManSettings
import edu.moravian.csci395.carman.data.getRoomDatabase
import kotlinx.coroutines.launch
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

        var currentLang: String? = null
        lifecycleScope.launch {
            settings.language.collect { lang ->
                if (currentLang != null && currentLang != lang) {
                    applyLocale(lang)
                    recreate()
                } else {
                    applyLocale(lang)
                }
                currentLang = lang
            }
        }

        setContent {
            App(database = database, settings = settings)
        }
    }

    private fun applyLocale(lang: String) {
        val locale = java.util.Locale(lang)
        java.util.Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)
        @Suppress("DEPRECATION")
        resources.updateConfiguration(config, resources.displayMetrics)
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