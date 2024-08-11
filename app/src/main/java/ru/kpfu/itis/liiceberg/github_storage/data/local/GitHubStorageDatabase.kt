package ru.kpfu.itis.liiceberg.github_storage.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.kpfu.itis.liiceberg.github_storage.data.local.dao.FileDao
import ru.kpfu.itis.liiceberg.github_storage.data.local.dao.StatisticDao
import ru.kpfu.itis.liiceberg.github_storage.data.local.entity.FileEntity
import ru.kpfu.itis.liiceberg.github_storage.data.local.entity.StatisticEntity

@Database(entities = [FileEntity::class, StatisticEntity::class], version = 1)
abstract class GitHubStorageDatabase : RoomDatabase() {
    abstract val fileDao: FileDao
    abstract val statisticDao: StatisticDao
}