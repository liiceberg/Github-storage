package ru.kpfu.itis.liiceberg.github_storage.data.remote.pojo.response

import com.google.gson.annotations.SerializedName

data class GitHubFile(
    val sha: String,
    @SerializedName("node_id") val nodeId: String,
    val size: Int,
    val url: String,
    val content: String,
    val encoding: String
)