package ru.kpfu.itis.liiceberg.github_storage.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "files", indices = [Index(value = ["directory", "path"], unique = true)])
data class FileEntity(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    val directory: String,
    val path: String,
    val content: String
)