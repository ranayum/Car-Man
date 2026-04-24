package edu.moravian.csci395.carman

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

/** Room database for CarMan. Holds the 3 entities and exposes one DAO per entity. */
@Database(
    entities = [
        CarEntity::class,
        MaintenanceEventEntity::class,
        MechanicEntity::class,
    ],
    version = 1,
)
@ConstructedBy(CarManDatabaseConstructor::class)
abstract class CarManDatabase : RoomDatabase() {
    abstract fun carDao(): CarDao

    abstract fun maintenanceEventDao(): MaintenanceEventDao

    abstract fun mechanicDao(): MechanicDao
}

// The Room compiler generates the `actual` implementation for each platform.
@Suppress("KotlinNoActualForExpect")
expect object CarManDatabaseConstructor : RoomDatabaseConstructor<CarManDatabase> {
    override fun initialize(): CarManDatabase
}

/** Finishes building the Room database with a bundled SQLite driver and IO dispatcher. */
fun getRoomDatabase(
    builder: RoomDatabase.Builder<CarManDatabase>,
): CarManDatabase = builder
    .setDriver(BundledSQLiteDriver())
    .setQueryCoroutineContext(Dispatchers.IO)
    .build()