package ru.kpfu.itis.liiceberg.github_storage.data.local.mapper

import ru.kpfu.itis.liiceberg.github_storage.data.local.entity.StatisticEntity
import ru.kpfu.itis.liiceberg.github_storage.data.remote.model.GitHubAction
import ru.kpfu.itis.liiceberg.github_storage.domain.model.ActionStatistic
import ru.kpfu.itis.liiceberg.github_storage.domain.model.GitHubActionItem
import java.time.Instant
import java.time.ZoneId
import javax.inject.Inject

class StatisticMapper @Inject constructor() {

    fun mapStatisticEntityToGitHubActionItem(list: List<StatisticEntity>?): List<GitHubActionItem> {
        return list?.map { entity ->
            GitHubActionItem(
                name = GitHubAction.valueOf(entity.action),
                date = Instant.ofEpochMilli(entity.date)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate(),
                statistic = ActionStatistic(entity.added, entity.deleted)
            )
        }?.toList() ?: emptyList()
    }
}