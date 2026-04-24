package edu.moravian.csci395.carman.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.serialization.Serializable

/** Route for a single car's detail view. */
@Serializable
data class CarDetail(val carId: Long)

/** Details of one car: photo, mileage, scheduled events, history. */
@Composable
fun CarDetailScreen(
    carId: Long,
    onLogMileageClick: (Long) -> Unit,
    onAddEventClick: (Long) -> Unit,
) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Car Detail (id = $carId)")
    }
}