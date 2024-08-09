package ru.kpfu.itis.liiceberg.github_storage.data.remote.pojo.response

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

enum class NodeType(val mode: String) {

    @SerializedName("blob")
    BLOB("100644"),
    @SerializedName("tree")
    TREE("040000");

    override fun toString(): String = name.lowercase()

}