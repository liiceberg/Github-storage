package ru.kpfu.itis.liiceberg.github_storage.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.kpfu.itis.liiceberg.github_storage.data.local.dao.FileDao
import ru.kpfu.itis.liiceberg.github_storage.data.local.entity.FileEntity

@Database(entities = [FileEntity::class], version = 1)
abstract class GitHubStorageDatabase : RoomDatabase() {
    abstract val fileDao: FileDao
}