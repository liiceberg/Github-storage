package ru.kpfu.itis.liiceberg.github_storage.presentation.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import ru.kpfu.itis.liiceberg.github_storage.R
import ru.kpfu.itis.liiceberg.github_storage.presentation.navigation.screens.mainScreenRoute
import ru.kpfu.itis.liiceberg.github_storage.presentation.navigation.screens.settingsScreenRoute

data class BottomNavItem(
    @StringRes val label: Int,
    @DrawableRes val icon: Int,
    val route: String,
    val rounded: Boolean
)


val BottomNavItems = listOf(
    BottomNavItem(
        label = R.string.main_label,
        icon = R.drawable.icon1,
        route = mainScreenRoute,
        rounded = false
    ),
    BottomNavItem(
        label = R.string.settings_label,
        icon = R.drawable.icon2,
        route = settingsScreenRoute,
        rounded = true
    ),
)