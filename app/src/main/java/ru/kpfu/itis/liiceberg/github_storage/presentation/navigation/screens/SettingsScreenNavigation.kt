package ru.kpfu.itis.liiceberg.github_storage.presentation.navigation.screens

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import ru.kpfu.itis.liiceberg.github_storage.presentation.screens.settings.SettingsView

const val settingsScreenRoute = "settings"

fun NavGraphBuilder.settingsScreen() {
    composable(settingsScreenRoute) {
        SettingsView()
    }
}
