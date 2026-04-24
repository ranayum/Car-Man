package edu.moravian.csci395.carman

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.serialization.Serializable

/** Route for adding a new car. */
@Serializable
object AddCar

/** Form for entering a new car's details (make, model, photo, mileage, etc.). */
@Composable
fun AddCarScreen(
    onSaved: () -> Unit,
    onCancel: () -> Unit,
) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Add Car")
    }
}