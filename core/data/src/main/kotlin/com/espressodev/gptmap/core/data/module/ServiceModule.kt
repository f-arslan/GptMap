package com.espressodev.gptmap.core.data.module

import com.espressodev.gptmap.core.data.AccountService
import com.espressodev.gptmap.core.data.FirestoreService
import com.espressodev.gptmap.core.data.LogService
import com.espressodev.gptmap.core.data.impl.AccountServiceImpl
import com.espressodev.gptmap.core.data.impl.FirestoreServiceImpl
import com.espressodev.gptmap.core.data.impl.LogServiceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ServiceModule {
    @Binds
    abstract fun provideLogService(impl: LogServiceImpl): LogService

    @Binds
    abstract fun provideAccountService(impl: AccountServiceImpl): AccountService

    @Binds
    abstract fun provideFirestoreService(impl: FirestoreServiceImpl): FirestoreService
}