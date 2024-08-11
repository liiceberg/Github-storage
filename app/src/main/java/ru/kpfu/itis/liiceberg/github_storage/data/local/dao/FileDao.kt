package ru.kpfu.itis.liiceberg.github_storage.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.kpfu.itis.liiceberg.github_storage.data.local.entity.FileEntity

@Dao
interface FileDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(entity: FileEntity)

    @Query("select * from files where directory = :directory")
    fun getAll(directory: String) : List<FileEntity>?

    @Query("delete from files where path = :path and directory = :directory")
    fun delete(path: String, directory: String)
}