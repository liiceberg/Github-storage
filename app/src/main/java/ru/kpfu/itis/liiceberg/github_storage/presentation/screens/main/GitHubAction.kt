package ru.kpfu.itis.liiceberg.github_storage.presentation.screens.main

import java.time.LocalDate


data class GitHubActionItem(
    val name: GitHubAction,
    val date: LocalDate,
    val statistic: ActionStatistic
)

enum class GitHubAction {
    PULL, PUSH
}

data class ActionStatistic(
    val plusStr: Int,
    val minusStr: Int
)



