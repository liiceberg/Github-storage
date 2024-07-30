package ru.kpfu.itis.liiceberg.github_storage.presentation.screens.settings

import ru.kpfu.itis.liiceberg.github_storage.presentation.base.UiState
import java.time.LocalDate

data class SettingsScreenState(
    val repository: String = "",
    val folderPath: String = "",
    val access: String = "",
    val accessActiveDate: LocalDate? = null,
    val repositoryNotValid: Boolean = false,
    val folderPathNotValid: Boolean = false,
    val accessNotValid: Boolean = false,
) : UiState