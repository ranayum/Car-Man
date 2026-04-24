package edu.moravian.csci395.carman

import androidx.room.Entity
import androidx.room.PrimaryKey

/** A mechanic shop the user has visited; displayed as a pin on the map screen. */
@Entity
data class MechanicEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val addressLine: String? = null,
    val latitude: Double,
    val longitude: Double,
    val phone: String? = null,
    val notes: String? = null,
    val createdAt: Long,
)