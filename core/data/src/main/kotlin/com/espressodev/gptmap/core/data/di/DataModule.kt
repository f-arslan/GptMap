package com.espressodev.gptmap.core.data.di

import com.espressodev.gptmap.core.data.repository.AuthenticationRepository
import com.espressodev.gptmap.core.data.repository.FavouriteRepository
import com.espressodev.gptmap.core.data.repository.FileRepository
import com.espressodev.gptmap.core.data.repository.ImageAnalysisRepository
import com.espressodev.gptmap.core.data.repository.ImageMessageRepository
import com.espressodev.gptmap.core.data.repository.UserRepository
import com.espressodev.gptmap.core.data.repository.impl.AuthenticationRepositoryImpl
import com.espressodev.gptmap.core.data.repository.impl.FavouriteRepositoryImpl
import com.espressodev.gptmap.core.data.repository.impl.FileRepositoryImpl
import com.espressodev.gptmap.core.data.repository.impl.ImageAnalysisRepositoryImpl
import com.espressodev.gptmap.core.data.repository.impl.ImageMessageRepositoryImpl
import com.espressodev.gptmap.core.data.repository.impl.UserRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Suppress("unused")
@Module
@InstallIn(SingletonComponent::class)
interface DataModule {
    @Binds
    fun provideImageMessageRepo(impl: ImageMessageRepositoryImpl): ImageMessageRepository

    @Binds
    fun provideUserRepo(impl: UserRepositoryImpl): UserRepository

    @Binds
    fun provideAuthenticationRepo(impl: AuthenticationRepositoryImpl): AuthenticationRepository

    @Binds
    fun provideImageAnalysisRepo(impl: ImageAnalysisRepositoryImpl): ImageAnalysisRepository

    @Binds
    fun provideFileRepo(impl: FileRepositoryImpl): FileRepository

    @Binds
    fun provideFavouriteRepo(impl: FavouriteRepositoryImpl): FavouriteRepository
}
