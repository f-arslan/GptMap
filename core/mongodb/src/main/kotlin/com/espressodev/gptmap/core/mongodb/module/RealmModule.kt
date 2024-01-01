package com.espressodev.gptmap.core.mongodb.module

import android.util.Log
import com.espressodev.gptmap.core.model.realm.RealmContent
import com.espressodev.gptmap.core.model.realm.RealmFavourite
import com.espressodev.gptmap.core.model.realm.RealmImageAnalysis
import com.espressodev.gptmap.core.model.realm.RealmImageMessage
import com.espressodev.gptmap.core.model.realm.RealmLocationImage
import com.espressodev.gptmap.core.model.realm.RealmUser
import com.espressodev.gptmap.core.mongodb.RealmAccountService
import com.espressodev.gptmap.core.mongodb.RealmSyncService
import com.espressodev.gptmap.core.mongodb.impl.RealmAccountServiceImpl
import com.espressodev.gptmap.core.mongodb.impl.RealmSyncServiceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
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
import javax.inject.Singleton

private const val APP_ID = "gptmapapplication-giuno"
private const val BASE_URL = "https://realm.mongodb.com"
private const val TAG = "RealmModule"

object RealmModule {
    val app = App.create(
        AppConfiguration.Builder(APP_ID)
            .baseUrl(BASE_URL)
            .build()
    )
    lateinit var realmUser: User
    lateinit var realm: Realm
    fun initRealm(currentUser: User) {
        val config = SyncConfiguration.Builder(
            currentUser,
            setOf(
                RealmUser::class,
                RealmFavourite::class,
                RealmContent::class,
                RealmLocationImage::class,
                RealmImageAnalysis::class,
                RealmImageMessage::class
            )
        )
            .maxNumberOfActiveVersions(17)
            .initialSubscriptions { realm: Realm ->
                add(realm.query<RealmUser>("userId == $0", currentUser.id))
                add(
                    realm.query<RealmFavourite>("userId == $0", currentUser.id),
                    name = "location sub",
                    updateExisting = true
                )
                add(
                    realm.query<RealmImageAnalysis>("userId == $0", currentUser.id),
                    name = "Image Analysis sub",
                    updateExisting = true
                )
            }
            .errorHandler { _: SyncSession, error: SyncException ->
                Log.e("RealmSyncServiceImpl", "errorHandler: ", error)
            }
            .log(LogLevel.ALL)
            .build()
        realm = Realm.open(config)
        realmUser = currentUser
        Log.d(
            TAG,
            "Successfully opened synced realm: ${realm.configuration.name}"
        )
        CoroutineScope(Dispatchers.Main).launch {
            realm.subscriptions.waitForSynchronization()
        }
    }

    suspend fun deleteRealm() {
        realm.close()
        realmUser.logOut()
    }
}

@Module
@InstallIn(SingletonComponent::class)
object RealmServiceModule {
    @Singleton
    @Provides
    fun bindRealmAccountService(): RealmAccountService =
        RealmAccountServiceImpl()

    @Singleton
    @Provides
    fun bindRealmSyncService(): RealmSyncService =
        RealmSyncServiceImpl()
}
