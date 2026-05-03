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
import androidx.compose.material3.TextButton
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
import edu.moravian.csci395.carman.data.CarDao
import edu.moravian.csci395.carman.data.MaintenanceEventDao
import kotlinx.serialization.Serializable

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
        "OIL_CHANGE" to "Oil Change",
        "TIRE_CHECK" to "Tires",
        "BRAKE_CHECK" to "Brakes",
        "CUSTOM" to "Custom"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditMode) "Edit Event" else "Add Maintenance Event") },
                navigationIcon = {
                    IconButton(onClick = onCancel) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (isEditMode) {
                        IconButton(onClick = { vm.delete(onSaved) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete")
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
            Text("Event Type", style = MaterialTheme.typography.titleMedium)
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
                        RadioButton(
                            selected = (type == value),
                            onClick = null // handled by row click
                        )
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
                label = { Text("Title *") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )

            OutlinedTextField(
                value = intervalMiles,
                onValueChange = vm::setIntervalMiles,
                label = { Text("Interval (miles) *") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )

            OutlinedTextField(
                value = notes,
                onValueChange = vm::setNotes,
                label = { Text("Notes") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Spacer(Modifier.weight(1f))

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
