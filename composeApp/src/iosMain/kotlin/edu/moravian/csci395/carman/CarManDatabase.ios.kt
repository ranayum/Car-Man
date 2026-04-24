package edu.moravian.csci395.carman

import androidx.room.Room
import androidx.room.RoomDatabase
import edu.moravian.csci395.carman.data.CarManDatabase
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

/** Builds the CarMan Room database file under the iOS app's Documents directory. */
fun getDatabaseBuilder(): RoomDatabase.Builder<CarManDatabase> {
    val dbFilePath = documentDirectory() + "/carman.db"
    return Room.databaseBuilder<CarManDatabase>(
        name = dbFilePath,
    )
}

/** Resolves the iOS Documents directory path where the database file lives. */
@OptIn(ExperimentalForeignApi::class)
private fun documentDirectory(): String {
    val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = false,
        error = null,
    )
    return requireNotNull(documentDirectory?.path)
}