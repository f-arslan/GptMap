package com.espressodev.gptmap.core

import com.espressodev.gptmap.core.domain.DownloadAndCompressImageUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DomainModule {
    @Singleton
    @Provides
    fun provideDownloadAndCompressImageUseCase(ioDispatcher: CoroutineDispatcher): DownloadAndCompressImageUseCase =
        DownloadAndCompressImageUseCase(ioDispatcher = ioDispatcher)
}
