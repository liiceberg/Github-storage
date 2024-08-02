package ru.kpfu.itis.liiceberg.github_storage.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import ru.kpfu.itis.liiceberg.github_storage.domain.repository.SystemFilesRepository
import javax.inject.Inject

class GetFolderUseCase @Inject constructor(
    private val dispatcher: CoroutineDispatcher,
    private val repository: SystemFilesRepository
) {
    suspend operator fun invoke(absolute: Boolean = false): String {
        return withContext(dispatcher) {
            if (absolute) {
                repository.getAbsoluteRootFilePath()
            } else {
                repository.getRootFolderPath()
            }
        }
    }
}