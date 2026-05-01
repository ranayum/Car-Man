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
import carman.composeapp.generated.resources.Res
import carman.composeapp.generated.resources.action_back_cd
import carman.composeapp.generated.resources.action_cancel
import carman.composeapp.generated.resources.action_save
import carman.composeapp.generated.resources.action_saving
import carman.composeapp.generated.resources.add_car_color
import carman.composeapp.generated.resources.add_car_license
import carman.composeapp.generated.resources.add_car_make
import carman.composeapp.generated.resources.add_car_make_placeholder
import carman.composeapp.generated.resources.add_car_mileage
import carman.composeapp.generated.resources.add_car_model
import carman.composeapp.generated.resources.add_car_model_placeholder
import carman.composeapp.generated.resources.add_car_title
import carman.composeapp.generated.resources.add_car_weekly_miles
import carman.composeapp.generated.resources.add_car_year
import edu.moravian.csci395.carman.data.CarDao
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource

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
                title = { Text(stringResource(Res.string.add_car_title)) },
                navigationIcon = {
                    IconButton(onClick = onCancel) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(Res.string.action_back_cd),
                        )
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
                label = { Text(stringResource(Res.string.add_car_make)) },
                placeholder = { Text(stringResource(Res.string.add_car_make_placeholder)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = model,
                onValueChange = vm::setModel,
                label = { Text(stringResource(Res.string.add_car_model)) },
                placeholder = { Text(stringResource(Res.string.add_car_model_placeholder)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = year,
                onValueChange = vm::setYear,
                label = { Text(stringResource(Res.string.add_car_year)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = licensePlate,
                onValueChange = vm::setLicensePlate,
                label = { Text(stringResource(Res.string.add_car_license)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = color,
                onValueChange = vm::setColor,
                label = { Text(stringResource(Res.string.add_car_color)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = currentMileage,
                onValueChange = vm::setCurrentMileage,
                label = { Text(stringResource(Res.string.add_car_mileage)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = weeklyAverageMiles,
                onValueChange = vm::setWeeklyAverageMiles,
                label = { Text(stringResource(Res.string.add_car_weekly_miles)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
            )

            Button(
                onClick = { vm.save(onSaved) },
                enabled = vm.canSave() && !isSaving,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(stringResource(if (isSaving) Res.string.action_saving else Res.string.action_save))
            }
            OutlinedButton(
                onClick = onCancel,
                enabled = !isSaving,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(stringResource(Res.string.action_cancel))
            }
        }
    }
}