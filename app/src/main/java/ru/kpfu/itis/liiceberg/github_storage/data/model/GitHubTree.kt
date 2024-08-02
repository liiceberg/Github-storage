package ru.kpfu.itis.liiceberg.github_storage.data.model

import com.google.gson.annotations.SerializedName

data class GitHubTree(
    val sha: String,
    val url: String,
    val tree: List<GitHubTreeNode>,
    val truncated: Boolean
)

data class GitHubTreeNode(
    val path: String,
    val mode: String,
    val type: NodeType,
    val sha: String,
    val size: Int? = null,
    val url: String
)

enum class NodeType {
    @SerializedName("blob")
    BLOB,
    @SerializedName("tree")
    TREE
}