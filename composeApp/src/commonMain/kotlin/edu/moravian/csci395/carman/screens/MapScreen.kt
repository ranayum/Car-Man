package edu.moravian.csci395.carman.screens

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import edu.moravian.csci395.carman.data.MechanicDao
import edu.moravian.csci395.carman.data.MechanicEntity
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonPrimitive
import org.maplibre.compose.camera.CameraPosition
import org.maplibre.compose.camera.rememberCameraState
import org.maplibre.compose.expressions.dsl.const
import org.maplibre.compose.layers.CircleLayer
import org.maplibre.compose.map.MaplibreMap
import org.maplibre.compose.sources.GeoJsonData
import org.maplibre.compose.sources.rememberGeoJsonSource
import org.maplibre.compose.style.BaseStyle
import org.maplibre.compose.style.rememberStyleState
import org.maplibre.compose.util.ClickResult
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
    LaunchedEffect(mechanicDao) {
        vm.setup(mechanicDao)
    }

    val mechanics by vm.mechanics.collectAsState()
    val discovered by vm.discoveredMechanics.collectAsState()
    val selectedMechanic by vm.selectedMechanic.collectAsState()

    val cameraState = rememberCameraState(
        firstPosition = CameraPosition(
            target = Position(longitude = -75.381, latitude = 40.630),
            zoom = 14.0,
        )
    )

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val width = maxWidth
        val height = maxHeight

        MaplibreMap(
            modifier = Modifier.fillMaxSize(),
            baseStyle = BaseStyle.Uri("https://tiles.openfreemap.org/styles/liberty"),
            cameraState = cameraState,
            styleState = rememberStyleState()
        ) {
            val savedGeoJson = remember(mechanics) { GeoJsonData.JsonString(mechanics.toGeoJson()) }
            val savedSource = rememberGeoJsonSource(data = savedGeoJson)
            CircleLayer(
                id = "saved-layer",
                source = savedSource,
                color = const(Color.Blue),
                radius = const(8.dp),
                strokeColor = const(Color.White),
                strokeWidth = const(2.dp),
                onClick = { features ->
                    val feature = features.firstOrNull() ?: return@CircleLayer ClickResult.Pass
                    val index = feature.properties?.get("index")?.jsonPrimitive?.intOrNull ?: return@CircleLayer ClickResult.Pass
                    vm.selectMechanic(mechanics[index])
                    ClickResult.Consume
                }
            )

            val discoveredGeoJson = remember(discovered) { GeoJsonData.JsonString(discovered.toGeoJson()) }
            val discoveredSource = rememberGeoJsonSource(data = discoveredGeoJson)
            CircleLayer(
                id = "discovered-layer",
                source = discoveredSource,
                color = const(Color.Red),
                radius = const(6.dp),
                strokeColor = const(Color.White),
                strokeWidth = const(1.dp),
                onClick = { features ->
                    val feature = features.firstOrNull() ?: return@CircleLayer ClickResult.Pass
                    val index = feature.properties?.get("index")?.jsonPrimitive?.intOrNull ?: return@CircleLayer ClickResult.Pass
                    vm.selectMechanic(discovered[index])
                    ClickResult.Consume
                }
            )
        }

        if (selectedMechanic == null) {
            Button(
                onClick = {
                    val proj = cameraState.projection ?: return@Button
                    val tl = proj.positionFromScreenLocation(DpOffset(0.dp, 0.dp))
                    val br = proj.positionFromScreenLocation(DpOffset(width, height))
                    vm.searchArea(
                        north = tl.latitude,
                        south = br.latitude,
                        east = br.longitude,
                        west = tl.longitude
                    )
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp)
            ) {
                Text("Search this area")
            }
        } else {
            selectedMechanic?.let { mechanic ->
                Card(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                        .fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = mechanic.name,
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(onClick = { vm.selectMechanic(null) }) {
                                Icon(Icons.Default.Close, contentDescription = "Close")
                            }
                        }
                        mechanic.addressLine?.let { Text(it) }
                        mechanic.phone?.let { Text(it) }
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // If it's a discovered mechanic (id == 0), show a Save button
                        if (mechanic.id == 0L) {
                            Button(
                                onClick = { vm.saveMechanic(mechanic, mechanicDao) },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Save to My Mechanics")
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun List<MechanicEntity>.toGeoJson(): String {
    if (isEmpty()) return """{"type": "FeatureCollection", "features": []}"""
    val features = mapIndexed { index, it ->
        """
        {
          "type": "Feature",
          "geometry": {
            "type": "Point",
            "coordinates": [${it.longitude}, ${it.latitude}]
          },
          "properties": {
            "index": $index,
            "name": "${it.name.replace("\"", "\\\"")}"
          }
        }
        """.trimIndent()
    }.joinToString(",")
    return """{"type": "FeatureCollection", "features": [$features]}"""
}
