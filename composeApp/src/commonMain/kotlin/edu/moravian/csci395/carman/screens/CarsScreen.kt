package edu.moravian.csci395.carman.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
import edu.moravian.csci395.carman.data.CarDao
import edu.moravian.csci395.carman.data.CarEntity
import kotlinx.serialization.Serializable

/** Route for the My Cars list tab. */
@Serializable
object Cars

/** List of all cars; tap a row to open detail, FAB to add a new car. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarsScreen(
    carDao: CarDao,
    onCarClick: (Long) -> Unit,
    onAddCarClick: () -> Unit,
    vm: CarsVM = viewModel(),
) {
    LaunchedEffect(carDao) { vm.setup(carDao) }
    val cars by vm.cars.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("My Cars") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddCarClick) {
                Icon(Icons.Default.Add, contentDescription = "Add car")
            }
        },
    ) { padding ->
        if (cars.isEmpty()) {
            Box(
                Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "No cars yet — tap + to add one.",
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(cars, key = { it.id }) { car ->
                    CarRow(car = car, onClick = { onCarClick(car.id) })
                }
            }
        }
    }
}

/** A single row representing one car in the list. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CarRow(car: CarEntity, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                text = "${car.year} ${car.make} ${car.model}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            car.licensePlate?.let { plate ->
                Text(plate, style = MaterialTheme.typography.bodySmall)
            }
            car.currentMileage?.let { miles ->
                Text("$miles mi", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}