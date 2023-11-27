package com.espressodev.gptmap.core.mongodb.module

import com.espressodev.gptmap.core.mongodb.RealmAccountService
import com.espressodev.gptmap.core.mongodb.RealmSyncService
import com.espressodev.gptmap.core.mongodb.impl.RealmAccountServiceImpl
import com.espressodev.gptmap.core.mongodb.impl.RealmSyncServiceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.components.SingletonComponent
import io.realm.kotlin.mongodb.App
import io.realm.kotlin.mongodb.AppConfiguration
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object RealmModule {

    @Provides
    @Singleton
    fun provideRealmApp(): App = App.create(AppConfiguration.Builder(APP_ID).baseUrl(BASE_URL).build())

    private const val APP_ID = "gptmapapplication-giuno"
    private const val BASE_URL = "https://realm.mongodb.com"
}

@Module
@InstallIn(ViewModelComponent::class)
object RealmServiceModule {
    @Provides
    fun bindRealmAccountService(app: App): RealmAccountService =
        RealmAccountServiceImpl(app = app)

    @Provides
    fun bindRealmSyncService(app: App): RealmSyncService =
        RealmSyncServiceImpl(app = app)
}