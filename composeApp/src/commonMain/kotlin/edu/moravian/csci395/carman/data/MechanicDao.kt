package edu.moravian.csci395.carman.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/** DAO for reading and writing [MechanicEntity] rows. */
@Dao
interface MechanicDao {
    /** Inserts a mechanic and returns the new row's id. */
    @Insert
    suspend fun insert(mechanic: MechanicEntity): Long

    /** Updates an existing mechanic. */
    @Update
    suspend fun update(mechanic: MechanicEntity)

    /** Deletes a mechanic. */
    @Delete
    suspend fun delete(mechanic: MechanicEntity)

    /** All mechanics — used to populate the map pins. */
    @Query("SELECT * FROM MechanicEntity ORDER BY createdAt DESC")
    fun getAll(): Flow<List<MechanicEntity>>

    /** One mechanic by id, one-shot. */
    @Query("SELECT * FROM MechanicEntity WHERE id = :mechanicId LIMIT 1")
    suspend fun getByIdOnce(mechanicId: Long): MechanicEntity?
}