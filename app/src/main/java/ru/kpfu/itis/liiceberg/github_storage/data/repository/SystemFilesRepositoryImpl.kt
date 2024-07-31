package ru.kpfu.itis.liiceberg.github_storage.data.repository

import androidx.datastore.DataStore
import androidx.datastore.preferences.Preferences
import androidx.datastore.preferences.edit
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import ru.kpfu.itis.liiceberg.github_storage.domain.repository.SystemFilesRepository
import ru.kpfu.itis.liiceberg.github_storage.util.PrefsKeys
import javax.inject.Inject

class SystemFilesRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : SystemFilesRepository {

    override suspend fun getFolderPath(): String {
        return dataStore.data.map { it[PrefsKeys.FOLDER_KEY] }.first() ?: ""
    }

    override suspend fun saveFolderPath(path: String) {
        dataStore.edit {
            it[PrefsKeys.FOLDER_KEY] = path
        }
    }
}