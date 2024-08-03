package ru.kpfu.itis.liiceberg.github_storage.data.remote.interceptor

import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import ru.kpfu.itis.liiceberg.github_storage.domain.repository.TokenManager
import javax.inject.Inject

class AccessTokenInterceptor @Inject constructor(
    private val tokenManager: TokenManager
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = runBlocking {
            tokenManager.getAccessToken()
        }
        val request = chain.request().newBuilder()
        token?.let {
            request.addHeader(HEADER_AUTHORIZATION, "$TOKEN_TYPE ${token.value}")
            request.addHeader("X-GitHub-Api-Version", "2022-11-28")
            request.addHeader("Accept", "application/vnd.github+json")
        }
        return chain.proceed(request.build())
    }

    companion object {
        const val HEADER_AUTHORIZATION = "Authorization"
        const val TOKEN_TYPE = "Bearer"
    }
}