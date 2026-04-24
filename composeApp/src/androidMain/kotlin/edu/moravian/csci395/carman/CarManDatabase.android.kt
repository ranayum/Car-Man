package edu.moravian.csci395.carman

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

/** Builds the CarMan Room database file under the app's private Android storage. */
fun getDatabaseBuilder(context: Context): RoomDatabase.Builder<CarManDatabase> {
    val appContext = context.applicationContext
    val dbFile = appContext.getDatabasePath("carman.db")
    return Room.databaseBuilder<CarManDatabase>(
        context = appContext,
        name = dbFile.absolutePath,
    )
}