package edu.moravian.csci395.carman.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import edu.moravian.csci395.carman.data.CarManSettings
import kotlinx.serialization.Serializable

@Serializable
object Settings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    settings: CarManSettings,
    vm: SettingsVM = viewModel { SettingsVM() },
) {
    LaunchedEffect(settings) { vm.setup(settings) }

    val ownerName          by vm.ownerName.collectAsState()
    val notificationsOn    by vm.notificationsEnabled.collectAsState()
    val oilMiles           by vm.defaultOilMiles.collectAsState()
    val tireMiles          by vm.defaultTireMiles.collectAsState()
    val brakeMiles         by vm.defaultBrakeMiles.collectAsState()
    val darkTheme          by vm.useDarkTheme.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Settings") }) },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // ── Profile ──────────────────────────────────────────
            SectionHeader("Profile")
            OutlinedTextField(
                value = ownerName,
                onValueChange = vm::setOwnerName,
                label = { Text("Your name") },
                placeholder = { Text("e.g. Alex") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                // Save to DataStore when focus leaves the field
                supportingText = { Text("Saved automatically") },
            )
            // trigger save on every change (debounce not needed for class project)
            LaunchedEffect(ownerName) { vm.saveOwnerName() }

            HorizontalDivider()

            // ── Notifications ─────────────────────────────────────
            SectionHeader("Notifications")
            SettingSwitch(
                label = "Weekly mileage reminder",
                checked = notificationsOn,
                onCheckedChange = vm::setNotificationsEnabled,
            )

            HorizontalDivider()

            // ── Default intervals ─────────────────────────────────
            SectionHeader("Default maintenance intervals")
            OutlinedTextField(
                value = oilMiles,
                onValueChange = vm::setDefaultOilMiles,
                label = { Text("Oil change (mi)") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = tireMiles,
                onValueChange = vm::setDefaultTireMiles,
                label = { Text("Tire check (mi)") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = brakeMiles,
                onValueChange = vm::setDefaultBrakeMiles,
                label = { Text("Brake check (mi)") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
            )

            HorizontalDivider()

            // ── Appearance ────────────────────────────────────────
            SectionHeader("Appearance")
            SettingSwitch(
                label = "Dark theme",
                checked = darkTheme,
                onCheckedChange = vm::setUseDarkTheme,
            )
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
    )
}

@Composable
private fun SettingSwitch(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}