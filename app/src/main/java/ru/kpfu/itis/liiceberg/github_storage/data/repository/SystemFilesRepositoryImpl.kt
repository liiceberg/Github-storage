package ru.kpfu.itis.liiceberg.github_storage.data.repository

import android.content.Context
import androidx.datastore.DataStore
import androidx.datastore.preferences.Preferences
import androidx.datastore.preferences.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import ru.kpfu.itis.liiceberg.github_storage.domain.repository.SystemFilesRepository
import ru.kpfu.itis.liiceberg.github_storage.util.PrefsKeys
import java.io.File
import javax.inject.Inject

class SystemFilesRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    @ApplicationContext private val context: Context
) : SystemFilesRepository {

    override suspend fun getAbsoluteRootFilePath() : String {
        val root = context.externalCacheDir?.path ?: return ""
        return "$root${File.separator}${getRootFolderPath()}"
    }

    override suspend fun getRootFolderPath(): String {
        return dataStore.data.map { it[PrefsKeys.FOLDER_KEY] }.first() ?: ""
    }

    override suspend fun saveRootFolderPath(path: String) {
        dataStore.edit {
            it[PrefsKeys.FOLDER_KEY] = path
        }
    }

    override suspend fun saveFile(path: String, content: String?, isFolder: Boolean) {
        val root = getAbsoluteRootFilePath()
        val rootFile = File(root)
        if (rootFile.exists().not()) {
            rootFile.mkdir()
        }
        val absolutePath = "$root${File.separator}$path"
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
            file.writeText(content ?: "")
        }
    }


}