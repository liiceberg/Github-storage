package ru.kpfu.itis.liiceberg.github_storage.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history")
data class StatisticEntity(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    val action: String,
    val date: Long,
    val added: Int,
    val deleted: Int,
    val directory: String
)
