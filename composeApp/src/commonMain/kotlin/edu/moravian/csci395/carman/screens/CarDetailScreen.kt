package edu.moravian.csci395.carman.screens

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
import androidx.compose.material3.CardDefaults
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
import androidx.compose.foundation.layout.height
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import androidx.lifecycle.viewmodel.compose.viewModel
import carman.composeapp.generated.resources.Res
import carman.composeapp.generated.resources.car_color
import carman.composeapp.generated.resources.car_detail_add_event_cd
import carman.composeapp.generated.resources.car_detail_back_cd
import carman.composeapp.generated.resources.car_detail_current_mileage
import carman.composeapp.generated.resources.car_detail_events_empty
import carman.composeapp.generated.resources.car_detail_interval
import carman.composeapp.generated.resources.car_detail_loading
import carman.composeapp.generated.resources.car_detail_log_mileage
import carman.composeapp.generated.resources.car_detail_maintenance
import carman.composeapp.generated.resources.car_detail_next_due
import carman.composeapp.generated.resources.car_detail_mark_done
import carman.composeapp.generated.resources.car_detail_overdue
import carman.composeapp.generated.resources.car_detail_title_fallback
import carman.composeapp.generated.resources.car_detail_weekly_avg
import carman.composeapp.generated.resources.car_mileage_not_set
import carman.composeapp.generated.resources.car_plate
import edu.moravian.csci395.carman.data.CarDao
import edu.moravian.csci395.carman.data.MaintenanceEventDao
import edu.moravian.csci395.carman.data.MaintenanceEventEntity
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource

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
    vm: CarDetailVM = viewModel { CarDetailVM() },
) {
    LaunchedEffect(carId, carDao, eventDao) {
        vm.setup(carId, carDao, eventDao)
    }
    val car by vm.car.collectAsState()
    val events by vm.events.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        car?.let { "${it.year} ${it.make} ${it.model}" }
                            ?: stringResource(Res.string.car_detail_title_fallback),
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(Res.string.car_detail_back_cd),
                        )
                    }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { onAddEventClick(carId) }) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = stringResource(Res.string.car_detail_add_event_cd),
                )
            }
        },
    ) { padding ->
        val currentCar = car
        if (currentCar == null) {
            Box(
                Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center,
            ) {
                Text(stringResource(Res.string.car_detail_loading))
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item("summary") { CarSummaryCard(currentCar.licensePlate, currentCar.color, currentCar.photoPath) }

                item("mileage") {
                    MileageCard(
                        currentMileage = currentCar.currentMileage,
                        weeklyAverageMiles = null,
                        onLogMileage = { onLogMileageClick(carId) },
                    )
                }

                item("header") {
                    Text(
                        text = stringResource(Res.string.car_detail_maintenance),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(top = 8.dp),
                    )
                }

                if (events.isEmpty()) {
                    item("empty") {
                        Text(
                            text = stringResource(Res.string.car_detail_events_empty),
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                } else {
                    items(events, key = { it.id }) { event ->
                        EventRow(
                            event = event,
                            currentMileage = currentCar.currentMileage,
                            onMarkDone = { vm.completeEvent(event) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CarSummaryCard(licensePlate: String?, color: String?, photoPath: String?) {
    if (licensePlate == null && color == null && photoPath == null) return
    Card(modifier = Modifier.fillMaxWidth()) {
        Column {
            photoPath?.let {
                AsyncImage(
                    model = it,
                    contentDescription = "Car photo",
                    modifier = Modifier.fillMaxWidth().height(200.dp),
                    contentScale = ContentScale.Crop,
                )
            }
            Column(Modifier.padding(16.dp)) {
                licensePlate?.let {
                    Text(
                        text = stringResource(Res.string.car_plate, it),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
                color?.let {
                    Text(
                        text = stringResource(Res.string.car_color, it),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
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
                text = currentMileage
                    ?.let { stringResource(Res.string.car_detail_current_mileage, it) }
                    ?: stringResource(Res.string.car_mileage_not_set),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
            )
            weeklyAverageMiles?.let {
                Text(stringResource(Res.string.car_detail_weekly_avg, it), style = MaterialTheme.typography.bodyMedium)
            }
            OutlinedButton(onClick = onLogMileage) {
                Text(stringResource(Res.string.car_detail_log_mileage))
            }
        }
    }
}

@Composable
private fun EventRow(
    event: MaintenanceEventEntity,
    currentMileage: Int?,
    onMarkDone: () -> Unit,
) {
    val isOverdue = currentMileage != null &&
            event.nextDueMileage != null &&
            currentMileage >= event.nextDueMileage

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = if (isOverdue)
            CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
        else
            CardDefaults.cardColors(),
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isOverdue) MaterialTheme.colorScheme.error else Color.Unspecified,
                )
                if (isOverdue) {
                    Text(
                        text = stringResource(Res.string.car_detail_overdue),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            }
            Text(
                text = stringResource(Res.string.car_detail_interval, event.intervalMiles),
                style = MaterialTheme.typography.bodySmall,
            )
            event.nextDueMileage?.let { due ->
                Text(
                    text = stringResource(Res.string.car_detail_next_due, due),
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isOverdue) MaterialTheme.colorScheme.error else Color.Unspecified,
                )
            }
            androidx.compose.material3.TextButton(
                onClick = onMarkDone,
                modifier = Modifier.align(Alignment.End),
            ) {
                Text(
                    text = stringResource(Res.string.car_detail_mark_done),
                    color = if (isOverdue) MaterialTheme.colorScheme.error
                    else MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}