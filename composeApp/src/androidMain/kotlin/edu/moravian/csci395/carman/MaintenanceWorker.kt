package edu.moravian.csci395.carman

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import edu.moravian.csci395.carman.data.MaintenanceEventEntity
import edu.moravian.csci395.carman.data.getCarManSettings
import edu.moravian.csci395.carman.data.getRoomDatabase
import kotlinx.coroutines.flow.first
import java.util.concurrent.TimeUnit

/** Weekly background worker that reminds the user to log their car's mileage. */
class MaintenanceWorker(
    context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val settings = getCarManSettings(createDataStore(applicationContext))
        if (!settings.notificationsEnabled.first()) {
            return Result.success()
        }

        val database = getRoomDatabase(getDatabaseBuilder(applicationContext))
        val cars = database.carDao().getAll().first()
        val upcomingEvents = database.maintenanceEventDao().getAllUpcoming().first()

        val overdue = mutableListOf<String>()
        val dueSoon = mutableListOf<String>()
        val staleMileageCars = mutableListOf<String>()

        val now = System.currentTimeMillis()
        val twoWeeksMillis = TimeUnit.DAYS.toMillis(14)

        for (car in cars) {
            val carName = "${car.year} ${car.make}"
            val currentMileage = car.currentMileage ?: 0
            
            // Check for stale mileage (2 weeks)
            val lastUpdated = car.mileageUpdatedAt ?: 0
            if (now - lastUpdated > twoWeeksMillis) {
                staleMileageCars.add(carName)
            }

            // Check events for this car
            val carEvents = upcomingEvents.filter { it.carId == car.id }
            for (event in carEvents) {
                val dueMileage = event.nextDueMileage ?: continue
                if (currentMileage >= dueMileage) {
                    overdue.add("$carName: ${event.title}")
                } else if (currentMileage >= dueMileage - 500) {
                    dueSoon.add("$carName: ${event.title}")
                }
            }
        }

        when {
            overdue.isNotEmpty() -> {
                showNotification(
                    id = 10,
                    title = "Urgent: Maintenance Overdue",
                    message = overdue.joinToString("\n")
                )
            }
            dueSoon.isNotEmpty() -> {
                showNotification(
                    id = 11,
                    title = "Maintenance Due Soon",
                    message = "Approaching service for: " + dueSoon.joinToString(", ")
                )
            }
            staleMileageCars.isNotEmpty() -> {
                showNotification(
                    id = 12,
                    title = "Update Odometer",
                    message = "It's been a while since you logged miles for: " + staleMileageCars.joinToString(", ")
                )
            }
            else -> {
                // Fallback to general weekly reminder if nothing else is pending
                showMileageReminder()
            }
        }

        return Result.success()
    }

    private fun showNotification(id: Int, title: String, message: String) {
        val manager = applicationContext
            .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "maintenance_alerts"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Maintenance Alerts",
                NotificationManager.IMPORTANCE_HIGH,
            )
            manager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle(title)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        manager.notify(id, notification)
    }

    private fun showMileageReminder() {
        val manager = applicationContext
            .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "mileage_reminders"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Mileage Reminders",
                NotificationManager.IMPORTANCE_DEFAULT,
            )
            manager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("CarMan – Weekly Reminder")
            .setContentText("Log your mileage so your maintenance schedule stays up to date!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        manager.notify(1, notification)
    }
}