package com.espressodev.gptmap.core.data.module

import com.espressodev.gptmap.core.data.AccountService
import com.espressodev.gptmap.core.data.FirestoreService
import com.espressodev.gptmap.core.data.StorageService
import com.espressodev.gptmap.core.data.impl.AccountServiceImpl
import com.espressodev.gptmap.core.data.impl.FirestoreServiceImpl
import com.espressodev.gptmap.core.data.impl.StorageServiceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface ServiceModule {
    @Binds
    fun provideAccountService(impl: AccountServiceImpl): AccountService

    @Binds
    fun provideFirestoreService(impl: FirestoreServiceImpl): FirestoreService

    @Binds
    fun provideStorageService(impl: StorageServiceImpl): StorageService
}
