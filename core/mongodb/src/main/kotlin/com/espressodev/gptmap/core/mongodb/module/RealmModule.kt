package com.espressodev.gptmap.core.mongodb.module

import com.espressodev.gptmap.core.mongodb.RealmAccountService
import com.espressodev.gptmap.core.mongodb.impl.MongoModule.APP_ID
import com.espressodev.gptmap.core.mongodb.impl.RealmAccountServiceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.realm.kotlin.mongodb.App
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object RealmModule {
    @Singleton
    @Provides
    fun provideRealmApp(): App = App.create(APP_ID)
    @Provides
    fun bindRealmAccountService(app: App): RealmAccountService =
        RealmAccountServiceImpl(app = app)



    const val APP_ID = "gptmapapp-odcnu"
}