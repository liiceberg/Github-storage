package ru.kpfu.itis.liiceberg.github_storage.data.repository

import androidx.datastore.DataStore
import androidx.datastore.preferences.Preferences
import androidx.datastore.preferences.edit
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import ru.kpfu.itis.liiceberg.github_storage.domain.repository.GitHubRepository
import ru.kpfu.itis.liiceberg.github_storage.util.PrefsKeys
import javax.inject.Inject

class GitHubRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : GitHubRepository {

    override suspend fun getRepositoryUrl(): String {
        return dataStore.data.map { it[PrefsKeys.REPOSITORY_KEY] }.first() ?: ""
    }

    override suspend fun saveRepositoryUrl(repository: String) {
        dataStore.edit {
            it[PrefsKeys.REPOSITORY_KEY] = repository
        }
    }
}