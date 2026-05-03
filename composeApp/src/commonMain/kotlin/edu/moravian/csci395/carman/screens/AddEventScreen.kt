package edu.moravian.csci395.carman.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import carman.composeapp.generated.resources.Res
import carman.composeapp.generated.resources.action_back_cd
import carman.composeapp.generated.resources.action_cancel
import carman.composeapp.generated.resources.action_save
import carman.composeapp.generated.resources.action_saving
import carman.composeapp.generated.resources.add_event_delete_cd
import carman.composeapp.generated.resources.add_event_interval_field
import carman.composeapp.generated.resources.add_event_title_add
import carman.composeapp.generated.resources.add_event_title_edit
import carman.composeapp.generated.resources.add_event_title_field
import carman.composeapp.generated.resources.add_event_type_brakes
import carman.composeapp.generated.resources.add_event_type_custom
import carman.composeapp.generated.resources.add_event_type_label
import carman.composeapp.generated.resources.add_event_type_oil
import carman.composeapp.generated.resources.add_event_type_tires
import carman.composeapp.generated.resources.field_notes
import edu.moravian.csci395.carman.data.CarDao
import edu.moravian.csci395.carman.data.MaintenanceEventDao
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource

/** Route for scheduling a new maintenance event on a specific car. */
@Serializable
data class AddEvent(val carId: Long)

/** Route for editing an existing maintenance event. */
@Serializable
data class EditEvent(val eventId: Long)

/** Form for adding/editing a mileage-based maintenance event. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEventScreen(
    carId: Long,
    carDao: CarDao,
    eventDao: MaintenanceEventDao,
    onSaved: () -> Unit,
    onCancel: () -> Unit,
    eventId: Long? = null,
    vm: AddEventVM = viewModel { AddEventVM() },
) {
    LaunchedEffect(carId, carDao, eventDao, eventId) {
        vm.setup(carId, carDao, eventDao, eventId)
    }

    val type by vm.type.collectAsState()
    val title by vm.title.collectAsState()
    val intervalMiles by vm.intervalMiles.collectAsState()
    val notes by vm.notes.collectAsState()
    val isSaving by vm.isSaving.collectAsState()
    val isEditMode by vm.isEditMode.collectAsState()

    val eventTypes = listOf(
        "OIL_CHANGE" to stringResource(Res.string.add_event_type_oil),
        "TIRE_CHECK" to stringResource(Res.string.add_event_type_tires),
        "BRAKE_CHECK" to stringResource(Res.string.add_event_type_brakes),
        "CUSTOM" to stringResource(Res.string.add_event_type_custom),
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(if (isEditMode) Res.string.add_event_title_edit else Res.string.add_event_title_add))
                },
                navigationIcon = {
                    IconButton(onClick = onCancel) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(Res.string.action_back_cd))
                    }
                },
                actions = {
                    if (isEditMode) {
                        IconButton(onClick = { vm.delete(onSaved) }) {
                            Icon(Icons.Default.Delete, contentDescription = stringResource(Res.string.add_event_delete_cd))
                        }
                    }
                }
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(stringResource(Res.string.add_event_type_label), style = MaterialTheme.typography.titleMedium)
            Column(Modifier.selectableGroup()) {
                eventTypes.forEach { (value, label) ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = (type == value),
                                onClick = { vm.setType(value) },
                                role = Role.RadioButton
                            )
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = (type == value), onClick = null)
                        Text(
                            text = label,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                }
            }

            OutlinedTextField(
                value = title,
                onValueChange = vm::setTitle,
                label = { Text(stringResource(Res.string.add_event_title_field)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )

            OutlinedTextField(
                value = intervalMiles,
                onValueChange = vm::setIntervalMiles,
                label = { Text(stringResource(Res.string.add_event_interval_field)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )

            OutlinedTextField(
                value = notes,
                onValueChange = vm::setNotes,
                label = { Text(stringResource(Res.string.field_notes)) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Spacer(Modifier.weight(1f))

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
