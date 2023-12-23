package com.espressodev.gptmap.core

import com.espressodev.gptmap.core.domain.DownloadAndCompressImageUseCase
import com.espressodev.gptmap.core.domain.SaveImageToInternalStorageUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
object DomainModule {

    @Provides
    fun provideDownloadAndCompressImageUseCase(): DownloadAndCompressImageUseCase =
        DownloadAndCompressImageUseCase()

}