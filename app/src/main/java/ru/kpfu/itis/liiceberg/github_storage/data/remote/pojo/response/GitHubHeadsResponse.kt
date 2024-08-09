package ru.kpfu.itis.liiceberg.github_storage.data.remote.pojo.response

import com.google.gson.annotations.SerializedName

data class GitHubHeadsResponse(
    val ref: String,
    val url: String,
    @SerializedName("object") val lastObject: LastGitHubObject
)

data class LastGitHubObject(
    val sha: String,
    val type: String,
    val url: String
)