package ru.kpfu.itis.liiceberg.github_storage.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class CheckFolderPathUseCase @Inject constructor(
    private val dispatcher: CoroutineDispatcher
) {
    fun invoke(path: String): Boolean {
        return path.matches(".*/.*".toRegex())
    }
}