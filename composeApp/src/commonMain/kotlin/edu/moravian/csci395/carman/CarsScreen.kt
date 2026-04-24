package edu.moravian.csci395.carman

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.serialization.Serializable

/** Route for the My Cars list tab. */
@Serializable
object Cars

/** List of all cars owned by the user. */
@Composable
fun CarsScreen(
    onCarClick: (Long) -> Unit,
    onAddCarClick: () -> Unit,
) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("My Cars")
    }
}