package ru.kpfu.itis.liiceberg.github_storage.data.remote.pojo.request

import com.google.gson.annotations.SerializedName

data class CreateTreeRequest(
    @SerializedName("base_tree") val baseTreeSha: String,
    val tree: List<CreateTreeRequestNode>
)

interface CreateTreeRequestNode

data class CreateTreeRequestNodeUpdate(
    val path: String,
    val mode: String,
    val type: String,
    val content: String
) : CreateTreeRequestNode

data class CreateTreeRequestNodeDelete(
    val path: String,
    val mode: String,
    val type: String,
    val sha: String? = null,
) : CreateTreeRequestNode