package com.espressodev.gptmap.core

import com.espressodev.gptmap.core.domain.DownloadAndCompressImageUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object DomainModule {
    @Singleton
    @Provides
    fun provideDownloadAndCompressImageUseCase(): DownloadAndCompressImageUseCase =
        DownloadAndCompressImageUseCase()
}