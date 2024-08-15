package ru.kpfu.itis.liiceberg.github_storage.data.remote.mapper

import ru.kpfu.itis.liiceberg.github_storage.data.remote.model.GitStatus
import ru.kpfu.itis.liiceberg.github_storage.util.FilesComparator
import javax.inject.Inject

class GitStatusMapper @Inject constructor() {

    fun mapToGitStatus(map: Map<String, FilesComparator.FileStatus>): GitStatus {
        val modified = mutableListOf<String>()
        val created = mutableListOf<String>()
        val deleted = mutableListOf<String>()
        map.forEach { (file, status) ->
            when (status) {
                FilesComparator.FileStatus.MODIFIED -> modified.add(file)
                FilesComparator.FileStatus.CREATED -> created.add(file)
                FilesComparator.FileStatus.DELETED -> deleted.add(file)
            }
        }
        return GitStatus(
            modified, created, deleted
        )
    }
}