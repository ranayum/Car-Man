package edu.moravian.csci395.carman.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.maplibre.spatialk.geojson.Position
import org.maplibre.compose.camera.CameraPosition
import org.maplibre.compose.camera.rememberCameraState
import org.maplibre.compose.map.MaplibreMap
import org.maplibre.compose.style.BaseStyle
import org.maplibre.compose.style.rememberStyleState
import kotlinx.serialization.Serializable

/** Route for the Mechanics map tab. */
@Serializable
object MechanicsMap

/** Map showing pins for all mechanics the user has recorded. */
@Composable
fun MapScreen() {
    val cameraState = rememberCameraState(
        firstPosition = CameraPosition(
            target = Position(longitude = -75.381, latitude = 40.630),
            zoom = 14.0,
        )
    )

    MaplibreMap(
        modifier = Modifier.fillMaxSize(),
        baseStyle = BaseStyle.Uri("https://tiles.openfreemap.org/styles/liberty"),
        cameraState = cameraState,
        styleState = rememberStyleState()
    )
}