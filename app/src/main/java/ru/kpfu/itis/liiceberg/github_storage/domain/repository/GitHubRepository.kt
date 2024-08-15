package ru.kpfu.itis.liiceberg.github_storage.domain.repository

import ru.kpfu.itis.liiceberg.github_storage.domain.model.GitHubActionItem
import ru.kpfu.itis.liiceberg.github_storage.data.remote.model.GitStatus

interface GitHubRepository {
    suspend fun getRepositoryUrl() : String
    suspend fun saveRepositoryUrl(repository: String)
    suspend fun pull()
    suspend fun commitAndPush()
    suspend fun getHistory() : List<GitHubActionItem>
    suspend fun getFilesStatus() : GitStatus
}