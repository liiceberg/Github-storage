package ru.kpfu.itis.liiceberg.github_storage.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import ru.kpfu.itis.liiceberg.github_storage.domain.repository.GitHubRepository
import javax.inject.Inject

class GetRepositoryUseCase @Inject constructor(
    private val dispatcher: CoroutineDispatcher,
    private val repository: GitHubRepository
) {
    suspend operator fun invoke() : String {
        return withContext(dispatcher) {
            repository.getRepositoryUrl()
        }
    }
}