package ru.kpfu.itis.liiceberg.github_storage.data.remote

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url
import ru.kpfu.itis.liiceberg.github_storage.data.remote.pojo.request.CreateCommitRequest
import ru.kpfu.itis.liiceberg.github_storage.data.remote.pojo.response.CreateCommitResponse
import ru.kpfu.itis.liiceberg.github_storage.data.remote.pojo.request.CreateTreeRequest
import ru.kpfu.itis.liiceberg.github_storage.data.remote.pojo.response.GitHubCommit
import ru.kpfu.itis.liiceberg.github_storage.data.remote.pojo.response.GitHubFile
import ru.kpfu.itis.liiceberg.github_storage.data.remote.pojo.response.GitHubHeadsResponse
import ru.kpfu.itis.liiceberg.github_storage.data.remote.pojo.response.GitHubTree
import ru.kpfu.itis.liiceberg.github_storage.data.remote.pojo.request.UpdateBranchRequest

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

    @GET("repos/{owner}/{repo}/git/refs/heads/{branch}")
    suspend fun getHeads(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("branch") branch: String = "master",
    ) : GitHubHeadsResponse

    @GET("repos/{owner}/{repo}/git/commits/{sha}")
    suspend fun getCommit(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("sha") sha: String,
    ) : GitHubCommit

    @POST("repos/{owner}/{repo}/git/trees")
    suspend fun createTree(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Body tree: CreateTreeRequest
    ) : GitHubTree

    @POST("repos/{owner}/{repo}/git/commits")
    suspend fun createCommit(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Body commit: CreateCommitRequest
    ) : CreateCommitResponse

    @POST("repos/{owner}/{repo}/git/refs/heads/{branch}")
    suspend fun updateRefs(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("branch") branch: String = "master",
        @Body sha: UpdateBranchRequest
    )

}