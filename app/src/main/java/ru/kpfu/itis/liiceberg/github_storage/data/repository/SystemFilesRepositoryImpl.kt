package ru.kpfu.itis.liiceberg.github_storage.data.repository

import android.os.Environment
import androidx.datastore.DataStore
import androidx.datastore.preferences.Preferences
import androidx.datastore.preferences.edit
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
    private val directoryDao: DirectoryRootDao,
) : SystemFilesRepository {

    override suspend fun getRoot(): File = File(getRootFileAbsolutePath())

    override suspend fun getRootFileAbsolutePath(): String {
        val root = Environment.getExternalStorageDirectory().absolutePath
        return "$root${File.separator}${getRootFolderRelativePath()}"
    }

    override suspend fun getRootFolderRelativePath(): String {
        return dataStore.data.map { it[PrefsKeys.FOLDER_KEY] }.first() ?: ""
    }

    override suspend fun saveRootFolderPath(path: String) {
        dataStore.edit {
            it[PrefsKeys.FOLDER_KEY] = path
        }
    }

    override suspend fun getFolderRelativePath(path: String): String {
        val rootPath = getRootFileAbsolutePath()
        return path.substring(rootPath.length + 1)
    }

    override suspend fun saveAll(newFiles: Set<String>) {
        val root = getRootFileAbsolutePath()

        val last = lastCommitFiles()

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
            file.createNewFile().toString()
        }
        file.writeText(content ?: "")
    }

    override suspend fun lastCommitFiles(): Set<String>? {
        val root = getRootFileAbsolutePath()
        return directoryDao.get(root)?.files
    }
}