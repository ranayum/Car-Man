package edu.moravian.csci395.carman

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/** DAO for reading and writing [CarEntity] rows. */
@Dao
interface CarDao {
    /** Inserts a car and returns the new row's id. */
    @Insert
    suspend fun insert(car: CarEntity): Long

    /** Updates an existing car. Its id must already exist in the database. */
    @Update
    suspend fun update(car: CarEntity)

    /** Deletes a car (and cascades to its maintenance events). */
    @Delete
    suspend fun delete(car: CarEntity)

    /** All cars, newest first. Flow so the UI updates when anything changes. */
    @Query("SELECT * FROM CarEntity ORDER BY createdAt DESC")
    fun getAll(): Flow<List<CarEntity>>

    /** One car by id, reactive — emits null if the car is deleted. */
    @Query("SELECT * FROM CarEntity WHERE id = :carId LIMIT 1")
    fun getById(carId: Long): Flow<CarEntity?>

    /** One car by id, one-shot (non-reactive). Useful for snapshots inside suspend functions. */
    @Query("SELECT * FROM CarEntity WHERE id = :carId LIMIT 1")
    suspend fun getByIdOnce(carId: Long): CarEntity?

    /** Convenience: update just the mileage fields without rewriting the whole row. */
    @Query(
        """
        UPDATE CarEntity
        SET currentMileage = :mileage, mileageUpdatedAt = :timestamp
        WHERE id = :carId
        """,
    )
    suspend fun updateMileage(carId: Long, mileage: Int, timestamp: Long)
}