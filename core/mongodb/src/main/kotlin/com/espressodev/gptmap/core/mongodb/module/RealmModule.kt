package com.espressodev.gptmap.core.mongodb.module

import com.espressodev.gptmap.core.mongodb.RealmAccountService
import com.espressodev.gptmap.core.mongodb.impl.RealmAccountServiceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent


@Module
@InstallIn(ViewModelComponent::class)
object RealmModule {
    @Provides
    fun bindRealmAccountService(): RealmAccountService =
        RealmAccountServiceImpl()


    private const val APP_ID = "gptmapapp-odcnu"
}