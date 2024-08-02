package ru.kpfu.itis.liiceberg.github_storage.domain.repository

interface SystemFilesRepository {
    suspend fun getAbsoluteRootFilePath() : String
    suspend fun getRootFolderPath(): String
    suspend fun saveRootFolderPath(path: String)
    suspend fun saveFile(path: String, content: String?, isFolder: Boolean)

}