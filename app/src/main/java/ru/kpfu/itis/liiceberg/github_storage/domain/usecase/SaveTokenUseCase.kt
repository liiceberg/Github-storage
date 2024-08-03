package ru.kpfu.itis.liiceberg.github_storage.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import ru.kpfu.itis.liiceberg.github_storage.data.remote.model.AccessToken
import ru.kpfu.itis.liiceberg.github_storage.domain.repository.TokenManager
import javax.inject.Inject

class SaveTokenUseCase @Inject constructor(
    private val dispatcher: CoroutineDispatcher,
    private val manager: TokenManager,
) {
    suspend operator fun invoke(token: AccessToken) {
        withContext(dispatcher) {
            manager.saveAccessToken(token)
        }
    }
}