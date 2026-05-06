package edu.moravian.csci395.carman

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import edu.moravian.csci395.carman.data.CarManDatabase

fun getDatabaseBuilder(context: Context): RoomDatabase.Builder<CarManDatabase> {
    val dbFile = context.getDatabasePath("carman.db")
    return Room.databaseBuilder<CarManDatabase>(
        context = context.applicationContext,
        name = dbFile.absolutePath,
    )
}
