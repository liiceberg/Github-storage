package ru.kpfu.itis.liiceberg.github_storage.data.remote


sealed class ResultWrapper<out T> {
    data class Success<out T>(val value: T): ResultWrapper<T>()
    data class GenericError(val code: Int? = null, val error: String? = null): ResultWrapper<Nothing>()
    data object NetworkError: ResultWrapper<Nothing>()
}