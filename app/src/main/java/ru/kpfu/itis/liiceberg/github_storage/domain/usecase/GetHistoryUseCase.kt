package ru.kpfu.itis.liiceberg.github_storage.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import ru.kpfu.itis.liiceberg.github_storage.domain.repository.GitHubRepository
import ru.kpfu.itis.liiceberg.github_storage.domain.model.GitHubActionItem
import javax.inject.Inject

class GetHistoryUseCase @Inject constructor(
    private val dispatcher: CoroutineDispatcher,
    private val repository: GitHubRepository
) {
    suspend operator fun invoke() : List<GitHubActionItem>{
        return withContext(dispatcher) {
            repository.getHistory()
        }
    }
}