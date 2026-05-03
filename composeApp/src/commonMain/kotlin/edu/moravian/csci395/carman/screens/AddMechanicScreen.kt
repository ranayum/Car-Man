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
import carman.composeapp.generated.resources.add_mechanic_address
import carman.composeapp.generated.resources.add_mechanic_lat
import carman.composeapp.generated.resources.add_mechanic_lng
import carman.composeapp.generated.resources.add_mechanic_name
import carman.composeapp.generated.resources.add_mechanic_phone
import carman.composeapp.generated.resources.add_mechanic_title
import carman.composeapp.generated.resources.field_notes
import edu.moravian.csci395.carman.data.MechanicDao
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource

/** Route for adding a new mechanic. */
@Serializable
object AddMechanic

/** Form for adding a mechanic shop. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMechanicScreen(
    mechanicDao: MechanicDao,
    onSaved: () -> Unit,
    onCancel: () -> Unit,
    vm: AddMechanicVM = viewModel { AddMechanicVM() }
) {
    LaunchedEffect(mechanicDao) {
        vm.setup(mechanicDao)
    }

    val name by vm.name.collectAsState()
    val address by vm.address.collectAsState()
    val latitude by vm.latitude.collectAsState()
    val longitude by vm.longitude.collectAsState()
    val phone by vm.phone.collectAsState()
    val notes by vm.notes.collectAsState()
    val isSaving by vm.isSaving.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.add_mechanic_title)) },
                navigationIcon = {
                    IconButton(onClick = onCancel) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(Res.string.action_back_cd))
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = vm::setName,
                label = { Text(stringResource(Res.string.add_mechanic_name)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = address,
                onValueChange = vm::setAddress,
                label = { Text(stringResource(Res.string.add_mechanic_address)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = phone,
                onValueChange = vm::setPhone,
                label = { Text(stringResource(Res.string.add_mechanic_phone)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = latitude,
                onValueChange = vm::setLatitude,
                label = { Text(stringResource(Res.string.add_mechanic_lat)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = longitude,
                onValueChange = vm::setLongitude,
                label = { Text(stringResource(Res.string.add_mechanic_lng)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = notes,
                onValueChange = vm::setNotes,
                label = { Text(stringResource(Res.string.field_notes)) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Button(
                onClick = { vm.save(onSaved) },
                enabled = vm.canSave() && !isSaving,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(if (isSaving) Res.string.action_saving else Res.string.action_save))
            }
            OutlinedButton(
                onClick = onCancel,
                enabled = !isSaving,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(Res.string.action_cancel))
            }
        }
    }
}
