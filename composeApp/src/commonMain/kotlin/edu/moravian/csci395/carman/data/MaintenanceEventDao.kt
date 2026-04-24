package edu.moravian.csci395.carman.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/** DAO for reading and writing [MaintenanceEventEntity] rows. */
@Dao
interface MaintenanceEventDao {
    /** Inserts an event and returns the new row's id. */
    @Insert
    suspend fun insert(event: MaintenanceEventEntity): Long

    /** Updates an existing event. */
    @Update
    suspend fun update(event: MaintenanceEventEntity)

    /** Deletes an event. */
    @Delete
    suspend fun delete(event: MaintenanceEventEntity)

    /** All events for one car, ordered by how soon they're due. */
    @Query(
        """
        SELECT * FROM MaintenanceEventEntity
        WHERE carId = :carId
        ORDER BY nextDueMileage ASC
        """,
    )
    fun getAllForCar(carId: Long): Flow<List<MaintenanceEventEntity>>

    /** Every event across every car, ordered by how soon they're due — feeds the Home screen. */
    @Query(
        """
        SELECT * FROM MaintenanceEventEntity
        ORDER BY nextDueMileage ASC
        """,
    )
    fun getAllUpcoming(): Flow<List<MaintenanceEventEntity>>

    /** One event by id, one-shot. */
    @Query("SELECT * FROM MaintenanceEventEntity WHERE id = :eventId LIMIT 1")
    suspend fun getByIdOnce(eventId: Long): MaintenanceEventEntity?
}