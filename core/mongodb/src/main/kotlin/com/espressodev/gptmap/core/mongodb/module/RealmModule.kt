package com.espressodev.gptmap.core.mongodb.module

import android.util.Log
import com.espressodev.gptmap.core.model.realm.RealmUser
import com.espressodev.gptmap.core.mongodb.RealmAccountService
import com.espressodev.gptmap.core.mongodb.RealmDatabaseService
import com.espressodev.gptmap.core.mongodb.impl.RealmAccountServiceImpl
import com.espressodev.gptmap.core.mongodb.impl.RealmDatabaseServiceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.realm.kotlin.Realm
import io.realm.kotlin.log.LogLevel
import io.realm.kotlin.mongodb.App
import io.realm.kotlin.mongodb.User
import io.realm.kotlin.mongodb.sync.SyncConfiguration
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object RealmModule {
    @Singleton
    @Provides
    fun provideRealmApp(): App = App.create(APP_ID)

    @Provides
    fun provideRealmUser(app: App): User? = app.currentUser

    @Provides
    fun provideRealm(user: User?): Realm? = user?.let {
        Log.d("RealmModule", "provideRealm: ${user.id}")
        Realm.open(
            SyncConfiguration.Builder(user, setOf(RealmUser::class)).log(LogLevel.ALL)
            .build()
        )
    }

    @Provides
    fun bindRealmAccountService(app: App): RealmAccountService =
        RealmAccountServiceImpl(app = app)

    @Provides
    fun bindRealmDatabaseService(user: User?, realm: Realm?): RealmDatabaseService =
        RealmDatabaseServiceImpl(user = user, realm = realm)


    private const val APP_ID = "gptmapapp-odcnu"
}