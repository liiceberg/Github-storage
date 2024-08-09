package ru.kpfu.itis.liiceberg.github_storage.data.remote.pojo.response

data class GitHubCommit(
    val tree: GitHubCommitTree
)

data class GitHubCommitTree(
    val sha: String,
    val url: String
)