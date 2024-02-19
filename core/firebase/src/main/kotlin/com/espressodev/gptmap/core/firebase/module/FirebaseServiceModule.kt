package com.espressodev.gptmap.core.firebase.module

import com.espressodev.gptmap.core.firebase.AccountService
import com.espressodev.gptmap.core.firebase.FirestoreDataStore
import com.espressodev.gptmap.core.firebase.StorageDataStore
import com.espressodev.gptmap.core.firebase.impl.AccountServiceImpl
import com.espressodev.gptmap.core.firebase.impl.FirestoreDataStoreImpl
import com.espressodev.gptmap.core.firebase.impl.StorageDataStoreImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface FirebaseServiceModule {
    @Binds
    fun provideAccountService(impl: AccountServiceImpl): AccountService

    @Binds
    fun provideFirestoreService(impl: FirestoreDataStoreImpl): FirestoreDataStore

    @Binds
    fun provideStorageService(impl: StorageDataStoreImpl): StorageDataStore
}
