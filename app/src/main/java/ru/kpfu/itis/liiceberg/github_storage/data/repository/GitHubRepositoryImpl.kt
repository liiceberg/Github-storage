package ru.kpfu.itis.liiceberg.github_storage.data.repository

import androidx.datastore.DataStore
import androidx.datastore.preferences.Preferences
import androidx.datastore.preferences.edit
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import ru.kpfu.itis.liiceberg.github_storage.data.model.GitHubTree
import ru.kpfu.itis.liiceberg.github_storage.data.model.NodeType
import ru.kpfu.itis.liiceberg.github_storage.data.remote.GitHubApi
import ru.kpfu.itis.liiceberg.github_storage.domain.repository.GitHubRepository
import ru.kpfu.itis.liiceberg.github_storage.domain.repository.SystemFilesRepository
import ru.kpfu.itis.liiceberg.github_storage.util.PrefsKeys
import ru.kpfu.itis.liiceberg.github_storage.util.decodeFromBase64
import java.net.URI
import javax.inject.Inject

class GitHubRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val gitHubApi: GitHubApi,
    private val filesRepository: SystemFilesRepository
) : GitHubRepository {

    private var owner: String? = null
    private var repository: String? = null

    override suspend fun getRepositoryUrl(): String {
        return dataStore.data.map { it[PrefsKeys.REPOSITORY_KEY] }.first() ?: ""
    }

    override suspend fun saveRepositoryUrl(repository: String) {
        dataStore.edit {
            it[PrefsKeys.REPOSITORY_KEY] = repository
        }
    }

    override suspend fun pull() {
        parseRepositoryUrl()
        if (owner.isNullOrEmpty() || repository.isNullOrEmpty()) return

        val tree = gitHubApi.getAllFiles(owner!!, repository!!)
        performTreeTraversal(tree)
    }

    private suspend fun performTreeTraversal(tree: GitHubTree) {
        tree.tree.forEach { node ->
            val isFolder = node.type == NodeType.TREE
            var content: String? = null
            if (isFolder.not()) {
                content = getFileContent(node.url)
            }
            filesRepository.saveFile(path = node.path, content = content, isFolder = isFolder)
        }
    }

    private suspend fun getFileContent(url: String) : String {
        return gitHubApi.getFile(url).content.decodeFromBase64()
    }

    private suspend fun parseRepositoryUrl() {
        URI(getRepositoryUrl())
            .path
            .split("/")
            .run {
            owner = this[1]
            repository = this[2]
        }
    }
}