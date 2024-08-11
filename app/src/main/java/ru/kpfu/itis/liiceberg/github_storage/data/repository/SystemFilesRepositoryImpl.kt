package ru.kpfu.itis.liiceberg.github_storage.data.repository

import android.os.Environment
import androidx.datastore.DataStore
import androidx.datastore.preferences.Preferences
import androidx.datastore.preferences.edit
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import ru.kpfu.itis.liiceberg.github_storage.data.local.dao.FileDao
import ru.kpfu.itis.liiceberg.github_storage.data.local.entity.FileEntity
import ru.kpfu.itis.liiceberg.github_storage.domain.repository.SystemFilesRepository
import ru.kpfu.itis.liiceberg.github_storage.util.PrefsKeys
import java.io.File
import javax.inject.Inject


class SystemFilesRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val fileDao: FileDao,
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

    override suspend fun getSavedFilesNames(): Set<String>? {
        val root = getRootFileAbsolutePath()
        return fileDao.getAll(root)?.map { it.path }?.toSet()
    }

    override suspend fun getSavedFilesWithContent(): Map<String, String> {
        val root = getRootFileAbsolutePath()
        val map = mutableMapOf<String, String>()
        fileDao.getAll(root)?.forEach { file -> map[file.path] = file.content }
        return map
    }

    override suspend fun deleteFile(directory: String, path: String) {
        fileDao.delete(path = path, directory = directory)
    }

    override suspend fun saveFile(directory: String, path: String, content: String) {
        fileDao.save(FileEntity(directory = directory, path = path, content = content))
    }

    override suspend fun saveAll(newFiles: Map<String, String>) {
        val root = getRootFileAbsolutePath()

        val last = getSavedFilesNames()

        last?.forEach { file ->
            if (newFiles.keys.contains(file).not()) {
                val deleteFile = "${getRootFileAbsolutePath()}${File.separator}$file"
                File(deleteFile).deleteRecursively()
                fileDao.delete(file, root)
            }
        }

        newFiles.forEach { file ->
            fileDao.save(FileEntity(directory = root, path = file.key, content = file.value))
        }
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
        if (file.exists().not()) {
            file.createNewFile()
        }
        file.writeText(content ?: "")
    }

}