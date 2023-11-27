package com.espressodev.gptmap.core.mongodb.module

import android.util.Log
import com.espressodev.gptmap.core.model.realm.Hero
import com.espressodev.gptmap.core.model.realm.RealmUser
import com.espressodev.gptmap.core.mongodb.RealmAccountService
import com.espressodev.gptmap.core.mongodb.impl.RealmAccountServiceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.log.LogLevel
import io.realm.kotlin.mongodb.App
import io.realm.kotlin.mongodb.AppConfiguration
import io.realm.kotlin.mongodb.User
import io.realm.kotlin.mongodb.exceptions.SyncException
import io.realm.kotlin.mongodb.subscriptions
import io.realm.kotlin.mongodb.sync.SyncConfiguration
import io.realm.kotlin.mongodb.sync.SyncSession
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val APP_ID = "gptmapapplication-giuno"
private const val BASE_URL = "https://realm.mongodb.com"
private const val TAG = "RealmModule"

object RealmModule {
    val app = App.create(
        AppConfiguration.Builder(APP_ID)
            .baseUrl(BASE_URL)
            .build()
    )
    lateinit var user: User
    lateinit var realm: Realm
    fun initRealm(currentUser: User) {
        val config = SyncConfiguration.Builder(currentUser, setOf(Hero::class, RealmUser::class))
            .initialSubscriptions { realm: Realm ->
                add(realm.query<Hero>("owner_id == $0", currentUser.id))
                add(realm.query<RealmUser>("userId == $0", currentUser.id))
            }
            .errorHandler { _: SyncSession, error: SyncException ->
                Log.e("RealmSyncServiceImpl", "errorHandler: ", error)
            }
            .log(LogLevel.ALL)
            .waitForInitialRemoteData()
            .build()
        realm = Realm.open(config)
        user = currentUser
        Log.d(
            TAG,
            "Successfully opened synced realm: ${realm.configuration.name}"
        )
        CoroutineScope(Dispatchers.Main).launch {
            realm.subscriptions.waitForSynchronization()
        }
    }
}

@Module
@InstallIn(ViewModelComponent::class)
object RealmServiceModule {
    @Provides
    fun bindRealmAccountService(): RealmAccountService =
        RealmAccountServiceImpl()
}