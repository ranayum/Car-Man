package edu.moravian.csci395.carman

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import edu.moravian.csci395.carman.data.getRoomDatabase
import kotlinx.coroutines.flow.first

class MaintenanceWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val database = getRoomDatabase(getDatabaseBuilder(applicationContext))
        val carDao = database.carDao()
        val eventDao = database.maintenanceEventDao()

        val cars = carDao.getAll().first()
        val events = eventDao.getAllUpcoming().first()

        val now = System.currentTimeMillis()

        for (event in events) {
            val car = cars.find { it.id == event.carId } ?: continue
            val currentMileage = car.currentMileage ?: 0
            val nextDue = event.nextDueMileage ?: continue

            // Simple logic: notify if within 500 miles of being due
            if (nextDue - currentMileage <= 500) {
                showNotification(car.make + " " + car.model, event.title + " due soon at " + nextDue + " mi")
            }
        }

        return Result.success()
    }

    private fun showNotification(carName: String, message: String) {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "maintenance_reminders"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Maintenance Reminders", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Maintenance Due: $carName")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(carName.hashCode(), notification)
    }
}
