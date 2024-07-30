package ru.kpfu.itis.liiceberg.github_storage.presentation.screens.settings

import ru.kpfu.itis.liiceberg.github_storage.presentation.base.UiEvent

sealed interface SettingsScreenEvent : UiEvent {
    data class OnRepositoryFilled(val repository: String) : SettingsScreenEvent
    data class OnFolderFilled(val path: String) : SettingsScreenEvent
    data class OnAccessFilled(val access: String) : SettingsScreenEvent
    data object OnSave : SettingsScreenEvent
}