package edu.moravian.csci395.carman.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import edu.moravian.csci395.carman.data.CarDao
import edu.moravian.csci395.carman.data.MaintenanceEventDao
import edu.moravian.csci395.carman.data.MaintenanceEventEntity
import kotlinx.serialization.Serializable

/** Route for a single car's detail view. */
@Serializable
data class CarDetail(val carId: Long)

/** Detail view for one car: identity, mileage, scheduled maintenance events. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarDetailScreen(
    carId: Long,
    carDao: CarDao,
    eventDao: MaintenanceEventDao,
    onBack: () -> Unit,
    onLogMileageClick: (Long) -> Unit,
    onAddEventClick: (Long) -> Unit,
    onEventClick: (Long) -> Unit,
    vm: CarDetailVM = viewModel(),
) {
    LaunchedEffect(carId, carDao, eventDao) {
        vm.setup(carId, carDao, eventDao)
    }
    val car by vm.car.collectAsState()
    val events by vm.events.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(car?.let { "${it.year} ${it.make} ${it.model}" } ?: "Car") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { onAddEventClick(carId) }) {
                Icon(Icons.Default.Add, contentDescription = "Add maintenance event")
            }
        },
    ) { padding ->
        val currentCar = car
        if (currentCar == null) {
            Box(
                Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center,
            ) {
                Text("Loading...")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item { CarSummaryCard(currentCar.licensePlate, currentCar.color) }

                item {
                    MileageCard(
                        currentMileage = currentCar.currentMileage,
                        weeklyAverageMiles = currentCar.weeklyAverageMiles,
                        onLogMileage = { onLogMileageClick(carId) },
                    )
                }

                item {
                    Text(
                        text = "Maintenance",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(top = 8.dp),
                    )
                }

                if (events.isEmpty()) {
                    item {
                        Text(
                            text = "No events scheduled. Tap + to add one.",
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                } else {
                    items(events, key = { it.id }) { event ->
                        EventRow(
                            event = event,
                            onClick = { onEventClick(event.id) },
                            onComplete = { vm.completeEvent(event) },
                            canComplete = currentCar.currentMileage != null
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CarSummaryCard(licensePlate: String?, color: String?) {
    if (licensePlate == null && color == null) return
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            licensePlate?.let { Text("Plate: $it", style = MaterialTheme.typography.bodyMedium) }
            color?.let { Text("Color: $it", style = MaterialTheme.typography.bodyMedium) }
        }
    }
}

@Composable
private fun MileageCard(
    currentMileage: Int?,
    weeklyAverageMiles: Int?,
    onLogMileage: () -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = currentMileage?.let { "$it mi" } ?: "Mileage not set",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
            )
            weeklyAverageMiles?.let {
                Text("≈ $it mi / week", style = MaterialTheme.typography.bodyMedium)
            }
            OutlinedButton(onClick = onLogMileage) {
                Text("Log mileage")
            }
        }
    }
}

@Composable
private fun EventRow(
    event: MaintenanceEventEntity,
    onClick: () -> Unit,
    onComplete: () -> Unit,
    canComplete: Boolean,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = "Every ${event.intervalMiles} mi",
                    style = MaterialTheme.typography.bodySmall,
                )
                event.nextDueMileage?.let { due ->
                    Text("Next due at $due mi", style = MaterialTheme.typography.bodySmall)
                }
            }

            OutlinedButton(
                onClick = onComplete,
                enabled = canComplete,
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
            ) {
                Text("Done", style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}
