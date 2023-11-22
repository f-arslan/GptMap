package com.espressodev.gptmap.core.mongodb.module

import com.espressodev.gptmap.core.mongodb.RealmAccountService
import com.espressodev.gptmap.core.mongodb.impl.RealmAccountServiceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
abstract class RealmModule {
    @Binds
    abstract fun bindRealmAccountService(impl: RealmAccountServiceImpl): RealmAccountService
}