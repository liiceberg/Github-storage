package ru.kpfu.itis.liiceberg.github_storage.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "directory", indices = [Index(value = ["location"], unique = true)])
data class DirectoryRootEntity(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    val location: String,
    val files: Set<String>
)