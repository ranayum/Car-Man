package edu.moravian.csci395.carman

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * A recurring mileage-based maintenance task for one car
 * (oil change, tire check, brake check, or a custom task).
 */
@Entity(
    foreignKeys = [
        ForeignKey(
            entity = CarEntity::class,
            parentColumns = ["id"],
            childColumns = ["carId"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = MechanicEntity::class,
            parentColumns = ["id"],
            childColumns = ["mechanicId"],
            onDelete = ForeignKey.SET_NULL,
        ),
    ],
    indices = [
        Index("carId"),
        Index("mechanicId"),
    ],
)
data class MaintenanceEventEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val carId: Long,
    /** One of: OIL_CHANGE, TIRE_CHECK, BRAKE_CHECK, CUSTOM. Stored as a String. */
    val type: String,
    val title: String,
    val notes: String? = null,
    /** How many miles between services (e.g. 3000 for an oil change). */
    val intervalMiles: Int,
    /** Odometer reading (miles) at the last completion; null if never done. */
    val lastCompletedMileage: Int? = null,
    /** Predicted due mileage = lastCompletedMileage + intervalMiles. */
    val nextDueMileage: Int? = null,
    /** Optional link to the mechanic who performed the last service. */
    val mechanicId: Long? = null,
    /** Per-event override for how many days before the due date to notify. */
    val notificationLeadDays: Int? = null,
)