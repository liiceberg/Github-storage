package ru.kpfu.itis.liiceberg.github_storage.data.remote

import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

class ApiHandler @Inject constructor(){

    suspend fun <T> makeSafeApiCall(apiCall: suspend () -> Response<T>): ResultWrapper<T> {
        return try {
            val response = apiCall()
            if (response.isSuccessful) {
                response.body()?.let {
                    ResultWrapper.Success(it)
                } ?: ResultWrapper.GenericError(response.code(), "Response body is null")
            } else {
                val code = response.code()
                val errorResponse = response.errorBody()?.string()
                ResultWrapper.GenericError(code, errorResponse)
            }
        } catch (ex: Exception) {
            when (ex) {
                is IOException -> ResultWrapper.NetworkError
                is HttpException -> {
                    val code = ex.code()
                    val errorResponse = ex.response()?.errorBody()?.string()
                    ResultWrapper.GenericError(code, errorResponse)
                }
                else -> ResultWrapper.GenericError(null, ex.localizedMessage)
            }
        }
    }
}