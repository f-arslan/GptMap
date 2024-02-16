package com.espressodev.gptmap.core.mongodb.module

import com.espressodev.gptmap.core.mongodb.FavouriteService
import com.espressodev.gptmap.core.mongodb.ImageAnalysisService
import com.espressodev.gptmap.core.mongodb.ImageMessageService
import com.espressodev.gptmap.core.mongodb.RealmAccountService
import com.espressodev.gptmap.core.mongodb.UserManagementService
import com.espressodev.gptmap.core.mongodb.impl.FavouriteServiceImpl
import com.espressodev.gptmap.core.mongodb.impl.ImageAnalysisServiceImpl
import com.espressodev.gptmap.core.mongodb.impl.ImageMessageServiceImpl
import com.espressodev.gptmap.core.mongodb.impl.RealmAccountServiceImpl
import com.espressodev.gptmap.core.mongodb.impl.UserManagementServiceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RealmServiceModule {
    @Singleton
    @Provides
    fun bindRealmAccountService(): RealmAccountService =
        RealmAccountServiceImpl()

    @Singleton
    @Provides
    fun provideFavouritesService(): FavouriteService =
        FavouriteServiceImpl()

    @Singleton
    @Provides
    fun provideImageAnalysisService(): ImageAnalysisService =
        ImageAnalysisServiceImpl()

    @Singleton
    @Provides
    fun provideImageMessageService(): ImageMessageService =
        ImageMessageServiceImpl()

    @Singleton
    @Provides
    fun provideUserManagementService(): UserManagementService =
        UserManagementServiceImpl()
}
