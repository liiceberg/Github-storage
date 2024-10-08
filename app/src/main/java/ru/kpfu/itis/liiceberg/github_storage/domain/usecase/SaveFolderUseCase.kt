package ru.kpfu.itis.liiceberg.github_storage.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import ru.kpfu.itis.liiceberg.github_storage.domain.repository.SystemFilesRepository
import javax.inject.Inject

class SaveFolderUseCase @Inject constructor(
    private val dispatcher: CoroutineDispatcher,
    private val repository: SystemFilesRepository,
) {
    suspend operator fun invoke(path: String) {
        withContext(dispatcher) {
            repository.saveRootFolderPath(path)
        }
    }
}