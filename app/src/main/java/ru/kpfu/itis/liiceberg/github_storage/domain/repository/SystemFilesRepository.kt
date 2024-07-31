package ru.kpfu.itis.liiceberg.github_storage.domain.repository

interface SystemFilesRepository {
    suspend fun getFolderPath() : String
    suspend fun saveFolderPath(path: String)
}