package edu.moravian.csci395.carman.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.serialization.Serializable

/** Route for the Mechanics map tab. */
@Serializable
object MechanicsMap

/** Map showing pins for all mechanics the user has recorded. */
@Composable
fun MapScreen() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Mechanics Map")
    }
}