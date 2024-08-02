package ru.kpfu.itis.liiceberg.github_storage.presentation.screens.settings

import ru.kpfu.itis.liiceberg.github_storage.presentation.base.UiAction

sealed interface SettingsScreenAction : UiAction {
    data object ShowSuccessSaveToast: SettingsScreenAction
}