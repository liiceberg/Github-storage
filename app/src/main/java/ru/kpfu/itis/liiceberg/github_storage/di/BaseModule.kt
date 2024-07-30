package ru.kpfu.itis.liiceberg.github_storage.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

@Module
@InstallIn(SingletonComponent::class)
class BaseModule {

    @Provides
    fun provideCoroutineIoDispatcher(): CoroutineDispatcher = Dispatchers.IO
}