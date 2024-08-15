package ru.kpfu.itis.liiceberg.github_storage.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url
import ru.kpfu.itis.liiceberg.github_storage.data.remote.pojo.request.CreateCommitRequest
import ru.kpfu.itis.liiceberg.github_storage.data.remote.pojo.request.CreateTreeRequest
import ru.kpfu.itis.liiceberg.github_storage.data.remote.pojo.request.UpdateBranchRequest
import ru.kpfu.itis.liiceberg.github_storage.data.remote.pojo.response.CreateCommitResponse
import ru.kpfu.itis.liiceberg.github_storage.data.remote.pojo.response.GitHubCommit
import ru.kpfu.itis.liiceberg.github_storage.data.remote.pojo.response.GitHubFile
import ru.kpfu.itis.liiceberg.github_storage.data.remote.pojo.response.GitHubHeadsResponse
import ru.kpfu.itis.liiceberg.github_storage.data.remote.pojo.response.GitHubTree

interface GitHubApi {

    @GET("repos/{owner}/{repo}/git/trees/{branch}")
    suspend fun getAllFiles(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("branch") branch: String = "master",
        @Query("recursive")recursive: Int = 1,
    ) : Response<GitHubTree>

    @GET
    suspend fun getFile(@Url url: String) : Response<GitHubFile>

    @GET("repos/{owner}/{repo}/git/refs/heads/{branch}")
    suspend fun getHeads(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("branch") branch: String = "master",
    ) : Response<GitHubHeadsResponse>

    @GET("repos/{owner}/{repo}/git/commits/{sha}")
    suspend fun getCommit(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("sha") sha: String,
    ) : Response<GitHubCommit>

    @POST("repos/{owner}/{repo}/git/trees")
    suspend fun createTree(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Body tree: CreateTreeRequest
    ) : Response<GitHubTree>

    @POST("repos/{owner}/{repo}/git/commits")
    suspend fun createCommit(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Body commit: CreateCommitRequest
    ) : Response<CreateCommitResponse>

    @POST("repos/{owner}/{repo}/git/refs/heads/{branch}")
    suspend fun updateRefs(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("branch") branch: String = "master",
        @Body sha: UpdateBranchRequest
    ) : Response<Unit>

}