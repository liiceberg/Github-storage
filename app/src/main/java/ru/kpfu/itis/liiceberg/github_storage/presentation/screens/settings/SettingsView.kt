package ru.kpfu.itis.liiceberg.github_storage.presentation.screens.settings

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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

@Preview(
    showBackground = true,
    device = Devices.PIXEL_5,
    wallpaper = Wallpapers.GREEN_DOMINATED_EXAMPLE
)
@Composable
private fun SettingsViewPreview() {
    GithubstorageTheme {
        SettingsView("", "", "", LocalDate.now(), {}, {}, {}, {}, false, false, false)
    }
}

@Composable
fun SettingsView(viewModel: SettingsScreenViewModel = hiltViewModel()) {

    val state by viewModel.viewStates().collectAsStateWithLifecycle()

    SettingsView(
        repository = state.repository,
        folderPath = state.folderPath,
        access = state.access,
        accessActiveDate = state.accessActiveDate ?: LocalDate.now(),
        onSave = { viewModel.obtainEvent(SettingsScreenEvent.OnSave) },
        onRepositoryFilled = { viewModel.obtainEvent(SettingsScreenEvent.OnRepositoryFilled(it)) },
        onFolderPathFilled = { viewModel.obtainEvent(SettingsScreenEvent.OnFolderFilled(it)) },
        onAccessFilled = { viewModel.obtainEvent(SettingsScreenEvent.OnAccessFilled(it)) },
        repositoryNotValid = state.repositoryNotValid,
        folderPathNotValid = state.folderPathNotValid,
        accessNotValid = state.accessNotValid)
}

@Composable
private fun SettingsView(
    repository: String,
    folderPath: String,
    access: String,
    accessActiveDate: LocalDate,
    onSave: () -> Unit,
    onRepositoryFilled: (value: String) -> Unit,
    onFolderPathFilled: (value: String) -> Unit,
    onAccessFilled: (value: String) -> Unit,
    repositoryNotValid: Boolean,
    folderPathNotValid: Boolean,
    accessNotValid: Boolean,
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
                folderPathNotValid,
                access,
                onAccessFilled,
                accessNotValid,
                accessActiveDate
            )
        }
        Button(
            onClick = { onSave.invoke() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
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
    folderPathNotValid: Boolean,
    access: String,
    onAccessFilled: (value: String) -> Unit,
    accessNotValid: Boolean,
    accessActiveDate: LocalDate,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Field(
            text = repository,
            hint = stringResource(id = R.string.repository),
            onValueChange = onRepositoryFilled,
            isError = repositoryNotValid,
            supportingText = stringResource(id = R.string.repository_error)
        )
        Field(
            text = folderPath,
            hint = stringResource(id = R.string.folder),
            onValueChange = onFolderPathFilled,
            isError = folderPathNotValid,
            supportingText = stringResource(id = R.string.folder_path_error)
        )
        Field(
            text = access,
            hint = stringResource(id = R.string.access),
            onValueChange = onAccessFilled,
            isError = accessNotValid,
            supportingText = stringResource(id = R.string.access_error)
        )
        Field(
            text = accessActiveDate.formatDate(),
            hint = stringResource(id = R.string.settings_page_access_last_date),
            R.drawable.calendar,
            onValueChange = {},
            isError = false,
        )
    }
}

@Composable
private fun Field(
    text: String,
    hint: String,
    @DrawableRes trailingIcon: Int? = null,
    onValueChange: (value: String) -> Unit,
    isError: Boolean,
    supportingText: String = ""
) {
    OutlinedTextField(
        value = text,
        onValueChange = { onValueChange.invoke(it) },
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text(text = hint) },
        trailingIcon = {
            trailingIcon?.let { icon ->
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = null,
                    tint = Color.Unspecified
                )
            }
        },
        singleLine = true,
        isError = isError,
        supportingText = {
            if (isError) Text(text = supportingText)
        }
    )
}