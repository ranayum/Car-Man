package edu.moravian.csci395.carman

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.serialization.Serializable

/** Route for updating a car's current mileage. */
@Serializable
data class LogMileage(val carId: Long)

/** Simple form to update current mileage for a car. */
@Composable
fun LogMileageScreen(
    carId: Long,
    onSaved: () -> Unit,
    onCancel: () -> Unit,
) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Log Mileage for car $carId")
    }
}