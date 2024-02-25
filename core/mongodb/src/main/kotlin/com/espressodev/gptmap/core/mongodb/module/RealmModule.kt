package com.espressodev.gptmap.core.mongodb.module

import com.espressodev.gptmap.core.mongodb.FavouriteRealmRepository
import com.espressodev.gptmap.core.mongodb.ImageAnalysisRealmRepository
import com.espressodev.gptmap.core.mongodb.ImageMessageRealmRepository
import com.espressodev.gptmap.core.mongodb.RealmAccountRepository
import com.espressodev.gptmap.core.mongodb.UserManagementRealmRepository
import com.espressodev.gptmap.core.mongodb.impl.FavouriteRealmDataSource
import com.espressodev.gptmap.core.mongodb.impl.ImageAnalysisRealmDataSource
import com.espressodev.gptmap.core.mongodb.impl.ImageMessageRealmDataSource
import com.espressodev.gptmap.core.mongodb.impl.RealmAccountService
import com.espressodev.gptmap.core.mongodb.impl.UserManagementRealmDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface RealmModule {
    @Binds
    fun bindRealmAccountService(impl: RealmAccountService): RealmAccountRepository

    @Binds
    fun provideFavouritesDataSource(impl: FavouriteRealmDataSource): FavouriteRealmRepository

    @Binds
    fun provideImageAnalysisDataSource(impl: ImageAnalysisRealmDataSource): ImageAnalysisRealmRepository

    @Binds
    fun provideImageMessageDataSource(impl: ImageMessageRealmDataSource): ImageMessageRealmRepository

    @Binds
    fun provideUserManagementDataSource(impl: UserManagementRealmDataSource): UserManagementRealmRepository
}
