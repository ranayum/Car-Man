package edu.moravian.csci395.carman.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import carman.composeapp.generated.resources.Res
import carman.composeapp.generated.resources.settings_brake_miles
import carman.composeapp.generated.resources.settings_dark_theme
import carman.composeapp.generated.resources.settings_lang_english
import carman.composeapp.generated.resources.settings_lang_german
import carman.composeapp.generated.resources.settings_oil_miles
import carman.composeapp.generated.resources.settings_owner_name_hint
import carman.composeapp.generated.resources.settings_owner_name_label
import carman.composeapp.generated.resources.settings_owner_name_placeholder
import carman.composeapp.generated.resources.settings_section_appearance
import carman.composeapp.generated.resources.settings_section_intervals
import carman.composeapp.generated.resources.settings_section_language
import carman.composeapp.generated.resources.settings_section_notifications
import carman.composeapp.generated.resources.settings_section_profile
import carman.composeapp.generated.resources.settings_tire_miles
import carman.composeapp.generated.resources.settings_title
import carman.composeapp.generated.resources.settings_weekly_reminder
import edu.moravian.csci395.carman.data.CarManSettings
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource

@Serializable
object Settings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    settings: CarManSettings,
    vm: SettingsVM = viewModel { SettingsVM() },
) {
    LaunchedEffect(settings) { vm.setup(settings) }

    val ownerName       by vm.ownerName.collectAsState()
    val notificationsOn by vm.notificationsEnabled.collectAsState()
    val oilMiles        by vm.defaultOilMiles.collectAsState()
    val tireMiles       by vm.defaultTireMiles.collectAsState()
    val brakeMiles      by vm.defaultBrakeMiles.collectAsState()
    val darkTheme       by vm.useDarkTheme.collectAsState()
    val language        by vm.language.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(Res.string.settings_title)) }) },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            SectionHeader(stringResource(Res.string.settings_section_profile))
            OutlinedTextField(
                value = ownerName,
                onValueChange = vm::setOwnerName,
                label = { Text(stringResource(Res.string.settings_owner_name_label)) },
                placeholder = { Text(stringResource(Res.string.settings_owner_name_placeholder)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                supportingText = { Text(stringResource(Res.string.settings_owner_name_hint)) },
            )
            LaunchedEffect(ownerName) { vm.saveOwnerName() }

            HorizontalDivider()

            SectionHeader(stringResource(Res.string.settings_section_notifications))
            SettingSwitch(
                label = stringResource(Res.string.settings_weekly_reminder),
                checked = notificationsOn,
                onCheckedChange = vm::setNotificationsEnabled,
            )

            HorizontalDivider()

            SectionHeader(stringResource(Res.string.settings_section_intervals))
            OutlinedTextField(
                value = oilMiles,
                onValueChange = vm::setDefaultOilMiles,
                label = { Text(stringResource(Res.string.settings_oil_miles)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = tireMiles,
                onValueChange = vm::setDefaultTireMiles,
                label = { Text(stringResource(Res.string.settings_tire_miles)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = brakeMiles,
                onValueChange = vm::setDefaultBrakeMiles,
                label = { Text(stringResource(Res.string.settings_brake_miles)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
            )

            HorizontalDivider()

            SectionHeader(stringResource(Res.string.settings_section_appearance))
            SettingSwitch(
                label = stringResource(Res.string.settings_dark_theme),
                checked = darkTheme,
                onCheckedChange = vm::setUseDarkTheme,
            )

            HorizontalDivider()

            SectionHeader(stringResource(Res.string.settings_section_language))
            val langs = listOf(
                "en" to stringResource(Res.string.settings_lang_english),
                "de" to stringResource(Res.string.settings_lang_german),
            )
            Column(Modifier.selectableGroup()) {
                langs.forEach { (code, label) ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = (language == code),
                                onClick = { vm.setLanguage(code) },
                                role = Role.RadioButton,
                            )
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        RadioButton(selected = (language == code), onClick = null)
                        Text(label, modifier = Modifier.padding(start = 16.dp))
                    }
                }
            }
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
