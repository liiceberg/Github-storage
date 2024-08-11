package ru.kpfu.itis.liiceberg.github_storage.data.repository

import androidx.datastore.DataStore
import androidx.datastore.preferences.Preferences
import androidx.datastore.preferences.edit
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import ru.kpfu.itis.liiceberg.github_storage.data.local.dao.StatisticDao
import ru.kpfu.itis.liiceberg.github_storage.data.local.entity.StatisticEntity
import ru.kpfu.itis.liiceberg.github_storage.data.local.mapper.StatisticMapper
import ru.kpfu.itis.liiceberg.github_storage.data.model.GitHubAction
import ru.kpfu.itis.liiceberg.github_storage.data.remote.GitHubApi
import ru.kpfu.itis.liiceberg.github_storage.data.remote.pojo.request.CreateCommitRequest
import ru.kpfu.itis.liiceberg.github_storage.data.remote.pojo.request.CreateTreeRequest
import ru.kpfu.itis.liiceberg.github_storage.data.remote.pojo.request.CreateTreeRequestNode
import ru.kpfu.itis.liiceberg.github_storage.data.remote.pojo.request.CreateTreeRequestNodeDelete
import ru.kpfu.itis.liiceberg.github_storage.data.remote.pojo.request.CreateTreeRequestNodeUpdate
import ru.kpfu.itis.liiceberg.github_storage.data.remote.pojo.request.UpdateBranchRequest
import ru.kpfu.itis.liiceberg.github_storage.data.remote.pojo.response.NodeType
import ru.kpfu.itis.liiceberg.github_storage.domain.model.GitHubActionItem
import ru.kpfu.itis.liiceberg.github_storage.domain.repository.GitHubRepository
import ru.kpfu.itis.liiceberg.github_storage.domain.repository.SystemFilesRepository
import ru.kpfu.itis.liiceberg.github_storage.util.FilesComparator
import ru.kpfu.itis.liiceberg.github_storage.util.PrefsKeys
import ru.kpfu.itis.liiceberg.github_storage.util.decodeFromBase64
import java.io.File
import java.net.URI
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class GitHubRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val gitHubApi: GitHubApi,
    private val filesRepository: SystemFilesRepository,
    private val statisticDao: StatisticDao,
    private val statisticMapper: StatisticMapper
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

    override suspend fun getHistory(): List<GitHubActionItem> {
        return statisticMapper.mapStatisticEntityToGitHubActionItem(
            statisticDao.getAll(filesRepository.getRootFileAbsolutePath())
        )
    }

    override suspend fun pull() {
        parseRepositoryUrl()
        if (owner.isNullOrEmpty() || repository.isNullOrEmpty()) return
        val tree = gitHubApi.getAllFiles(owner!!, repository!!)

        val files = mutableMapOf<String, String>()
        tree.tree
            .filter { it.type == NodeType.BLOB }
            .map { node ->
                files[node.path] = getFileContent(node.url)
            }

        saveStatistic(GitHubAction.PULL, files)

        filesRepository.saveAll(files)

        tree.tree.forEach { node ->
            val isFolder = node.type == NodeType.TREE
            var content: String? = null
            if (isFolder.not()) {
                content = files[node.path]
            }
            filesRepository.saveFile(path = node.path, content = content, isFolder = isFolder)
        }
    }

    override suspend fun commitAndPush() {
        parseRepositoryUrl()
        if (owner.isNullOrEmpty() || repository.isNullOrEmpty()) return

        val lastObjectSha = gitHubApi.getHeads(owner!!, repository!!).lastObject.sha
        val baseTreeSha = gitHubApi.getCommit(owner!!, repository!!, lastObjectSha).tree.sha
        val createTreeRequest = CreateTreeRequest(baseTreeSha, buildGitHubTree())
        val newTreeSha = gitHubApi.createTree(owner!!, repository!!, createTreeRequest).sha
        val createCommitRequest =
            CreateCommitRequest(listOf(lastObjectSha), newTreeSha, getCommitMessage())
        val commitSha = gitHubApi.createCommit(owner!!, repository!!, createCommitRequest).sha
        gitHubApi.updateRefs(owner!!, repository!!, sha = UpdateBranchRequest(commitSha))
    }

    private suspend fun getFileContent(url: String): String {
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

    private val nodes = mutableListOf<CreateTreeRequestNode>()

    private suspend fun buildGitHubTree(): List<CreateTreeRequestNode> {
        val root = filesRepository.getRoot()
        nodes.clear()
        bypass(root)

        val rootPath = filesRepository.getRootFileAbsolutePath()

        val files = mutableMapOf<String, String>()
        nodes.forEach { node ->
            (node as? CreateTreeRequestNodeUpdate)?.let {
                files[node.path] = node.content
            }
        }
        saveStatistic(action = GitHubAction.PUSH, files)

        files.forEach { (path, content) -> filesRepository.saveFile(rootPath, path, content) }

        verifyDeletedFiles(rootPath)
        return nodes
    }

    private suspend fun bypass(dir: File) {
        dir.listFiles()?.forEach {
            if (it.isDirectory) {
                bypass(it)
            } else {
                nodes.add(convertFileToNode(it))
            }
        }
    }

    private suspend fun convertFileToNode(file: File): CreateTreeRequestNode {
        val path = filesRepository.getFolderRelativePath(file.path)
        return CreateTreeRequestNodeUpdate(
            path = path,
            mode = NodeType.BLOB.mode,
            type = NodeType.BLOB.toString(),
            content = file.readText()
        )
    }

    private suspend fun verifyDeletedFiles(root: String) {
        val existingFiles =
            nodes.map { node -> (node as? CreateTreeRequestNodeUpdate)?.path }.toSet()

        filesRepository.getSavedFilesNames()?.forEach {
            if (existingFiles.contains(it).not()) {
                nodes.add(
                    CreateTreeRequestNodeDelete(
                        path = it,
                        mode = NodeType.BLOB.mode,
                        type = NodeType.BLOB.toString()
                    )
                )
                filesRepository.deleteFile(directory = root, path = it)
            }
        }
    }

    private fun getCommitMessage(): String {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("d MMMM yy г., HH:mm"))
    }

    private suspend fun saveStatistic(action: GitHubAction, files: Map<String, String>) {
        val root = filesRepository.getRootFileAbsolutePath()
        val comparator = FilesComparator(filesRepository.getSavedFilesWithContent(), files)

        statisticDao.save(
            StatisticEntity(
                action = action.name,
                date = System.currentTimeMillis(),
                added = comparator.added(),
                deleted = comparator.deleted(),
                directory = root
            )
        )
    }
}