package edu.moravian.csci395.carman

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.serialization.Serializable

/** Route for scheduling a new maintenance event on a specific car. */
@Serializable
data class AddEvent(val carId: Long)

/** Form for adding a mileage-based maintenance event (oil change, tire, brake, custom). */
@Composable
fun AddEventScreen(
    carId: Long,
    onSaved: () -> Unit,
    onCancel: () -> Unit,
) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Add Event for car $carId")
    }
}