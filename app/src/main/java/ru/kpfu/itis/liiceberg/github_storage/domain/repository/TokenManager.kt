package ru.kpfu.itis.liiceberg.github_storage.domain.repository

import ru.kpfu.itis.liiceberg.github_storage.data.model.AccessToken

interface TokenManager {
    suspend fun getAccessToken() : AccessToken?
    suspend fun saveAccessToken(token: AccessToken)
}