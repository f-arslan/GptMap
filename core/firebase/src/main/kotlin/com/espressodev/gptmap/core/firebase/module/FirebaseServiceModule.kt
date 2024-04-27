package com.espressodev.gptmap.core.firebase.module

import com.espressodev.gptmap.core.firebase.AccountService
import com.espressodev.gptmap.core.firebase.FirestoreRepository
import com.espressodev.gptmap.core.firebase.StorageRepository
import com.espressodev.gptmap.core.firebase.impl.AccountServiceImpl
import com.espressodev.gptmap.core.firebase.impl.FirestoreDataSource
import com.espressodev.gptmap.core.firebase.impl.StorageDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Suppress("unused")
@Module
@InstallIn(SingletonComponent::class)
interface FirebaseServiceModule {
    @Binds
    fun provideAccountService(impl: AccountServiceImpl): AccountService

    @Binds
    fun provideFirestoreService(impl: FirestoreDataSource): FirestoreRepository

    @Binds
    fun provideStorageService(impl: StorageDataSource): StorageRepository
}
