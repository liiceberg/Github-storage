package ru.kpfu.itis.liiceberg.github_storage.presentation.screens.settings

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.Wallpapers
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.kpfu.itis.liiceberg.github_storage.R
import ru.kpfu.itis.liiceberg.github_storage.presentation.theme.GithubstorageTheme
import ru.kpfu.itis.liiceberg.github_storage.presentation.theme.JetTopAppBar
import ru.kpfu.itis.liiceberg.github_storage.util.formatDate
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date

@Preview(
    showBackground = true,
    device = Devices.PIXEL_5,
    wallpaper = Wallpapers.GREEN_DOMINATED_EXAMPLE
)
@Composable
private fun SettingsViewPreview() {
    GithubstorageTheme {
        SettingsView(
            "",
            "",
            "",
            LocalDate.now(),
            {},
            {},
            {},
            {},
            {},
            {},
            {},
            false,
            false,
            false
        )
    }
}

@Composable
fun SettingsView(viewModel: SettingsScreenViewModel = hiltViewModel()) {

    val state by viewModel.viewStates().collectAsStateWithLifecycle()

    SettingsView(
        repository = state.repository,
        folderPath = state.folderPath,
        access = state.access,
        accessActiveDate = state.accessActiveDate,
        onSave = { viewModel.obtainEvent(SettingsScreenEvent.OnSave) },
        onRepositoryFilled = { viewModel.obtainEvent(SettingsScreenEvent.OnRepositoryFilled(it)) },
        onFolderPathFilled = { viewModel.obtainEvent(SettingsScreenEvent.OnFolderFilled(it)) },
        onAccessFilled = { viewModel.obtainEvent(SettingsScreenEvent.OnAccessFilled(it)) },
        onDateSelected = { viewModel.obtainEvent(SettingsScreenEvent.OnDateSelected(it)) },
        onDatePickerCalled = { viewModel.obtainEvent(SettingsScreenEvent.OnDatePickerCalled) },
        onDatePickerDismissed = { viewModel.obtainEvent(SettingsScreenEvent.OnDatePickerDismissed) },
        repositoryNotValid = state.repositoryNotValid,
        accessNotValid = state.accessNotValid,
        showDatePicker = state.showDatePickerDialog
    )
}

@Composable
private fun SettingsView(
    repository: String,
    folderPath: String,
    access: String,
    accessActiveDate: LocalDate?,
    onSave: () -> Unit,
    onRepositoryFilled: (value: String) -> Unit,
    onFolderPathFilled: (value: String) -> Unit,
    onAccessFilled: (value: String) -> Unit,
    onDateSelected: (date: LocalDate) -> Unit,
    onDatePickerCalled: () -> Unit,
    onDatePickerDismissed: () -> Unit,
    repositoryNotValid: Boolean,
    accessNotValid: Boolean,
    showDatePicker: Boolean,
) {
    Column(Modifier.fillMaxSize()) {
        JetTopAppBar(
            text = stringResource(id = R.string.settings_page_title),
            modifier = Modifier
                .fillMaxWidth()
        )
        Box(Modifier.weight(1f)) {
            FieldsContainer(
                repository,
                onRepositoryFilled,
                repositoryNotValid,
                folderPath,
                onFolderPathFilled,
                access,
                onAccessFilled,
                accessNotValid,
                accessActiveDate,
                onDatePickerCalled
            )
        }
        JetDatePickerDialog(showDatePicker, onDateSelected, onDatePickerDismissed)
        val enableSaveButton = (repositoryNotValid || accessNotValid).not() &&
                (repository.isNotEmpty() && folderPath.isNotEmpty() && access.isNotEmpty())


        Button(
            onClick = {
                onSave.invoke()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            enabled = enableSaveButton
        ) {
            Text(text = stringResource(id = R.string.save_btn), textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun FieldsContainer(
    repository: String,
    onRepositoryFilled: (value: String) -> Unit,
    repositoryNotValid: Boolean,
    folderPath: String,
    onFolderPathFilled: (value: String) -> Unit,
    access: String,
    onAccessFilled: (value: String) -> Unit,
    accessNotValid: Boolean,
    accessActiveDate: LocalDate?,
    onDatePickerCalled: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Field(
            text = repository,
            hint = stringResource(id = R.string.repository),
            onValueChange = onRepositoryFilled,
            isError = repositoryNotValid,
            supportingText = stringResource(id = R.string.repository_error),
            keyboardType = KeyboardType.Uri
        )
        val launcher =
            rememberLauncherForActivityResult(contract = ActivityResultContracts.OpenDocumentTree()) { uri ->
                uri?.path?.let { onFolderPathFilled.invoke(it) }
            }
        Field(
            text = folderPath,
            hint = stringResource(id = R.string.folder),
            onValueChange = {},
            trailingIcon = {
                IconButton(
                    onClick = {
                        launcher.launch(null)
                    },
                    content = {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_edit_24),
                            contentDescription = null,
                            tint = Color.Unspecified
                        )
                    },
                )
            },
            readOnly = true
        )
        Field(
            text = access,
            hint = stringResource(id = R.string.access),
            onValueChange = onAccessFilled,
            isError = accessNotValid,
            supportingText = stringResource(id = R.string.access_error),
        )
        Field(
            text = accessActiveDate?.formatDate() ?: "",
            hint = stringResource(id = R.string.settings_page_access_last_date),
            onValueChange = {},
            trailingIcon = {
                IconButton(
                    onClick = {
                        onDatePickerCalled.invoke()
                    },
                    content = {
                        Icon(
                            painter = painterResource(id = R.drawable.calendar),
                            contentDescription = null,
                            tint = Color.Unspecified
                        )
                    },
                )
            },
            readOnly = true
        )
    }
}

@Composable
private fun Field(
    text: String,
    hint: String,
    onValueChange: (value: String) -> Unit,
    isError: Boolean = false,
    supportingText: String = "",
    keyboardType: KeyboardType = KeyboardType.Text,
    trailingIcon: @Composable (() -> Unit)? = null,
    readOnly: Boolean = false
) {
    OutlinedTextField(
        value = text,
        onValueChange = { onValueChange.invoke(it) },
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text(text = hint) },
        trailingIcon = trailingIcon,
        singleLine = true,
        isError = isError,
        supportingText = {
            if (isError) Text(text = supportingText)
        },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        readOnly = readOnly
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun JetDatePickerDialog(
    show: Boolean,
    onDateSelected: (date: LocalDate) -> Unit,
    onDatePickerDismissed: () -> Unit
) {
    val state = rememberDatePickerState()
    if (show) {
        DatePickerDialog(
            onDismissRequest = { onDatePickerDismissed.invoke() },
            confirmButton = {
                TextButton(onClick = {
                    state.selectedDateMillis?.let {
                        val date = Date(it).toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                        onDateSelected.invoke(date)
                    }
                }) {
                    Text(text = stringResource(id = android.R.string.ok))
                }
            }) {
            DatePicker(state = state)
        }
    }
}