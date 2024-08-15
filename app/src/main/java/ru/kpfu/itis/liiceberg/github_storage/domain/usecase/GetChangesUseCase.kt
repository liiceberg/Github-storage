package ru.kpfu.itis.liiceberg.github_storage.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import ru.kpfu.itis.liiceberg.github_storage.data.remote.model.GitStatus
import ru.kpfu.itis.liiceberg.github_storage.domain.repository.GitHubRepository
import javax.inject.Inject

class GetChangesUseCase @Inject constructor(
    private val repository: GitHubRepository,
    private val dispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke() : GitStatus {
        return withContext(dispatcher) {
            repository.getFilesStatus()
        }
    }
}