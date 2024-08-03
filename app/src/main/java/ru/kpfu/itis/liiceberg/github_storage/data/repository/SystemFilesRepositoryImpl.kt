package ru.kpfu.itis.liiceberg.github_storage.data.repository

import android.content.Context
import androidx.datastore.DataStore
import androidx.datastore.preferences.Preferences
import androidx.datastore.preferences.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import ru.kpfu.itis.liiceberg.github_storage.data.local.dao.DirectoryRootDao
import ru.kpfu.itis.liiceberg.github_storage.data.local.entity.DirectoryRootEntity
import ru.kpfu.itis.liiceberg.github_storage.domain.repository.SystemFilesRepository
import ru.kpfu.itis.liiceberg.github_storage.util.PrefsKeys
import java.io.File
import javax.inject.Inject

class SystemFilesRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    @ApplicationContext private val context: Context,
    private val directoryDao: DirectoryRootDao
) : SystemFilesRepository {

    override suspend fun getRootFileAbsolutePath() : String {
        val root = context.externalCacheDir?.path ?: return ""
        val path = getRootFolderPath()
        val absolutePath = "$root${File.separator}$path"
        if (path.isNotEmpty()) {
            checkDoesRootFileExist(absolutePath)
        }
        return absolutePath
    }

    override suspend fun getRootFolderPath(): String {
        return dataStore.data.map { it[PrefsKeys.FOLDER_KEY] }.first() ?: ""
    }

    override suspend fun saveRootFolderPath(path: String) {
        dataStore.edit {
            it[PrefsKeys.FOLDER_KEY] = path
        }
    }

    override suspend fun saveAll(newFiles: Set<String>) {
        val root = getRootFileAbsolutePath()

        val last = directoryDao.get(root)?.files

        last?.forEach { file ->
            if (newFiles.contains(file).not()) {
                val deleteFile = "${getRootFileAbsolutePath()}${File.separator}$file"
                File(deleteFile).deleteRecursively()
            }
        }

        directoryDao.save(DirectoryRootEntity(location = root, files = newFiles))
    }

    override suspend fun saveFile(path: String, content: String?, isFolder: Boolean) {
        val absolutePath = "${getRootFileAbsolutePath()}${File.separator}$path"
        val file = File(absolutePath)
        if (isFolder) {
            saveFolder(file)
        } else {
            saveFile(file, content)
        }
    }

    private fun saveFolder(file: File) {
        if (file.exists().not()) {
            file.mkdirs()
        }
    }

    private fun saveFile(file: File, content: String?) {
        if (file.exists()) {

        } else {
            file.createNewFile()
        }
        file.writeText(content ?: "")
    }

    private fun checkDoesRootFileExist(path: String) {
        val rootFile = File(path)
        if (rootFile.exists().not()) {
            rootFile.mkdir()
        }
    }
}