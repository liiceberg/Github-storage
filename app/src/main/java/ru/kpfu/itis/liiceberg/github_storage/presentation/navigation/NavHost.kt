package ru.kpfu.itis.liiceberg.github_storage.presentation.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import ru.kpfu.itis.liiceberg.github_storage.presentation.navigation.screens.mainScreen
import ru.kpfu.itis.liiceberg.github_storage.presentation.navigation.screens.mainScreenRoute
import ru.kpfu.itis.liiceberg.github_storage.presentation.navigation.screens.settingsScreen

@Composable
fun NavHostContainer(
    navController: NavHostController,
    padding: PaddingValues
) {

    NavHost(
        navController = navController,
        startDestination = mainScreenRoute,
        modifier = Modifier.padding(paddingValues = padding),

        builder = {
            mainScreen()
            settingsScreen()
        })

}