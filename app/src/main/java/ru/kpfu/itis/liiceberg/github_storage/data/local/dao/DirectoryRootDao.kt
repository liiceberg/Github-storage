package ru.kpfu.itis.liiceberg.github_storage.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.kpfu.itis.liiceberg.github_storage.data.local.entity.DirectoryRootEntity

@Dao
interface DirectoryRootDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(entity: DirectoryRootEntity)

    @Query("select * from directory where location = :path")
    fun get(path: String) : DirectoryRootEntity?
}