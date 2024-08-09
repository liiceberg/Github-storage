package ru.kpfu.itis.liiceberg.github_storage.domain.repository

interface GitHubRepository {
    suspend fun getRepositoryUrl() : String
    suspend fun saveRepositoryUrl(repository: String)
    suspend fun pull()
    suspend fun commitAndPush()
}