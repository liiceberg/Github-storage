package ru.kpfu.itis.liiceberg.github_storage.presentation.screens.settings

import ru.kpfu.itis.liiceberg.github_storage.presentation.base.UiEvent
import java.time.LocalDate

sealed interface SettingsScreenEvent : UiEvent {
    data class OnRepositoryFilled(val repository: String) : SettingsScreenEvent
    data class OnFolderFilled(val uri: String) : SettingsScreenEvent
    data class OnAccessFilled(val access: String) : SettingsScreenEvent
    data class OnDateSelected(val date: LocalDate) : SettingsScreenEvent
    data object OnDatePickerDismissed : SettingsScreenEvent
    data object OnDatePickerCalled : SettingsScreenEvent
    data object OnSave : SettingsScreenEvent
}