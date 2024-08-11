package ru.kpfu.itis.liiceberg.github_storage.domain.model

import ru.kpfu.itis.liiceberg.github_storage.data.model.GitHubAction
import java.time.LocalDate


data class GitHubActionItem(
    val name: GitHubAction,
    val date: LocalDate,
    val statistic: ActionStatistic
)

data class ActionStatistic(
    val plusStr: Int,
    val minusStr: Int
)



