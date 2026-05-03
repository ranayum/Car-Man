package edu.moravian.csci395.carman.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import edu.moravian.csci395.carman.data.MechanicDao
import kotlinx.serialization.Serializable
import org.maplibre.compose.camera.CameraPosition
import org.maplibre.compose.camera.rememberCameraState
import org.maplibre.compose.map.MaplibreMap
import org.maplibre.compose.style.BaseStyle
import org.maplibre.compose.style.rememberStyleState
import org.maplibre.spatialk.geojson.Position

/** Route for the Mechanics map tab. */
@Serializable
object MechanicsMap

/** Map showing pins for all mechanics the user has recorded. */
@Composable
fun MapScreen(
    mechanicDao: MechanicDao,
    vm: MapVM = viewModel { MapVM() }
) {
    androidx.compose.runtime.LaunchedEffect(mechanicDao) {
        vm.setup(mechanicDao)
    }

    val mechanics by vm.mechanics.collectAsState()

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
