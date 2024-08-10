package ru.kpfu.itis.liiceberg.github_storage.domain.repository

import java.io.File

interface SystemFilesRepository {
    suspend fun getRootFileAbsolutePath() : String
    suspend fun getRootFolderRelativePath(): String
    suspend fun saveRootFolderPath(path: String)
    suspend fun saveFile(path: String, content: String?, isFolder: Boolean)
    suspend fun saveAll(newFiles: Map<String, String>)
    suspend fun getRoot() : File
    suspend fun getFolderRelativePath(path: String) : String
    suspend fun getSavedFiles(): Set<String>?
}