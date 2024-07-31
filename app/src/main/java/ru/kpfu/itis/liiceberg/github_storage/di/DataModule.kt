package ru.kpfu.itis.liiceberg.github_storage.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.kpfu.itis.liiceberg.github_storage.data.repository.GitHubRepositoryImpl
import ru.kpfu.itis.liiceberg.github_storage.data.repository.SystemFilesRepositoryImpl
import ru.kpfu.itis.liiceberg.github_storage.domain.repository.TokenManager
import ru.kpfu.itis.liiceberg.github_storage.data.repository.TokenManagerImpl
import ru.kpfu.itis.liiceberg.github_storage.domain.repository.GitHubRepository
import ru.kpfu.itis.liiceberg.github_storage.domain.repository.SystemFilesRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {
    @Binds
    @Singleton
    fun bindTokenManagerToTokenManagerImpl(tokenManagerImpl: TokenManagerImpl) : TokenManager

    @Binds
    @Singleton
    fun bindGitHubRepositoryToGitHubRepositoryImpl(gitHubRepositoryImpl: GitHubRepositoryImpl) : GitHubRepository

    @Binds
    @Singleton
    fun bindFilesRepositoryToFilesRepositoryImpl(systemFilesRepositoryImpl: SystemFilesRepositoryImpl) : SystemFilesRepository
}