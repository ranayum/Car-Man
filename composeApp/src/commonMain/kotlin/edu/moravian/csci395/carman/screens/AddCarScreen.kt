package edu.moravian.csci395.carman.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import edu.moravian.csci395.carman.data.CarDao
import kotlinx.serialization.Serializable

/** Route for adding a new car. */
@Serializable
object AddCar

/** Form for entering a new car's details and saving it to the database. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCarScreen(
    carDao: CarDao,
    onSaved: () -> Unit,
    onCancel: () -> Unit,
    vm: AddCarVM = viewModel(),
) {
    LaunchedEffect(carDao) { vm.setup(carDao) }

    val make by vm.make.collectAsState()
    val model by vm.model.collectAsState()
    val year by vm.year.collectAsState()
    val licensePlate by vm.licensePlate.collectAsState()
    val color by vm.color.collectAsState()
    val currentMileage by vm.currentMileage.collectAsState()
    val weeklyAverageMiles by vm.weeklyAverageMiles.collectAsState()
    val isSaving by vm.isSaving.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Car") },
                navigationIcon = {
                    IconButton(onClick = onCancel) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            OutlinedTextField(
                value = make,
                onValueChange = vm::setMake,
                label = { Text("Make *") },
                placeholder = { Text("Toyota") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = model,
                onValueChange = vm::setModel,
                label = { Text("Model *") },
                placeholder = { Text("Corolla") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = year,
                onValueChange = vm::setYear,
                label = { Text("Year *") },
                placeholder = { Text("2018") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = licensePlate,
                onValueChange = vm::setLicensePlate,
                label = { Text("License plate") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = color,
                onValueChange = vm::setColor,
                label = { Text("Color") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = currentMileage,
                onValueChange = vm::setCurrentMileage,
                label = { Text("Current mileage") },
                placeholder = { Text("e.g. 32000") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = weeklyAverageMiles,
                onValueChange = vm::setWeeklyAverageMiles,
                label = { Text("Weekly average miles") },
                placeholder = { Text("e.g. 200") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
            )

            // Save / Cancel buttons
            Button(
                onClick = { vm.save(onSaved) },
                enabled = vm.canSave() && !isSaving,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(if (isSaving) "Saving..." else "Save")
            }
            OutlinedButton(
                onClick = onCancel,
                enabled = !isSaving,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Cancel")
            }
        }
    }
}