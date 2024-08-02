package ru.kpfu.itis.liiceberg.github_storage.data.remote

import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url
import ru.kpfu.itis.liiceberg.github_storage.data.model.GitHubFile
import ru.kpfu.itis.liiceberg.github_storage.data.model.GitHubTree

interface GitHubApi {

    @GET("repos/{owner}/{repo}/git/trees/{branch}")
    suspend fun getAllFiles(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("branch") branch: String = "master",
        @Query("recursive")recursive: Int = 1,
    ) : GitHubTree

    @GET
    suspend fun getFile(@Url url: String) : GitHubFile

    @PUT("repos/{owner}/{repo}/contents/{path}")
    suspend fun updateFile(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("path") path: String,
    )

    @DELETE("repos/{owner}/{repo}/contents/{path}")
    suspend fun deleteFile(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("path") path: String,
    )
}