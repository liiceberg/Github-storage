package ru.kpfu.itis.liiceberg.github_storage.presentation.navigation.screens

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import ru.kpfu.itis.liiceberg.github_storage.presentation.screens.main.MainView

const val mainScreenRoute = "main"

fun NavGraphBuilder.mainScreen() {
    composable(mainScreenRoute) {
        MainView()
    }
}
