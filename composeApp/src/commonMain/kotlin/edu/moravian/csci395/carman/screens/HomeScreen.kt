package edu.moravian.csci395.carman.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
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
import carman.composeapp.generated.resources.Res
import carman.composeapp.generated.resources.home_due_at
import carman.composeapp.generated.resources.home_empty_hint
import carman.composeapp.generated.resources.home_empty_message
import carman.composeapp.generated.resources.home_upcoming_title
import edu.moravian.csci395.carman.data.CarDao
import edu.moravian.csci395.carman.data.MaintenanceEventDao
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource

/** Route for the Home / Upcoming dashboard tab. */
@Serializable
object Home

/** Dashboard listing upcoming maintenance events across all cars. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    carDao: CarDao,
    eventDao: MaintenanceEventDao,
    onEventClick: (Long) -> Unit,
    vm: HomeVM = viewModel { HomeVM() }
) {
    LaunchedEffect(carDao, eventDao) {
        vm.setup(carDao, eventDao)
    }

    val upcomingEvents by vm.upcomingEvents.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(stringResource(Res.string.home_upcoming_title)) })
        }
    ) { padding ->
        if (upcomingEvents.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(stringResource(Res.string.home_empty_message), style = MaterialTheme.typography.bodyLarge)
                Text(stringResource(Res.string.home_empty_hint), style = MaterialTheme.typography.bodyMedium)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(upcomingEvents, key = { it.event.id }) { item ->
                    UpcomingEventRow(
                        upcoming = item,
                        onClick = { onEventClick(item.event.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun UpcomingEventRow(
    upcoming: UpcomingEvent,
    onClick: () -> Unit
) {
    val event = upcoming.event
    val car = upcoming.car
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                text = event.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            car?.let {
                Text(
                    text = "${it.year} ${it.make} ${it.model}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
            event.nextDueMileage?.let { due ->
                Text(
                    text = stringResource(Res.string.home_due_at, due),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}
