package edu.moravian.csci395.carman.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import edu.moravian.csci395.carman.data.CarManSettings
import edu.moravian.csci395.carman.data.MaintenanceEventDao
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource

@Serializable
object Home

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    carDao: CarDao,
    eventDao: MaintenanceEventDao,
    settings: CarManSettings,
    onEventClick: (Long) -> Unit,
    vm: HomeVM = viewModel { HomeVM() }
) {
    LaunchedEffect(carDao, eventDao, settings) {
        vm.setup(carDao, eventDao, settings)
    }

    val upcomingEvents by vm.upcomingEvents.collectAsState()
    val ownerName by vm.ownerName.collectAsState()

    val overdueCount = upcomingEvents.count { item ->
        item.car?.currentMileage != null &&
                item.event.nextDueMileage != null &&
                item.car.currentMileage >= item.event.nextDueMileage
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(stringResource(Res.string.home_upcoming_title)) })
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item("greeting") {
                GreetingCard(name = ownerName, overdueCount = overdueCount)
            }

            if (upcomingEvents.isEmpty()) {
                item("empty") {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(top = 32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(stringResource(Res.string.home_empty_message), style = MaterialTheme.typography.bodyLarge)
                        Text(stringResource(Res.string.home_empty_hint), style = MaterialTheme.typography.bodyMedium)
                    }
                }
            } else {
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
private fun GreetingCard(name: String, overdueCount: Int) {
    val greeting = if (name.isNotBlank()) "Hello, $name!" else "Hello!"
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
    ) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = greeting,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
            Text(
                text = if (overdueCount > 0)
                    "$overdueCount item${if (overdueCount > 1) "s" else ""} need${if (overdueCount == 1) "s" else ""} attention"
                else
                    "Your cars are all caught up!",
                style = MaterialTheme.typography.bodyMedium,
                color = if (overdueCount > 0)
                    MaterialTheme.colorScheme.error
                else
                    MaterialTheme.colorScheme.onPrimaryContainer,
            )
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
    val isOverdue = car?.currentMileage != null &&
            event.nextDueMileage != null &&
            car.currentMileage >= event.nextDueMileage

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
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
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isOverdue) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
                )
                if (isOverdue) {
                    Text(
                        text = "OVERDUE",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            }
            car?.let {
                Text(
                    text = "${it.year} ${it.make} ${it.model}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isOverdue) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary,
                )
            }
            event.nextDueMileage?.let { due ->
                Text(
                    text = stringResource(Res.string.home_due_at, due),
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isOverdue) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}
