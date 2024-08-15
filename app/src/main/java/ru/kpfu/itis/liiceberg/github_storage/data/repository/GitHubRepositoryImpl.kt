package ru.kpfu.itis.liiceberg.github_storage.data.repository

import android.content.Context
import androidx.annotation.StringRes
import androidx.datastore.DataStore
import androidx.datastore.preferences.Preferences
import androidx.datastore.preferences.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import ru.kpfu.itis.liiceberg.github_storage.R
import ru.kpfu.itis.liiceberg.github_storage.data.exception.AccessDeniedException
import ru.kpfu.itis.liiceberg.github_storage.data.exception.ConflictException
import ru.kpfu.itis.liiceberg.github_storage.data.exception.ConnectionException
import ru.kpfu.itis.liiceberg.github_storage.data.exception.ForbiddenException
import ru.kpfu.itis.liiceberg.github_storage.data.exception.NetworkException
import ru.kpfu.itis.liiceberg.github_storage.data.exception.RepositoryNotFoundException
import ru.kpfu.itis.liiceberg.github_storage.data.local.dao.StatisticDao
import ru.kpfu.itis.liiceberg.github_storage.data.local.entity.StatisticEntity
import ru.kpfu.itis.liiceberg.github_storage.data.local.mapper.StatisticMapper
import ru.kpfu.itis.liiceberg.github_storage.data.remote.ApiHandler
import ru.kpfu.itis.liiceberg.github_storage.data.remote.GitHubApi
import ru.kpfu.itis.liiceberg.github_storage.data.remote.ResultWrapper
import ru.kpfu.itis.liiceberg.github_storage.data.remote.mapper.GitStatusMapper
import ru.kpfu.itis.liiceberg.github_storage.data.remote.model.GitHubAction
import ru.kpfu.itis.liiceberg.github_storage.data.remote.model.GitStatus
import ru.kpfu.itis.liiceberg.github_storage.data.remote.pojo.request.CreateCommitRequest
import ru.kpfu.itis.liiceberg.github_storage.data.remote.pojo.request.CreateTreeRequest
import ru.kpfu.itis.liiceberg.github_storage.data.remote.pojo.request.CreateTreeRequestNode
import ru.kpfu.itis.liiceberg.github_storage.data.remote.pojo.request.CreateTreeRequestNodeDelete
import ru.kpfu.itis.liiceberg.github_storage.data.remote.pojo.request.CreateTreeRequestNodeUpdate
import ru.kpfu.itis.liiceberg.github_storage.data.remote.pojo.request.UpdateBranchRequest
import ru.kpfu.itis.liiceberg.github_storage.data.remote.pojo.response.GitHubTree
import ru.kpfu.itis.liiceberg.github_storage.data.remote.pojo.response.NodeType
import ru.kpfu.itis.liiceberg.github_storage.domain.model.GitHubActionItem
import ru.kpfu.itis.liiceberg.github_storage.domain.repository.GitHubRepository
import ru.kpfu.itis.liiceberg.github_storage.domain.repository.SystemFilesRepository
import ru.kpfu.itis.liiceberg.github_storage.util.FilesComparator
import ru.kpfu.itis.liiceberg.github_storage.util.PrefsKeys
import ru.kpfu.itis.liiceberg.github_storage.util.decodeFromBase64
import java.net.URI
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class GitHubRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val gitHubApi: GitHubApi,
    private val filesRepository: SystemFilesRepository,
    private val statisticDao: StatisticDao,
    private val statisticMapper: StatisticMapper,
    private val gitStatusMapper: GitStatusMapper,
    private val apiHandler: ApiHandler,
    @ApplicationContext private val context: Context
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

        val result = apiHandler.makeSafeApiCall { gitHubApi.getAllFiles(owner!!, repository!!) }

        when (result) {
            is ResultWrapper.NetworkError -> throw networkException()
            is ResultWrapper.GenericError -> throw genericException(result.code, result.error)
            is ResultWrapper.Success -> makePull(result.value)
        }
    }

    private suspend fun makePull(tree: GitHubTree) {
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

        val getHeadsResult =
            apiHandler.makeSafeApiCall { gitHubApi.getHeads(owner!!, repository!!) }
        val lastObjectSha = when (getHeadsResult) {
            is ResultWrapper.Success -> getHeadsResult.value.lastObject.sha
            is ResultWrapper.NetworkError -> throw networkException()
            is ResultWrapper.GenericError -> throw genericException(
                getHeadsResult.code,
                getHeadsResult.error
            )
        }

        val getCommitResult =
            apiHandler.makeSafeApiCall { gitHubApi.getCommit(owner!!, repository!!, lastObjectSha) }
        val baseTreeSha = when (getCommitResult) {
            is ResultWrapper.Success -> getCommitResult.value.tree.sha
            is ResultWrapper.NetworkError -> throw networkException()
            is ResultWrapper.GenericError -> throw genericException(
                getCommitResult.code,
                getCommitResult.error
            )
        }

        val createTreeRequest = CreateTreeRequest(baseTreeSha, buildGitHubTree())
        val createTreeResult = apiHandler.makeSafeApiCall {
            gitHubApi.createTree(
                owner!!,
                repository!!,
                createTreeRequest
            )
        }
        val newTreeSha = when (createTreeResult) {
            is ResultWrapper.Success -> createTreeResult.value.sha
            is ResultWrapper.NetworkError -> throw networkException()
            is ResultWrapper.GenericError -> throw genericException(
                createTreeResult.code,
                createTreeResult.error
            )
        }

        val createCommitRequest =
            CreateCommitRequest(listOf(lastObjectSha), newTreeSha, getCommitMessage())
        val createCommitResult = apiHandler.makeSafeApiCall {
            gitHubApi.createCommit(
                owner!!,
                repository!!,
                createCommitRequest
            )
        }
        val commitSha = when (createCommitResult) {
            is ResultWrapper.Success -> createCommitResult.value.sha
            is ResultWrapper.NetworkError -> throw networkException()
            is ResultWrapper.GenericError -> throw genericException(
                createCommitResult.code,
                createCommitResult.error
            )
        }

        apiHandler.makeSafeApiCall {
            gitHubApi.updateRefs(
                owner!!,
                repository!!,
                sha = UpdateBranchRequest(commitSha)
            )
        }
    }

    override suspend fun getFilesStatus(): GitStatus {
        val comparator = FilesComparator(
            filesRepository.getSavedFilesWithContent(),
            filesRepository.getExistingFilesWithContent()
        )
        return gitStatusMapper.mapToGitStatus(comparator.all())
    }

    private suspend fun getFileContent(url: String): String {
        val result = apiHandler.makeSafeApiCall { gitHubApi.getFile(url) }
        when (result) {
            is ResultWrapper.Success -> return result.value.content.decodeFromBase64()
            is ResultWrapper.NetworkError -> throw networkException()
            is ResultWrapper.GenericError -> throw genericException(result.code, result.error)
        }
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


    private suspend fun buildGitHubTree(): List<CreateTreeRequestNode> {
        val nodes = mutableListOf<CreateTreeRequestNode>()

        val existingFiles = filesRepository.getExistingFilesWithContent()
        saveStatistic(action = GitHubAction.PUSH, existingFiles)

        existingFiles.forEach { (path, content) -> nodes.add(convertFileToNode(path, content)) }

        val rootPath = filesRepository.getRootFileAbsolutePath()

        filesRepository.saveFiles(rootPath, existingFiles)

        filesRepository.getSavedFilesNames()?.forEach {
            if (existingFiles.keys.contains(it).not()) {
                nodes.add(
                    CreateTreeRequestNodeDelete(
                        path = it,
                        mode = NodeType.BLOB.mode,
                        type = NodeType.BLOB.toString()
                    )
                )
                filesRepository.deleteFile(directory = rootPath, path = it)
            }
        }
        return nodes
    }

    private fun convertFileToNode(path: String, content: String): CreateTreeRequestNode {
        return CreateTreeRequestNodeUpdate(
            path = path,
            mode = NodeType.BLOB.mode,
            type = NodeType.BLOB.toString(),
            content = content
        )
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

    private fun networkException() = ConnectionException(getErrorMessage(R.string.connection_error))

    private fun genericException(code: Int?, message: String?) = when (code) {
        401 -> AccessDeniedException(getErrorMessage(R.string.access_denied_error))
        403 -> ForbiddenException(getErrorMessage(R.string.forbidden_error))
        404 -> RepositoryNotFoundException(getErrorMessage(R.string.page_not_found_error))
        409 -> ConflictException(getErrorMessage(R.string.conflict_error))
        else -> NetworkException(message ?: getErrorMessage(R.string.unknown_network_error))
    }

    private fun getErrorMessage(@StringRes id: Int) = context.resources.getString(id)

}