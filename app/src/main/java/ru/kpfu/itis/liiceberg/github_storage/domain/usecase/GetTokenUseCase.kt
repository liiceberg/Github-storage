package ru.kpfu.itis.liiceberg.github_storage.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import ru.kpfu.itis.liiceberg.github_storage.data.remote.pojo.AccessToken
import ru.kpfu.itis.liiceberg.github_storage.domain.repository.TokenManager
import javax.inject.Inject

class GetTokenUseCase @Inject constructor(
    private val dispatcher: CoroutineDispatcher,
    private val manager: TokenManager
) {
    suspend operator fun invoke() : AccessToken? {
        return withContext(dispatcher) {
            manager.getAccessToken()
        }
    }
}