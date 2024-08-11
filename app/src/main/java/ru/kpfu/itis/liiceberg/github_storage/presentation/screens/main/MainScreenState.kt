package ru.kpfu.itis.liiceberg.github_storage.presentation.screens.main

import ru.kpfu.itis.liiceberg.github_storage.domain.model.GitHubActionItem
import ru.kpfu.itis.liiceberg.github_storage.presentation.base.UiState
import java.time.LocalDate

data class MainScreenState(
    val repository: String = "",
    val folderPath: String = "",
    val accessDate: LocalDate? = null,
    val history: List<GitHubActionItem> = emptyList(),
    val pullLoading: Boolean = false,
    val pushLoading: Boolean = false,
) : UiState