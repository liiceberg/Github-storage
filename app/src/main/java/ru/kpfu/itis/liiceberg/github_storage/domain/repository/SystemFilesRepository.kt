package ru.kpfu.itis.liiceberg.github_storage.domain.repository

interface SystemFilesRepository {
    suspend fun getRootFileAbsolutePath() : String
    suspend fun getRootFolderRelativePath(): String
    suspend fun saveRootFolderPath(path: String)
    suspend fun saveFile(path: String, content: String?, isFolder: Boolean)
    suspend fun saveAll(newFiles: Map<String, String>)
    suspend fun getFolderRelativePath(path: String) : String
    suspend fun getSavedFilesNames(): Set<String>?
    suspend fun getSavedFilesWithContent(): Map<String, String>
    suspend fun deleteFile(directory: String, path: String)
    suspend fun saveFiles(directory: String, files: Map<String, String>)
    suspend fun getExistingFilesWithContent() : Map<String, String>
}