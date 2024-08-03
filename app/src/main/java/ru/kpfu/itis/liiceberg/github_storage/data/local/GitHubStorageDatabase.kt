package ru.kpfu.itis.liiceberg.github_storage.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.kpfu.itis.liiceberg.github_storage.data.local.dao.DirectoryRootDao
import ru.kpfu.itis.liiceberg.github_storage.data.local.entity.DirectoryRootEntity

@Database(entities = [DirectoryRootEntity::class], version = 1)
@TypeConverters(Converters::class)
abstract class GitHubStorageDatabase : RoomDatabase() {
    abstract val directoryRootDao: DirectoryRootDao
}