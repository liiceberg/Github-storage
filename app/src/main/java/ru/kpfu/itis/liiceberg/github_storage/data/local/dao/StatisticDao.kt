package ru.kpfu.itis.liiceberg.github_storage.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import ru.kpfu.itis.liiceberg.github_storage.data.local.entity.StatisticEntity

@Dao
interface StatisticDao {

    @Insert
    fun save(entity: StatisticEntity)

    @Query("select * from history where directory = :directory order by date desc")
    fun getAll(directory: String) : List<StatisticEntity>?
}