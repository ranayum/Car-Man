package edu.moravian.csci395.carman.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import carman.composeapp.generated.resources.log_mileage_current
import carman.composeapp.generated.resources.log_mileage_field
import carman.composeapp.generated.resources.log_mileage_no_record
import carman.composeapp.generated.resources.log_mileage_title
import edu.moravian.csci395.carman.data.CarDao
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource

/** Route for updating a car's current mileage. */
@Serializable
data class LogMileage(val carId: Long)

/** Simple form to update a car's current mileage; pre-shows the existing value. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogMileageScreen(
    carId: Long,
    carDao: CarDao,
    onSaved: () -> Unit,
    onCancel: () -> Unit,
    vm: LogMileageVM = viewModel { LogMileageVM() },
) {
    LaunchedEffect(carId, carDao) { vm.setup(carId, carDao) }

    val car by vm.car.collectAsState()
    val newMileage by vm.newMileage.collectAsState()
    val isSaving by vm.isSaving.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.log_mileage_title)) },
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            car?.let {
                Text(
                    text = "${it.year} ${it.make} ${it.model}",
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = it.currentMileage
                        ?.let { mi -> stringResource(Res.string.log_mileage_current, mi) }
                        ?: stringResource(Res.string.log_mileage_no_record),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            OutlinedTextField(
                value = newMileage,
                onValueChange = vm::setNewMileage,
                label = { Text(stringResource(Res.string.log_mileage_field)) },
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