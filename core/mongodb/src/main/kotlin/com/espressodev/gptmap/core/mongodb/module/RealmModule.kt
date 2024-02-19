package com.espressodev.gptmap.core.mongodb.module

import com.espressodev.gptmap.core.mongodb.FavouriteDataSource
import com.espressodev.gptmap.core.mongodb.ImageAnalysisDataSource
import com.espressodev.gptmap.core.mongodb.ImageMessageDataSource
import com.espressodev.gptmap.core.mongodb.RealmAccountService
import com.espressodev.gptmap.core.mongodb.UserManagementDataSource
import com.espressodev.gptmap.core.mongodb.impl.FavouriteDataSourceImpl
import com.espressodev.gptmap.core.mongodb.impl.ImageAnalysisDataSourceImpl
import com.espressodev.gptmap.core.mongodb.impl.ImageMessageDataSourceImpl
import com.espressodev.gptmap.core.mongodb.impl.RealmAccountServiceImpl
import com.espressodev.gptmap.core.mongodb.impl.UserManagementDataSourceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RealmDataModule {
    @Singleton
    @Provides
    fun bindRealmAccountService(): RealmAccountService =
        RealmAccountServiceImpl()

    @Singleton
    @Provides
    fun provideFavouritesDataSource(): FavouriteDataSource =
        FavouriteDataSourceImpl()

    @Singleton
    @Provides
    fun provideImageAnalysisDataSource(): ImageAnalysisDataSource =
        ImageAnalysisDataSourceImpl()

    @Singleton
    @Provides
    fun provideImageMessageDataSource(): ImageMessageDataSource =
        ImageMessageDataSourceImpl()

    @Singleton
    @Provides
    fun provideUserManagementDataSource(): UserManagementDataSource =
        UserManagementDataSourceImpl()
}

