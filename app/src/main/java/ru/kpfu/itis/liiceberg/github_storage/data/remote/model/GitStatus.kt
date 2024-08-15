package ru.kpfu.itis.liiceberg.github_storage.data.remote.model

data class GitStatus(
    val modified: List<String>,
    val created: List<String>,
    val deleted: List<String>
) {
    companion object {
        fun empty() = GitStatus(emptyList(), emptyList(), emptyList())
    }
}