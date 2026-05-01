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
import carman.composeapp.generated.resources.Res
import carman.composeapp.generated.resources.car_mileage
import carman.composeapp.generated.resources.car_plate
import carman.composeapp.generated.resources.cars_add_cd
import carman.composeapp.generated.resources.cars_empty
import carman.composeapp.generated.resources.cars_title
import edu.moravian.csci395.carman.data.CarDao
import edu.moravian.csci395.carman.data.CarEntity
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource

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
        topBar = { TopAppBar(title = { Text(stringResource(Res.string.cars_title)) }) },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddCarClick) {
                Icon(Icons.Default.Add, contentDescription = stringResource(Res.string.cars_add_cd))
            }
        },
    ) { padding ->
        if (cars.isEmpty()) {
            Box(
                Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(Res.string.cars_empty),
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
                Text(
                    text = stringResource(Res.string.car_plate, plate),
                    style = MaterialTheme.typography.bodySmall,
                )
            }
            car.currentMileage?.let { miles ->
                Text(
                    text = stringResource(Res.string.car_mileage, miles),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}