package ru.kpfu.itis.liiceberg.github_storage.data.repository

import androidx.datastore.DataStore
import androidx.datastore.preferences.Preferences
import androidx.datastore.preferences.edit
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import ru.kpfu.itis.liiceberg.github_storage.data.remote.pojo.AccessToken
import ru.kpfu.itis.liiceberg.github_storage.domain.repository.TokenManager
import ru.kpfu.itis.liiceberg.github_storage.util.PrefsKeys
import java.time.LocalDate
import javax.inject.Inject

class TokenManagerImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : TokenManager {

    override suspend fun getAccessToken(): AccessToken? {
        val date = dataStore.data.map { it[PrefsKeys.ACCESS_TOKEN_ACTIVE_PERIOD_KEY] }.first()
        if (date == null) return null
        val token = dataStore.data.map { it[PrefsKeys.ACCESS_TOKEN_KEY] }.first() ?: ""
        return AccessToken(token, LocalDate.ofEpochDay(date))
    }

    override suspend fun saveAccessToken(token: AccessToken) {
        dataStore.edit {
            it[PrefsKeys.ACCESS_TOKEN_KEY] = token.value
            it[PrefsKeys.ACCESS_TOKEN_ACTIVE_PERIOD_KEY] = token.activePeriod.toEpochDay()
        }
    }
}