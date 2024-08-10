package ru.kpfu.itis.liiceberg.github_storage.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.kpfu.itis.liiceberg.github_storage.data.local.GitHubStorageDatabase
import ru.kpfu.itis.liiceberg.github_storage.data.local.dao.FileDao
import ru.kpfu.itis.liiceberg.github_storage.util.Keys
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): GitHubStorageDatabase {
        return Room.databaseBuilder(
            context,
            GitHubStorageDatabase::class.java,
            Keys.DATABASE_NAME
        ).build()
    }

    @Provides
    fun provideDirectoryDao(db: GitHubStorageDatabase) : FileDao = db.fileDao
}