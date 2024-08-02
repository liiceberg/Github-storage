package ru.kpfu.itis.liiceberg.github_storage.data.model

import com.google.gson.annotations.SerializedName

data class GitHubFile(
    val sha: String,
    @SerializedName("node_id") val nodeId: String,
    val size: Int,
    val url: String,
    val content: String,
    val encoding: String
)