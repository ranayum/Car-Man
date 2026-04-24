package edu.moravian.csci395.carman.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * A car owned by the user. Tracks identification info plus the data needed
 * to predict when mileage-based maintenance events come due.
 */
@Entity
data class CarEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val make: String,
    val model: String,
    val year: Int,
    val licensePlate: String? = null,
    val color: String? = null,
    /** Absolute path/URI of the car's photo on local storage. */
    val photoPath: String? = null,
    /** Most recently logged odometer reading (miles). */
    val currentMileage: Int? = null,
    /** User's estimated weekly driving distance (miles) — used to predict due dates. */
    val weeklyAverageMiles: Int? = null,
    /** Epoch millis when currentMileage was last updated. */
    val mileageUpdatedAt: Long? = null,
    /** Epoch millis when the car was first added. */
    val createdAt: Long,
)