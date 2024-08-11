package ru.kpfu.itis.liiceberg.github_storage.domain.repository

import ru.kpfu.itis.liiceberg.github_storage.domain.model.GitHubActionItem

interface GitHubRepository {
    suspend fun getRepositoryUrl() : String
    suspend fun saveRepositoryUrl(repository: String)
    suspend fun pull()
    suspend fun commitAndPush()
    suspend fun getHistory() : List<GitHubActionItem>
}