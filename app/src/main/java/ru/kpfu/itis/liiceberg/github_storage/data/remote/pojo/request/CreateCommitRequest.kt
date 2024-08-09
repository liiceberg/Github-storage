package ru.kpfu.itis.liiceberg.github_storage.data.remote.pojo.request

data class CreateCommitRequest(
    val parents: List<String>,
    val tree: String,
    val message: String
)